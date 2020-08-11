package com.app.barfi.Activity;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import io.apptik.widget.MultiSlider;

import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.fluidslider.FluidSlider;
import com.app.barfi.Objects.UserObject;
import com.app.barfi.Login.AuthenticationActivity;
import com.app.barfi.R;
import com.skyfishjy.library.RippleBackground;

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

    private static final String TAG = "Yo";

    private TextView mPhone, mEmail;

    private SegmentedButtonGroup mRadioGroup;
    FluidSlider mSlider;
    private Button mLogOut;
    private TextView mDelete;

    private TextView mDone;

    private Switch mSwitch;

    private TextView min,max;
    String ageMin = "20";
    String ageMax = "80";
    private MultiSlider multiSlider;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    UserObject mUser = new UserObject();

    private LinearLayout mPgs;

    private Integer searchDistance=100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mPgs = findViewById(R.id.pgsLL);
        mPgs.setVisibility(View.VISIBLE);


        mRadioGroup = findViewById(R.id.radioRealButtonGroup);
        mSlider = findViewById(R.id.fluidSlider);
        mPhone = findViewById(R.id.phone);
        mEmail = findViewById(R.id.email);

        mLogOut = findViewById(R.id.logOut);
        mDelete = findViewById(R.id.delete);

        mDone = findViewById(R.id.done);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        mSwitch = (Switch) findViewById(R.id.switch1);

        min = (TextView) findViewById(R.id.minValue5);
        max = (TextView) findViewById(R.id.maxValue5);


        multiSlider = (MultiSlider)findViewById(R.id.range_slider5);

        /*min.setText(String.valueOf(multiSlider.getThumb(0).getValue()));
        max.setText(String.valueOf(multiSlider.getThumb(1).getValue()));*/


        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSlider.setVisibility(View.GONE);
                    searchDistance = 2000;
                    /*Map userInfo1 = new HashMap();
                    userInfo1.put("search_distance", 2000);
                    mUserDatabase.child("filters").updateChildren(userInfo1);*/

                } else {
                    mSlider.setVisibility(View.VISIBLE);
                    searchDistance = 100;
                    /*Map userInfo1 = new HashMap();
                    userInfo1.put("search_distance", 100);
                    mUserDatabase.child("filters").updateChildren(userInfo1);*/
                }
            }
        });


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
            finish();
        });


        getUserInfo();

        mLogOut.setOnClickListener(v -> logOut());
        mDelete.setOnClickListener(v -> deleteUser());

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


                if (mUser.getSearchDistance()==2000){
                    mSlider.setVisibility(View.GONE);
                    mSwitch.setChecked(true);
                }else{
                    mSlider.setVisibility(View.VISIBLE);
                    mSwitch.setChecked(false);
                }

                mSlider.setPosition(mUser.getSearchDistance() / 100);

                switch(mUser.getInterest()){
                    case "Male":
                        mRadioGroup.setPosition(0, false);
                        break;
                    case "Female":
                        mRadioGroup.setPosition(1, false);
                        break;
                    case "Others":
                        mRadioGroup.setPosition(2, false);
                        break;
                    default:
                        mRadioGroup.setPosition(3, false);
                        break;
                }

                mPgs.setVisibility(View.GONE);

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
        String interest = "All";


        switch(mRadioGroup.getPosition()){
            case 0:
                interest = "Male";
                break;
            case 1:
                interest = "Female";
                break;
            case 2:
                interest = "Others";
                break;
            case 3:
                interest = "All";
                break;
        }



        Map userInfo = new HashMap();
        userInfo.put("interest", interest);
        userInfo.put("ageMin", ageMin);
        userInfo.put("ageMax", ageMax);
        if (!mSwitch.isChecked())
        userInfo.put("search_distance", Math.round(mSlider.getPosition() * 100));
        else userInfo.put("search_distance", searchDistance);


        mUserDatabase.child("filters").updateChildren(userInfo);

    }

    /**
     * Logs out user and takes it to the AuthenticationActivity
     */
    private void logOut(){
        // saveUserInformation();

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SettingsActivity.this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void deleteUser(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setMessage( Html.fromHtml("<font color='#2d2d2d'>Deleting account will remove all the profile information, connections & chats forever. </font>"));
        builder.setTitle( Html.fromHtml("<font color='#2d2d2d'>Are you sure?</font>"));
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                //mUserDatabase.removeValue();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this, AuthenticationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                mUserDatabase.removeValue();
               // currentUser.delete();
                finish();


                /*currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"OK! Works fine!");
                            mUserDatabase.removeValue();
                            Intent intent = new Intent(SettingsActivity.this, AuthenticationActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG,"Something is wrong!");
                        }
                    }
                });*/
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // If user click no, then dialog box is canceled.
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveUserInformation();
        finish();
        return false;
    }

    @Override
    public void onBackPressed() {
        saveUserInformation();
        super.onBackPressed();
        finish();
      //  Toast.makeText(getApplicationContext(), "Settings updated", Toast.LENGTH_SHORT).show();
    }

}
