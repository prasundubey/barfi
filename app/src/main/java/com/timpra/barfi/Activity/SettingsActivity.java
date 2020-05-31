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

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());



        final TextView min5 = (TextView) findViewById(R.id.minValue5);
        final TextView max5 = (TextView) findViewById(R.id.maxValue5);



        MultiSlider multiSlider5 = (MultiSlider)findViewById(R.id.range_slider5);

        min5.setText(String.valueOf(multiSlider5.getThumb(0).getValue()));
        max5.setText(String.valueOf(multiSlider5.getThumb(1).getValue()));

        multiSlider5.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider,
                                       MultiSlider.Thumb thumb,
                                       int thumbIndex,
                                       int value)
            {
                if (thumbIndex == 0) {
                    min5.setText(String.valueOf(value));
                } else {
                    max5.setText(String.valueOf(value));
                }
            }
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
