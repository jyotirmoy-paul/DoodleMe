package paul.cipherresfeber.doodleme;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import paul.cipherresfeber.doodleme.CNNModel.Predictor;
import paul.cipherresfeber.doodleme.Fragments.NameViewFragment;
import paul.cipherresfeber.doodleme.Views.DrawModel;
import paul.cipherresfeber.doodleme.Views.DrawingCanvas;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private DrawingCanvas drawingCanvas;
    private DrawModel drawModel;
    private PointF mTmpPoint = new PointF();
    private float mLastX;
    private float mLastY;

    private Predictor predictor;

    private ArrayList<String> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // method to initialize the canvas drawing and our cnn model
        initializeCanvasAndModel();

        // the button to clear out the canvas
        Button buttonClearDrawingCanvas = findViewById(R.id.btnClearDrawingCanvas);
        buttonClearDrawingCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawModel.clear();
                drawingCanvas.reset();
                drawingCanvas.invalidate();
            }
        });

        // at this point predictor object has been initialized, read the labels and choose 5 samples
        ArrayList<String> labels = predictor.getLabels();
        Collections.shuffle(labels); // shuffle the list
        questions = new ArrayList<>();
        for(int i=0; i<5; i++)
            questions.add(labels.get(i));

        // now show the nameViewFragment
        NameViewFragment fragment = new NameViewFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top,
                R.anim.enter_from_top, R.anim.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragmentContainer, fragment, "NameViewFragment").commit();

        Toast.makeText(this,
                questions.toString(), Toast.LENGTH_SHORT).show();




//
//        Timer t = new Timer();
//        t.scheduleAtFixedRate(new TimerTask(){
//            @Override
//            public void run() {
//                predictor.predict(drawingCanvas.getDrawnBitmap(), 3);
//            }
//        }, 1000 /* start the execution after a second*/ , 1500 /* call after every 500ms */ );

    }



    @SuppressLint("ClickableViewAccessibility")
    private void initializeCanvasAndModel(){
        // instantiate the predictor
        predictor = new Predictor("cnn-model.tflite",
                "labels.txt", MainActivity.this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        drawingCanvas = findViewById(R.id.drawingCanvas);
        drawModel = new DrawModel(metrics.widthPixels, (int)((double)metrics.heightPixels*0.70));
        drawingCanvas.setModel(drawModel);
        drawingCanvas.setOnTouchListener(this);
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

    protected void onResume() {
        drawingCanvas.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        drawingCanvas.onPause();
        super.onPause();
    }
}
