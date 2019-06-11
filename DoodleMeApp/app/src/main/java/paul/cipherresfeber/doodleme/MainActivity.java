package paul.cipherresfeber.doodleme;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import paul.cipherresfeber.doodleme.CustomData.LabelProbability;
import paul.cipherresfeber.doodleme.Utility.ProbabilitySorter;
import paul.cipherresfeber.doodleme.Views.DrawModel;
import paul.cipherresfeber.doodleme.Views.DrawingCanvas;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener{

    private DrawingCanvas drawingCanvas;
    private DrawModel drawModel;
    private PointF mTmpPoint = new PointF();

    private float mLastX;
    private float mLastY;

    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            tflite = new Interpreter(loadModelFile());
        } catch (Exception e){
            e.printStackTrace();
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        drawingCanvas = findViewById(R.id.drawingCanvas);
        drawModel = new DrawModel(480, 480);
        drawingCanvas.setModel(drawModel, metrics);
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

                int MODEL_IMAGE_INPUT_SIZE = 280;

                float[] pixelData = drawingCanvas.getPixelData(MODEL_IMAGE_INPUT_SIZE);

                save(Arrays.toString(pixelData));

                // modelPrediction array contains the probability of all labels
                float[][] modelPrediction = new float[1][88];

                tflite.run(reshapeToFourDimension(pixelData, MODEL_IMAGE_INPUT_SIZE), modelPrediction);

                // finding the top 3 probability as predicted by the model
                String[] labels = getLabels();
                if(labels == null){
                    Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<LabelProbability> list = new ArrayList<>();

                for(int i=0; i<labels.length; i++){
                    list.add(new LabelProbability(modelPrediction[0][i], labels[i]));
                }

                Collections.sort(list, new ProbabilitySorter());

                ArrayList<LabelProbability> topThreePredictions = new ArrayList<>();
                topThreePredictions.add(list.get(0));
                topThreePredictions.add(list.get(1));
                topThreePredictions.add(list.get(2));
                topThreePredictions.add(list.get(3));
                topThreePredictions.add(list.get(4));

                Log.i("PREDCITIONS: ", topThreePredictions.toString());


            }
        });

    }

    // sort the


    // read the labels from the asset folder
    private String[] getLabels(){

        try{
            BufferedReader abc = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            List<String> lines = new ArrayList<String>();
            String line;
            while((line = abc.readLine()) != null) {
                lines.add(line);
            }
            abc.close();
            // finally return the string array
            return lines.toArray(new String[]{});
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // well, numpy.reshape is pretty easy, but below is just
    // the implementation for converting a n-dimensional array to 1 x sqrt(n) x sqrt(n) x 1 dimension
    private float[][][][] reshapeToFourDimension(float[] arr,int size){
        float[][][][] newArr = new float[1][size][size][1];
        int k=0;
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                newArr[0][i][j][0] = arr[k];
                k++;
            }
        }
        return newArr;
    }


    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("cnn_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // write text to file
    public void save(String text) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "file.txt";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(text.getBytes());
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

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
