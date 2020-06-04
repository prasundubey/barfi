package com.timpra.barfi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.timpra.barfi.Activity.MainActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class InfoFragment5 extends Fragment {


    public InfoFragment5() {
        // Required empty public constructor
    }

    private TextView mSet, mText;
    private ImageView mImage;
    private Button mStart;
    View view;

    Dialog mSwipeDialog;

    private ArrayAdapter <String> adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_fragment_5, container, false);

        mSwipeDialog = new Dialog(getActivity());

        mSet = (TextView) view.findViewById(R.id.set);
        mText = (TextView) view.findViewById(R.id.text);
        mImage = (ImageView) view.findViewById(R.id.image);


        mStart = (Button) view.findViewById(R.id.start);


        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwipePopup(view);

            }
        });


        return view;
    }

    public void SwipePopup(View v) {
        Button mChat;
        mSwipeDialog.setContentView(R.layout.swipe_popup);
        mChat = (Button) mSwipeDialog.findViewById(R.id.gochat);
        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                mSwipeDialog.dismiss();
            }
        });

        mSwipeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        mSwipeDialog.show();



    }


}
