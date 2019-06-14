package paul.cipherresfeber.doodleme;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import paul.cipherresfeber.doodleme.CustomData.ResultData;
import paul.cipherresfeber.doodleme.Fragments.DoodleDrawingFragment;
import paul.cipherresfeber.doodleme.Fragments.ResultFragment;
import paul.cipherresfeber.doodleme.Utility.Constants;
import paul.cipherresfeber.doodleme.Utility.DoodleDrawingKeeper;

public class MainActivity extends AppCompatActivity implements DoodleDrawingKeeper {

    TextView textViewDoodleName;

    private ArrayList<String> questions;
    private ArrayList<ResultData> resultData;

    private int questionNumber;
    private String doodleName;

    private DoodleDrawingFragment fragment;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultData = new ArrayList<>();

        // randomly choose 5 samples
        ArrayList<String> labels = getLabels(Constants.MODEL_LABEL_FILE_NAME);
        Collections.shuffle(labels); // shuffle the list
        questions = new ArrayList<>();
        for(int i=0; i<5; i++)
            questions.add(labels.get(i));


        // show the first doodle name
        textViewDoodleName = findViewById(R.id.txvDoodleName);
        questionNumber = 0;
        doodleName = questions.get(questionNumber);
        textViewDoodleName.setText(doodleName);


        Button buttonStart = findViewById(R.id.btnStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragment = DoodleDrawingFragment.newInstance(doodleName, MainActivity.this);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top,
                        R.anim.enter_from_top, R.anim.exit_to_top);
                transaction.addToBackStack(null);
                transaction.add(R.id.fragmentContainer, fragment, "DoodleDrawingFragment").commit();


            }
        });

    }

    @Override
    public void keepResult(String doodleName, boolean couldGuess, Bitmap userDrawing) {
        // save the result
        resultData.add(new ResultData(doodleName, couldGuess, userDrawing));

        questionNumber++; // increment the questionNumber by 1

        if(questionNumber < questions.size()){
            // if more questions are left, fetch the new one
            doodleName = questions.get(questionNumber);
            // update the view
            textViewDoodleName.setText(doodleName);
        } else{
            textViewDoodleName.setText("");
            // the game is finished, open result fragment
            ResultFragment f = new ResultFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top,
                    R.anim.enter_from_top, R.anim.exit_to_top);
            transaction.addToBackStack(null);
            transaction.add(R.id.fragmentContainer, f, "ResultFragment").commit();
        }

    }

    // read the labels from the asset folder
    private ArrayList<String> getLabels(String fileName){

        try{
            BufferedReader abc = new BufferedReader(
                    new InputStreamReader(getAssets().open(fileName)));
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while((line = abc.readLine()) != null) {
                lines.add(line);
            }
            abc.close();
            return lines;
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onBackPressed() {
        if(fragment.isVisible())
            fragment.onBackPressed();
        else
            super.onBackPressed();
    }
}
