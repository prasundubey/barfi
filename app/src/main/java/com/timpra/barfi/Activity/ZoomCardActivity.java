package com.timpra.barfi.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.timpra.barfi.Objects.UserObject;
import com.timpra.barfi.R;

/**
 * Activity displayed when user clicks on a card
 *
 * It displays the user that was clicked on's information in detail
 */
public class ZoomCardActivity extends AppCompatActivity {

    private TextView    mName,
                        mJob,
                        mAbout;

    private ImageView mImage;
    private ImageView mImage1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_card);


        Intent i = getIntent();
        UserObject mUserObject = (UserObject)i.getSerializableExtra("UserObject");

        mName = findViewById(R.id.name);
        mJob = findViewById(R.id.job);

        mAbout = findViewById(R.id.about);
        mImage = findViewById(R.id.image);
        mImage1 = findViewById(R.id.image1);

        mName.setText(mUserObject.getName() + ", " + mUserObject.getAge());


        if(mUserObject.getJob().equals(""))
        {
        mJob.setText(mUserObject.getJob());
        } else mJob.setVisibility(View.GONE);

        mAbout.setText(mUserObject.getAbout());

        if(!mUserObject.getProfileImageUrl().equals("default"))
            Glide.with(getApplicationContext()).load(mUserObject.getProfileImageUrl()).into(mImage);




        if(!mUserObject.getImageUrl().equals("default"))
            Glide.with(getApplicationContext()).load(mUserObject.getImageUrl()).into(mImage1);
        else mImage1.setVisibility(View.GONE);
    }



}
