package com.timpra.barfi;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class InfoFragment1 extends Fragment {

    private static final String TAG = "Birthday";
    EditText mName,
            mEmail;
    Button mNext;
    private TextView mBirthday;
    SegmentedButtonGroup mRadioGroup;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseAuth mAuth;
    public String mAge;
    private String mUserId;
    private int age;

    View view;

    public InfoFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_fragment_1, container, false);



        mEmail = (EditText) view.findViewById(R.id.email);
        mName = (EditText) view.findViewById(R.id.name);
        mBirthday = (TextView) view.findViewById(R.id.birthday);
        mNext = (Button) view.findViewById(R.id.next);
        mRadioGroup = (SegmentedButtonGroup) view.findViewById(R.id.radioRealButtonGroup);

        mBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                Calendar twenty1 = (Calendar) cal.clone();
                twenty1.add(Calendar.YEAR, -18);



                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);

                dialog.getDatePicker().setMaxDate(twenty1.getTimeInMillis());

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = day + "/" + month  + "/" + year;
                mBirthday.setText(date);

                Calendar dob = Calendar.getInstance();
                Calendar today = Calendar.getInstance();

                dob.set(year, month, day);

                age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

                if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }

                mAge = Integer.toString(age);
            }

        };


        mEmail.addTextChangedListener(nextTextWatcher);
        mName.addTextChangedListener(nextTextWatcher);
        mBirthday.addTextChangedListener(nextTextWatcher);


        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.infoContainer, new InfoFragment2());
                fr.commit();
            }
        });


        return view;
    }


    private TextWatcher nextTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            final String name = mName.getText().toString();
            final String email = mEmail.getText().toString();
            final String bday = mBirthday.getText().toString();

            mNext.setEnabled(!name.isEmpty() && !email.isEmpty() && !bday.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void saveUserInformation() {

        final String name = mName.getText().toString();
        final String email = mEmail.getText().toString();
        final String accountType;
        int selectId = mRadioGroup.getPosition();

        switch (selectId) {
            case 1:
                accountType = "Female";
                break;
            default:
                accountType = "Male";
        }


        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("sex", accountType);
        userInfo.put("email", email);
        userInfo.put("search_distance", 100);

        switch(accountType){
            case "Male":
                userInfo.put("interest", "Female");
                break;
            case "Female":
                userInfo.put("interest", "Male");
                break;
        }
        FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).updateChildren(userInfo);




        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("age").setValue(age);


    }


}
