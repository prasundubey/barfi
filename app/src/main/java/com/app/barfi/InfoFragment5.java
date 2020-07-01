package com.app.barfi;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.barfi.Activity.MainActivity;

import androidx.fragment.app.Fragment;


public class InfoFragment5 extends Fragment {


    public InfoFragment5() {
        // Required empty public constructor
    }

    private TextView mSet, mText;
    private ImageView mImage;
    private Button mStart;
    View view;

    //Dialog mSwipeDialog;

    private ArrayAdapter <String> adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_fragment_5, container, false);

        //mSwipeDialog = new Dialog(getActivity());

        mSet = (TextView) view.findViewById(R.id.set);
        mText = (TextView) view.findViewById(R.id.text);
        mImage = (ImageView) view.findViewById(R.id.image);


        mStart = (Button) view.findViewById(R.id.start);


        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });


        return view;
    }


}
