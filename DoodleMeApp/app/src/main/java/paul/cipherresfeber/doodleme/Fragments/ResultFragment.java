package paul.cipherresfeber.doodleme.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import paul.cipherresfeber.doodleme.CustomData.ResultData;
import paul.cipherresfeber.doodleme.R;
import paul.cipherresfeber.doodleme.Utility.Constants;

public class ResultFragment extends Fragment {

    private ArrayList<ResultData> resultData;

    public static ResultFragment newInstance(ArrayList<ResultData> resultData) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.RESULT_DATA, resultData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            resultData = (ArrayList<ResultData>) getArguments().getSerializable(Constants.RESULT_DATA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_landing, container, false);

        return view;
    }
}
