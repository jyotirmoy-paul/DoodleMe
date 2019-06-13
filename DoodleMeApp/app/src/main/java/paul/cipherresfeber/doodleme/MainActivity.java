package paul.cipherresfeber.doodleme;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import paul.cipherresfeber.doodleme.CNNModel.PredictionListener;
import paul.cipherresfeber.doodleme.CNNModel.Predictor;
import paul.cipherresfeber.doodleme.CustomData.LabelProbability;
import paul.cipherresfeber.doodleme.Views.DrawModel;
import paul.cipherresfeber.doodleme.Views.DrawingCanvas;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, PredictionListener {

    private DrawingCanvas drawingCanvas;
    private DrawModel drawModel;
    private PointF mTmpPoint = new PointF();

    private float mLastX;
    private float mLastY;

    private final int MODEL_IMAGE_INPUT_SIZE = 28;

    Predictor predictor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiate the predictor
        predictor = new Predictor("cnn-model.tflite",
                "labels.txt", MainActivity.this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        drawingCanvas = findViewById(R.id.drawingCanvas);
        drawModel = new DrawModel(metrics.widthPixels, (int)((double)metrics.heightPixels*0.70));
        drawingCanvas.setModel(drawModel);
        drawingCanvas.setOnTouchListener(this);

        Button buttonClearDrawingCanvas = findViewById(R.id.btnClearDrawingCanvas);
        buttonClearDrawingCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawModel.clear();
                drawingCanvas.reset();
                drawingCanvas.invalidate();
            }
        });

        Button buttonGetPixel = findViewById(R.id.btnGetPixel);
        buttonGetPixel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                predictor.predict(drawingCanvas.getDrawnBitmap(),3);

            }
        });

    }

    // this method is called every time a new doodle is predicted
    @Override
    public void Predicted(ArrayList<LabelProbability> predictions) {

    }


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
    public void onClick(View v) {

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
