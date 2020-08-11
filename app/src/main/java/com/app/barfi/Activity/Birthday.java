package com.app.barfi.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.app.barfi.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Birthday extends AppCompatActivity {

    private static final String TAG = "Birthday";
    private TextView mDisplayDate;
    private FirebaseAuth mAuth;
    private String mUserId;
    private DatabaseReference mUserDatabase;
    private Long mDate;
    public String mAge;


    private TextView mBirthday;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Integer birthYear, birthDay;
    private int age;

    private Button mConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);

        mBirthday = (TextView) findViewById(R.id.birthday);
        mConfirm = (Button) findViewById(R.id.confirm);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                Calendar twenty1 = (Calendar) cal.clone();
                twenty1.add(Calendar.YEAR, -21);



                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialog = new DatePickerDialog(Birthday.this,
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

                mConfirm.setEnabled(true);


            }

        };




        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
                Intent intent = new Intent(Birthday.this, MainActivity.class);
                startActivity(intent);
                finish();
               // return;
            }
        });

    }


    private void saveInfo(){
        Map userBday = new HashMap();
        userBday.put("year", birthYear);
        userBday.put("day", birthDay );
        mUserDatabase.child("dob").updateChildren(userBday);
        mUserDatabase.child("age").setValue(age);
    }


}
