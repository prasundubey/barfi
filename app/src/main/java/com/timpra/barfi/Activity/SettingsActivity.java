package com.timpra.barfi.Activity;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import io.apptik.widget.MultiSlider;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.fluidslider.FluidSlider;
import com.timpra.barfi.Objects.UserObject;
import com.timpra.barfi.Login.AuthenticationActivity;
import com.timpra.barfi.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity that control the search settings of the user:
 *  -Search interest
 *  -Search Distance
 *
 *  Also has option to log out the user
 */
public class SettingsActivity extends AppCompatActivity {


    private TextView mPhone, mEmail;

    private SegmentedButtonGroup mRadioGroup;
    FluidSlider mSlider;
    private Button mLogOut;

    private TextView mDone;

    private TextView min,max;
    String ageMin = "20";
    String ageMax = "80";
    private MultiSlider multiSlider;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    UserObject mUser = new UserObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mRadioGroup = findViewById(R.id.radioRealButtonGroup);
        mSlider = findViewById(R.id.fluidSlider);
        mLogOut = findViewById(R.id.logOut);
        mPhone = findViewById(R.id.phone);
        mEmail = findViewById(R.id.email);

        mDone = findViewById(R.id.done);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());



        min = (TextView) findViewById(R.id.minValue5);
        max = (TextView) findViewById(R.id.maxValue5);



        multiSlider = (MultiSlider)findViewById(R.id.range_slider5);

        /*min.setText(String.valueOf(multiSlider.getThumb(0).getValue()));
        max.setText(String.valueOf(multiSlider.getThumb(1).getValue()));*/

        multiSlider.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider,
                                       MultiSlider.Thumb thumb,
                                       int thumbIndex,
                                       int value)
            {
                if (thumbIndex == 0) {
                    min.setText(String.valueOf(value));
                    ageMin = String.valueOf(value);
                } else {
                    max.setText(String.valueOf(value));
                    ageMax = String.valueOf(value);
                }
            }
        });

        mDone.setOnClickListener(view -> {
            saveUserInformation();
            super.onBackPressed();
        });


        getUserInfo();

        mLogOut.setOnClickListener(v -> logOut());

    }

    /**
     * Fetch user search settings and populates elements
     */
    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUser.parseObject(dataSnapshot);

                if (!mUser.getPhone().equals("")) {
                    mPhone.setText(mUser.getPhone());
                } else mPhone.setVisibility(View.GONE);


                if (!mUser.getEmail().equals("")) {
                    mEmail.setText(mUser.getEmail());
                } else mEmail.setVisibility(View.GONE);

                if (!mUser.getAgeMin().equals("")) {
                    min.setText(mUser.getAgeMin());
                    multiSlider.getThumb(0).setValue(Integer.parseInt(mUser.getAgeMin()));
                } else min.setVisibility(View.GONE);

                if (!mUser.getAgeMax().equals("")) {
                    max.setText(mUser.getAgeMax());
                    multiSlider.getThumb(1).setValue(Integer.parseInt(mUser.getAgeMax()));
                } else max.setVisibility(View.GONE);



                mSlider.setPosition(mUser.getSearchDistance() / 100);

                switch(mUser.getInterest()){
                    case "Male":
                        mRadioGroup.setPosition(0, false);
                        break;
                    case "Female":
                        mRadioGroup.setPosition(1, false);
                        break;
                    default:
                        mRadioGroup.setPosition(2, false);
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Saves user search settings to the database
     */
    private void saveUserInformation() {
        String interest = "Both";


        switch(mRadioGroup.getPosition()){
            case 0:
                interest = "Male";
                break;
            case 1:
                interest = "Female";
                break;
            case 2:
                interest = "Both";
                break;
        }



        Map userInfo = new HashMap();
        userInfo.put("interest", interest);
        userInfo.put("ageMin", ageMin);
        userInfo.put("ageMax", ageMax);
        userInfo.put("search_distance", Math.round(mSlider.getPosition() * 100));
        mUserDatabase.updateChildren(userInfo);

    }

    /**
     * Logs out user and takes it to the AuthenticationActivity
     */
    private void logOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SettingsActivity.this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveUserInformation();
        finish();
        return false;
    }

}
