package paul.cipherresfeber.doodleme.Fragments;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
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
import java.util.Locale;
import java.util.Random;

import paul.cipherresfeber.doodleme.CNNModel.PredictionListener;
import paul.cipherresfeber.doodleme.CNNModel.Predictor;
import paul.cipherresfeber.doodleme.CustomData.LabelProbability;
import paul.cipherresfeber.doodleme.R;
import paul.cipherresfeber.doodleme.Utility.Constants;
import paul.cipherresfeber.doodleme.Views.DrawModel;
import paul.cipherresfeber.doodleme.Views.DrawingCanvas;

public class DoodleDrawingFragment extends Fragment implements View.OnTouchListener, PredictionListener {

    private TextToSpeech speechEngine;
    private String doodleName;
    private boolean stopPredicting;
    private final float MIN_THRESHOLD_PROBABILITY = (float) 0.30;
    private final float MIN_THRESHOLD_PROBABILITY_FOR_DETECTION = (float) 0.10;

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

        // initialize the speech engine
        speechEngine = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = speechEngine.setLanguage(Locale.getDefault());
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(getContext(),
                                "Speech not supported", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        speechEngine.setSpeechRate(0.9f);
        speechEngine.setPitch(1f);

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

        Toast.makeText(getContext(),
                doodleName, Toast.LENGTH_SHORT).show();

        return view;
    }

    // after a prediction is made this method is invoked
    @Override
    public void predictionCallback(ArrayList<LabelProbability> topPredictions) {

        for(int i=0; i<topPredictions.size(); i++){
            if(topPredictions.get(i).getLabelName().equals(doodleName) &&
                    topPredictions.get(i).getProbability() > MIN_THRESHOLD_PROBABILITY){
                // then the user has correctly drawn the doodle
                handleSuccess(doodleName);
            }
        }

        handleFailure(topPredictions);

    }

    // if the doodle is correctly drawn, this method is invoked
    private void handleSuccess(String doodleName){
        stopPredicting = true; // the model won't predict anything now

        String[] precedingSpeech = {
                "Oh I know! It's ",
                "Gotcha, it's ",
                "I got it. It's a nice ",
                "I got it, it's "
        };

        String finalSpeech = precedingSpeech[new Random().nextInt(precedingSpeech.length)] +
                join(doodleName.split("_"), " ");

        speechEngine.speak(
                finalSpeech,
                TextToSpeech.QUEUE_FLUSH, null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top)
                        .remove(DoodleDrawingFragment.this).commit();
            }
        }, 2000); // wait for 2 second before quiting

    }

    // if the drawn doodle is not correct, this method is invoked
    private void handleFailure(ArrayList<LabelProbability> predictions){

        String[] precedingSpeech = {
                "Well, I see ",
                "I can see ",
                "I guess it's "
        };

        StringBuilder builder = new StringBuilder();
        for(int i=0; i<predictions.size(); i++){
            if(predictions.get(i).getProbability() > MIN_THRESHOLD_PROBABILITY_FOR_DETECTION){
                builder.append(join(predictions.get(i).getLabelName().split("_"), " "))
                        .append(", or ");
            }
        }

        if(!predictions.isEmpty()){
            speechEngine.speak(precedingSpeech[new Random().nextInt(precedingSpeech.length)] +
                    builder.subSequence(0,builder.length()-5).toString(), TextToSpeech.QUEUE_ADD, null);
        } else{

            String[] couldNotGuessDrawingSpeech = {
                    "I have no idea what you are drawing!",
                    "What is this? No clue!",
                    "Well I am paranoid by your drawing!"
            };

            speechEngine.speak(couldNotGuessDrawingSpeech[new Random().nextInt(couldNotGuessDrawingSpeech.length)],
                    TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    public static String join(String[] arr, String separator) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = arr.length; i < il; i++) {
            if (i > 0)
                sbStr.append(separator);
            sbStr.append(arr[i]);
        }
        return sbStr.toString();
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

        // try to predict drawing after the user has lifted his/her finger
        if(!stopPredicting)
            predictor.predict(drawingCanvas.getBitmap(), 5 /* get the top 5 predictions*/ );
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
