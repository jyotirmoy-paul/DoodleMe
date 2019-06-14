package paul.cipherresfeber.doodleme;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import paul.cipherresfeber.doodleme.Fragments.DoodleDrawingFragment;
import paul.cipherresfeber.doodleme.Utility.Constants;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> questions;

    private DoodleDrawingFragment fragment;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // randomly choose 5 samples
        ArrayList<String> labels = getLabels(Constants.MODEL_LABEL_FILE_NAME);
        Collections.shuffle(labels); // shuffle the list
        questions = new ArrayList<>();
        for(int i=0; i<5; i++)
            questions.add(labels.get(i));


        fragment = DoodleDrawingFragment.newInstance(questions.get(0));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_top,
                R.anim.enter_from_top, R.anim.exit_to_top);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragmentContainer, fragment, "DoodleDrawingFragment").commit();



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
