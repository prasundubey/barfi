package com.timpra.barfi;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class InfoFragment3 extends Fragment {


    public InfoFragment3() {
        // Required empty public constructor
    }




    EditText
            mAbout,
            mJob,
            mCompany,
            mSchool;
    Button mNext3;
    private TextView mDegree, mTown;


    private FirebaseAuth mAuth;
    public String mAge;
    private String mUserId;
    private int age;

    View view;


    private ArrayAdapter <String> adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_fragment_3, container, false);



        mTown = (TextView) view.findViewById(R.id.homeTown);
        mJob = (EditText) view.findViewById(R.id.jobTitle);
        mCompany = (EditText) view.findViewById(R.id.company);
        mSchool = (EditText) view.findViewById(R.id.school);
        mAbout = (EditText) view.findViewById(R.id.bio);
        mDegree = (TextView) view.findViewById(R.id.degree);

        mNext3 = (Button) view.findViewById(R.id.next3);




        mTown.setOnClickListener(k -> {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    View row = getLayoutInflater().inflate(R.layout.row_items, null);

                    ListView l1 = (ListView) row.findViewById(R.id.listView);
                    String[] names = {"Patna", "Bettiah", "Bhagalpur", "Smastipur", "Rosra"};

                    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, names);
                    l1.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = (String) parent.getItemAtPosition(position);
                            mTown.setText(selectedItem);
                        }
                    });

                    builder1.setView(row);
                    AlertDialog dialog = builder1.create();
                    dialog.show();



        });





        mDegree.setOnClickListener(v -> {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
           /*
            View view1 = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog, null);
            TextView title = (TextView) view1.findViewById(R.id.title);
            ImageButton imageButton = (ImageButton) view1.findViewById(R.id.image);
            title.setText("Education level");
            imageButton.setImageResource(R.drawable.ic_school);*/
           // TextView title = new TextView(getContext());
            // title.setText("Whats you degreee mf");
            //title.setPadding(20, 100, 20, 20);   // Set Position
           // title.setGravity(Gravity.CENTER);
            //title.setTextColor(Color.RED);
           // title.setTextSize(25);
            builder.setTitle("Whats you degreee mf");
            builder.setIcon(R.drawable.ic_school);



        String[] deg = {"High school diploma", "In college", "Undergraduate", "In grad school", "Graduate"};

        builder.setItems(deg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedItem = Arrays.asList(deg).get(which);
                mDegree.setText(selectedItem);
            }

        });

            // add dont disclose button
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDegree.setText(null);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            /*builder.setView(view1);
            builder.show();*/

        });




        mNext3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.infoContainer, new InfoFragment4());
                fr.commit();
            }
        });


        return view;
    }



    private void saveUserInformation() {

        final String town = mTown.getText().toString();
        final String jobTitle = mJob.getText().toString();
        final String company = mCompany.getText().toString();
        final String school = mSchool.getText().toString();
        final String degree = mDegree.getText().toString();
        final String about = mAbout.getText().toString();






        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map userInfo = new HashMap();
        userInfo.put("town", town);
        userInfo.put("jobTitle", jobTitle);
        userInfo.put("company", company);
        userInfo.put("job", jobTitle + " at " + company);
        userInfo.put("school", school);
        userInfo.put("degree", degree);
        userInfo.put("about", about);

        FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("details").updateChildren(userInfo);




        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();


    }


}
