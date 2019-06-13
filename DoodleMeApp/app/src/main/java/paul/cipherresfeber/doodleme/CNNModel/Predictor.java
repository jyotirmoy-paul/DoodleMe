package paul.cipherresfeber.doodleme.CNNModel;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

import paul.cipherresfeber.doodleme.CustomData.LabelProbability;
import paul.cipherresfeber.doodleme.MainActivity;
import paul.cipherresfeber.doodleme.Utility.ProbabilitySorter;

public class Predictor {

    private Context context;
    private String[] labels;
    private Interpreter tflite;

    // initialize the model here
    public Predictor(String tensorflowLiteModelName, String labelFileName,Context context){
        this.context = context;
        // load the tflite model
        try{
            tflite = new Interpreter(loadModel(tensorflowLiteModelName));
        } catch (Exception e){
            e.printStackTrace();
        }
        // read the labels
        labels = getLabels(labelFileName);
    }

    // predict method --> returns the top n predictions
    public void predict(Bitmap rawBitmap, final int numberOfTopPredictions){

        // array for storing result returned by tflite model
        // 2D array with size 1 x MODEL_OUTPUT_SIZE
        final float[][] predictions = new float[1][labels.length];

        final ArrayList<LabelProbability> list = new ArrayList<>();

        final ArrayList<LabelProbability> topPredictions = new ArrayList<>();

        rawBitmap = Bitmap.createScaledBitmap(rawBitmap, 840,840,false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rawBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        Glide.with(context)
                .asBitmap()
                .load(stream.toByteArray())
                .override(28,28)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {

                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();

                        int[] pixels = new int[width * height];
                        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

                        float[] modelInputPixel = new float[pixels.length];

                        // turning white pixels to 1.0
                        for(int i=0; i<pixels.length; i++){
                            modelInputPixel[i] = (float)(0xff&pixels[i])/255;
                        }

                        // anything other than white is converted to 0 (black)
                        for(int i=0; i<pixels.length; i++){
                            if(modelInputPixel[i] != 1.0)
                                modelInputPixel[i] = 0;
                        }


                        save("arr = " + Arrays.toString(modelInputPixel) + "\n\n");

                        // get output from tflite model
                        tflite.run(reshapeToFourDimension(modelInputPixel, 28), predictions);

                        for(int i=0; i<labels.length; i++){
                            list.add(new LabelProbability(predictions[0][i], labels[i]));
                        }

                        // sorting labels according to highest prediction values
                        Collections.sort(list, new ProbabilitySorter());

                        for(int i=0; i<numberOfTopPredictions; i++){
                            topPredictions.add(list.get(i));
                        }


                        PredictionListener predictionListener = new MainActivity();
                        predictionListener.Predicted(topPredictions);

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    // well, numpy.reshape is pretty easy, but the below method is just
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

    // read the labels from the asset folder
    private String[] getLabels(String fileName){

        try{
            BufferedReader abc = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(fileName)));
            List<String> lines = new ArrayList<>();
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

    // load the tflite model from asset folder
    private MappedByteBuffer loadModel(String fileName) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(fileName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // write text to file
    public void save(String text) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = "try.py";
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

    public String[] getLabels() {
        return labels;
    }
}
