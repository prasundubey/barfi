package com.app.barfi.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.app.barfi.Fragments.CardFragment;
import com.app.barfi.Objects.UserObject;
import com.app.barfi.R;

/**
 * Activity displayed when user clicks on a card
 *
 * It displays the user that was clicked on's information in detail
 */


public class ZoomCardActivity extends AppCompatActivity {

    private TextView    mName;

    private TextView
            mJob,
            mAbout,
            mDegree,
            mSchool,
            mTown,
            mCity;

    private ImageView mImage;
    private ImageView mImage1;
    private ImageView mImage3;

    private ImageView mVerified;


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

    private Button mEditProfile;
    private ImageView mBack;

    private LinearLayout mButtonLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom_card);

        mEditProfile = findViewById(R.id.edit_profile);
        mEditProfile.setVisibility(View.GONE);

        mButtonLayout = findViewById(R.id.buttonLayout);


        Intent i = getIntent();
        UserObject mUserObject = (UserObject)i.getSerializableExtra("UserObject");

        //Check if to show the bottom button layout
        Boolean mFab = (Boolean)i.getSerializableExtra("ShowFab");
        if (!mFab)
            mButtonLayout.setVisibility(View.GONE);
        else mButtonLayout.setVisibility(View.VISIBLE);




        mName = findViewById(R.id.name);
        mImage = findViewById(R.id.image);
        mImage1 = findViewById(R.id.image1);
        mImage3 = findViewById(R.id.image3);

        mVerified = findViewById(R.id.verified);

        mBack = findViewById(R.id.back);

        mJob = findViewById(R.id.job);
        mAbout = findViewById(R.id.about);
        mDegree = findViewById(R.id.degree);
        mSchool = findViewById(R.id.school);
        mTown = findViewById(R.id.homeTown);
        mCity = findViewById(R.id.city);


        mName.setText(mUserObject.getName() + ", " + mUserObject.getAge());

        if (!mUserObject.getVerification().equals("")) {
            mVerified.setVisibility(View.VISIBLE);
        }else mVerified.setVisibility(View.GONE);

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



        mBack.setOnClickListener(view -> { super.onBackPressed();});


        FloatingActionButton fabLike = findViewById(R.id.fabLike);
        FloatingActionButton fabNope = findViewById(R.id.fabNope);
        fabLike.setOnClickListener(v -> {
            CardFragment.flingContainer.getTopCardListener().selectRight();
            super.onBackPressed();
        });
        fabNope.setOnClickListener(v -> {
            CardFragment.flingContainer.getTopCardListener().selectLeft();
            super.onBackPressed();
        });




        if(!mUserObject.getProfileImageUrl().equals("default"))
            Glide.with(getApplicationContext()).load(mUserObject.getProfileImageUrl()).thumbnail(0.1f).into(mImage);

        if(!mUserObject.getImageUrl().equals("default"))
            Glide.with(getApplicationContext()).load(mUserObject.getImageUrl()).thumbnail(0.1f).into(mImage1);
        else mImage1.setVisibility(View.GONE);

        if(!mUserObject.getImageUrl3().equals("default"))
            Glide.with(getApplicationContext()).load(mUserObject.getImageUrl3()).thumbnail(0.1f).into(mImage3);
        else mImage3.setVisibility(View.GONE);





        if(!mUserObject.getAbout().equals(""))
        { mAbout.setText(mUserObject.getAbout());
        } else mAbout.setVisibility(View.GONE);


        if(!mUserObject.getJob().equals(""))
        { mJob.setText(mUserObject.getJob());
        } else mJob.setVisibility(View.GONE);

        if(!mUserObject.getDegree().equals(""))
        { mDegree.setText(mUserObject.getDegree());
        } else mDegree.setVisibility(View.GONE);

        if(!mUserObject.getSchool().equals(""))
        { mSchool.setText(mUserObject.getSchool());
        } else mSchool.setVisibility(View.GONE);

        if(!mUserObject.getCity().equals(""))
        { mCity.setText(mUserObject.getCity());
        } else mCity.setVisibility(View.GONE);



        if(!mUserObject.getTown().equals(""))
        { mTown.setText("From "+ mUserObject.getTown());
        } else mTown.setVisibility(View.GONE);


     // extraaa info

        if(!mUserObject.getStatus().equals(""))
        { mStatus.setText(mUserObject.getStatus());
        } else mStatus.setVisibility(View.GONE);

        if(!mUserObject.getReligion().equals(""))
        { mReligion.setText(mUserObject.getReligion());
        } else mReligion.setVisibility(View.GONE);

        if(!mUserObject.getHeight().equals(""))
        { mHeight.setText(mUserObject.getHeight());
        } else mHeight.setVisibility(View.GONE);

        if(!mUserObject.getZodiac().equals(""))
        { mZodiac.setText(mUserObject.getZodiac());
        } else mZodiac.setVisibility(View.GONE);

        if(!mUserObject.getDrinking().equals(""))
        { mDrinking.setText(mUserObject.getDrinking());
        } else mDrinking.setVisibility(View.GONE);

        if(!mUserObject.getSmoking().equals(""))
        { mSmoking.setText(mUserObject.getSmoking());
        } else mSmoking.setVisibility(View.GONE);

        if(!mUserObject.getExercise().equals(""))
        { mExercise.setText(mUserObject.getExercise());
        } else mExercise.setVisibility(View.GONE);

        if(!mUserObject.getPets().equals(""))
        { mPets.setText(mUserObject.getPets());
        } else mPets.setVisibility(View.GONE);

        if(!mUserObject.getKids().equals(""))
        { mKids.setText(mUserObject.getKids());
        } else mKids.setVisibility(View.GONE);

        if(!mUserObject.getDiet().equals(""))
        { mDiet.setText(mUserObject.getDiet());
        } else mDiet.setVisibility(View.GONE);

        if(!mUserObject.getPolitics().equals(""))
        { mPolitics.setText(mUserObject.getPolitics());
        } else mPolitics.setVisibility(View.GONE);

        if(!mUserObject.getReading().equals(""))
        { mReading.setText(mUserObject.getReading());
        } else mReading.setVisibility(View.GONE);



    }



}
