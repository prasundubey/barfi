package com.app.barfi;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app.barfi.Objects.UserObject;

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
    private DatabaseReference mUserDatabase;
    UserObject mUser = new UserObject();

    public String mAge;
    private String mUserId;
    private int age;

    private Integer birthYear, birthDay;


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



        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        getUserInfo();



        mBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                Calendar twenty1 = (Calendar) cal.clone();
                twenty1.add(Calendar.YEAR, -21);



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

                birthYear = dob.get(Calendar.YEAR);
                birthDay = dob.get(Calendar.DAY_OF_YEAR);

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


    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUser.parseObject(dataSnapshot);

                mName.setText(mUser.getName());
                // mBirthday.setText(mUser.get());
                mEmail.setText(mUser.getEmail());

                if(mUser.getUserSex().equals("Male"))
                    mRadioGroup.setPosition(0, false);
                if(mUser.getUserSex().equals("Female"))
                    mRadioGroup.setPosition(1, false);
                if(mUser.getUserSex().equals("Others"))
                    mRadioGroup.setPosition(2, false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }






    private void saveUserInformation() {

        final String phonenumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        final String name = mName.getText().toString();
        final String email = mEmail.getText().toString();
        final String accountType;
        int selectId = mRadioGroup.getPosition();


        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        switch (selectId) {

            case 2:
                accountType = "Others";
                break;
            case 1:
                accountType = "Female";
                break;
            default:
                accountType = "Male";
        }


        Map userInfo = new HashMap();

        userInfo.put("phone", phonenumber);

        userInfo.put("name", name);
        userInfo.put("fullName", name);
        userInfo.put("sex", accountType);
        userInfo.put("email", email);

        userInfo.put("age", age);
        userInfo.put("vc", versionCode);

        userInfo.put("score", 1);

        mUserDatabase.updateChildren(userInfo);

        Map userBday = new HashMap();
        userBday.put("year", birthYear);
        userBday.put("day", birthDay );
        mUserDatabase.child("dob").updateChildren(userBday);



        // Filters
        //age range

        Map userInfo1 = new HashMap();
        userInfo1.put("search_distance", 2000);
        switch(accountType){
            case "Male":
                userInfo1.put("ageMax", age+3);
                userInfo1.put("ageMin", age-8);
                break;
            case "Female":
                userInfo1.put("ageMax", age+8);
                userInfo1.put("ageMin", age-2);
                break;
        }

        switch(accountType){
            case "Male":
                userInfo1.put("interest", "Female");
                break;
            case "Female":
                userInfo1.put("interest", "Male");
                break;
            case "Others":
                userInfo1.put("interest", "Others");
                break;
        }

        mUserDatabase.child("filters").updateChildren(userInfo1);

        //add account level
        Map userInfo2 = new HashMap();
        userInfo2.put("level", "basic");
        mUserDatabase.child("status").updateChildren(userInfo2);



        /*mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("age").setValue(age);

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("vc").setValue(versionCode);
*/

    }


}

