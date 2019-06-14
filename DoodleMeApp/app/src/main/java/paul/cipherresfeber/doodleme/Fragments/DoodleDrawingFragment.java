package paul.cipherresfeber.doodleme.Fragments;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import paul.cipherresfeber.doodleme.CNNModel.PredictionListener;
import paul.cipherresfeber.doodleme.CNNModel.Predictor;
import paul.cipherresfeber.doodleme.CustomData.LabelProbability;
import paul.cipherresfeber.doodleme.R;
import paul.cipherresfeber.doodleme.Utility.Constants;
import paul.cipherresfeber.doodleme.Views.DrawModel;
import paul.cipherresfeber.doodleme.Views.DrawingCanvas;

public class DoodleDrawingFragment extends Fragment implements View.OnTouchListener, PredictionListener {

    private String doodleName;
    private ArrayList<LabelProbability> topPredictions;

    private DrawingCanvas drawingCanvas;
    private DrawModel drawModel;
    private PointF mTmpPoint = new PointF();
    private float mLastX;
    private float mLastY;

    private Predictor predictor;

    public static DoodleDrawingFragment newInstance(String doodleName) {
        DoodleDrawingFragment fragment = new DoodleDrawingFragment();
        Bundle args = new Bundle();
        args.putString(Constants.DOODLE_NAME, doodleName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doodleName = getArguments().getString(Constants.DOODLE_NAME);
        }

        // instantiate the predictor
        predictor = new Predictor(Constants.TFLITE_MODEL_NAME,
                Constants.MODEL_LABEL_FILE_NAME, getContext(), this);

        

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doodle_drawing, container, false);

        // method to initialize the canvas drawing and our cnn model
        initializeCanvas(view);

        // the button to clear out the canvas
        Button buttonClearDrawingCanvas = view.findViewById(R.id.btnClearDrawingCanvas);
        buttonClearDrawingCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawModel.clear();
                drawingCanvas.reset();
                drawingCanvas.invalidate();
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                predictor.predict(drawingCanvas.getBitmap(), 4);
            }
        }, 50, 1500 /* run after every 1.5s */);

        return view;
    }

    // after a prediction is made this method is invoked
    @Override
    public void PredictionCallback(ArrayList<LabelProbability> topPredictions) {

        Toast.makeText(getContext(),
                topPredictions.toString(), Toast.LENGTH_SHORT).show();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeCanvas(View view){

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        drawingCanvas = view.findViewById(R.id.drawingCanvas);

        drawModel = new DrawModel(metrics.widthPixels, (int)((double)metrics.heightPixels*0.70));

        drawingCanvas.setModel(drawModel);
        drawingCanvas.setOnTouchListener(DoodleDrawingFragment.this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_DOWN) {
            processTouchDown(event);
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            processTouchMove(event);
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            processTouchUp();
            return true;
        }
        return false;
    }

    private void processTouchDown(MotionEvent event) {
        mLastX = event.getX();
        mLastY = event.getY();
        drawingCanvas.calcPos(mLastX, mLastY, mTmpPoint);
        float lastConvX = mTmpPoint.x;
        float lastConvY = mTmpPoint.y;
        drawModel.startLine(lastConvX, lastConvY);
    }

    private void processTouchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        drawingCanvas.calcPos(x, y, mTmpPoint);
        float newConvX = mTmpPoint.x;
        float newConvY = mTmpPoint.y;
        drawModel.addLineElem(newConvX, newConvY);

        mLastX = x;
        mLastY = y;
        drawingCanvas.invalidate();
    }

    private void processTouchUp() {
        drawModel.endLine();
    }

    @Override
    public void onResume() {
        super.onResume();
        drawingCanvas.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        drawingCanvas.onPause();
    }

    // todo: do something else here
    public void onBackPressed(){
        Toast.makeText(getContext(),
                "NOT POSSIBLE", Toast.LENGTH_SHORT).show();
    }

}
