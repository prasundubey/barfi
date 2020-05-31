package com.timpra.barfi.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.timpra.barfi.Objects.UserObject;
import com.timpra.barfi.R;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity displayed when user clicks on a card
 *
 * It displays the user that was clicked on's information in detail
 */


public class ZoomCardUser extends AppCompatActivity {

    private TextView    mName;

    private TextView
            mJob,
            mAbout,
            mDegree,
            mSchool,
            mTown,
            mCity;

    private LinearLayout mButtonLayout;

    private Button mEditProfile;

    private ImageView mImage;
    private ImageView mImage1;
    private ImageView mImage3;

    private FirebaseAuth mAuth;
    UserObject mUser = new UserObject();
    private DatabaseReference mUserDatabase;

    private TextView
            mStatus,
            mReligion,
            mHeight,
            mZodiac,
            mDrinking,
            mSmoking,
            mExercise,
            mPets,
            mKids,
            mDiet,
            mPolitics,
            mReading;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom_card);



        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //age


                mUser.parseObject(dataSnapshot);

                mEditProfile = findViewById(R.id.edit_profile);

                mEditProfile.setOnClickListener(view -> {
                    Intent intent = new Intent(ZoomCardUser.this, EditProfileActivity.class);
                    startActivity(intent);
                });

                mButtonLayout = findViewById(R.id.buttonLayout);
                mButtonLayout.setVisibility(View.GONE);

                mName = findViewById(R.id.name);
                mImage = findViewById(R.id.image);
                mImage1 = findViewById(R.id.image1);
                mImage3 = findViewById(R.id.image3);


                mJob = findViewById(R.id.job);
                mAbout = findViewById(R.id.about);
                mDegree = findViewById(R.id.degree);
                mSchool = findViewById(R.id.school);
                mTown = findViewById(R.id.homeTown);
                mCity = findViewById(R.id.city);


                mName.setText(mUser.getName() + ", " + mUser.getAge());

                mStatus = (TextView) findViewById(R.id.status);
                mReligion = (TextView) findViewById(R.id.religion);
                mHeight = (TextView) findViewById(R.id.height);
                mZodiac = (TextView) findViewById(R.id.zodiac);
                mDrinking = (TextView) findViewById(R.id.drinking);
                mSmoking = (TextView) findViewById(R.id.smoking);
                mExercise = (TextView) findViewById(R.id.exercise);
                mPets = (TextView) findViewById(R.id.pets);
                mKids = (TextView) findViewById(R.id.kids);
                mDiet = (TextView) findViewById(R.id.diet);
                mPolitics = (TextView) findViewById(R.id.politics);
                mReading = (TextView) findViewById(R.id.reading);


                if (!mUser.getProfileImageUrl().equals("default"))
                    Glide.with(getApplicationContext()).load(mUser.getProfileImageUrl()).into(mImage);

                if (!mUser.getImageUrl().equals("default"))
                    Glide.with(getApplicationContext()).load(mUser.getImageUrl()).into(mImage1);
                else mImage1.setVisibility(View.GONE);

                if (!mUser.getImageUrl3().equals("default"))
                    Glide.with(getApplicationContext()).load(mUser.getImageUrl3()).into(mImage3);
                else mImage3.setVisibility(View.GONE);


                mAbout.setText(mUser.getAbout());

                if (!mUser.getJob().equals("")) {
                    mJob.setText(mUser.getJob());
                } else mJob.setVisibility(View.GONE);

                if (!mUser.getDegree().equals("")) {
                    mDegree.setText(mUser.getDegree());
                } else mDegree.setVisibility(View.GONE);

                if (!mUser.getSchool().equals("")) {
                    mSchool.setText(mUser.getSchool());
                } else mSchool.setVisibility(View.GONE);

                if (!mUser.getCity().equals("")) {
                    mCity.setText(mUser.getCity());
                } else mCity.setVisibility(View.GONE);


                if (!mUser.getTown().equals("")) {
                    mTown.setText("From " + mUser.getTown());
                } else mTown.setVisibility(View.GONE);


                // extraaa info

                if (!mUser.getStatus().equals("")) {
                    mStatus.setText(mUser.getStatus());
                } else mStatus.setVisibility(View.GONE);

                if (!mUser.getReligion().equals("")) {
                    mReligion.setText(mUser.getReligion());
                } else mReligion.setVisibility(View.GONE);

                if (!mUser.getHeight().equals("")) {
                    mHeight.setText(mUser.getHeight());
                } else mHeight.setVisibility(View.GONE);

                if (!mUser.getZodiac().equals("")) {
                    mZodiac.setText(mUser.getZodiac());
                } else mZodiac.setVisibility(View.GONE);

                if (!mUser.getDrinking().equals("")) {
                    mDrinking.setText(mUser.getDrinking());
                } else mDrinking.setVisibility(View.GONE);

                if (!mUser.getSmoking().equals("")) {
                    mSmoking.setText(mUser.getSmoking());
                } else mSmoking.setVisibility(View.GONE);

                if (!mUser.getExercise().equals("")) {
                    mExercise.setText(mUser.getExercise());
                } else mExercise.setVisibility(View.GONE);

                if (!mUser.getPets().equals("")) {
                    mPets.setText(mUser.getPets());
                } else mPets.setVisibility(View.GONE);

                if (!mUser.getKids().equals("")) {
                    mKids.setText(mUser.getKids());
                } else mKids.setVisibility(View.GONE);

                if (!mUser.getDiet().equals("")) {
                    mDiet.setText(mUser.getDiet());
                } else mDiet.setVisibility(View.GONE);

                if (!mUser.getPolitics().equals("")) {
                    mPolitics.setText(mUser.getPolitics());
                } else mPolitics.setVisibility(View.GONE);

                if (!mUser.getReading().equals("")) {
                    mReading.setText(mUser.getReading());
                } else mReading.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



}
