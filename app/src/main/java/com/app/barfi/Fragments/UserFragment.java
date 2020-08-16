package com.app.barfi.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.barfi.Activity.ForceUpdateActivity;
import com.app.barfi.Activity.MainActivity;
import com.app.barfi.Activity.VerifyAccount;
import com.app.barfi.Activity.WebViewActivity;
import com.app.barfi.BuildConfig;
import com.app.barfi.NewUserDetails;
import com.app.barfi.Objects.CurrentUserObject;
import com.app.barfi.Objects.ScoreObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.app.barfi.Activity.EditProfileActivity;
import com.app.barfi.Activity.ZoomCardUser;
import com.app.barfi.Objects.UserObject;
import com.app.barfi.R;
import com.app.barfi.Activity.SettingsActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.media.MediaRecorder.VideoSource.CAMERA;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Activity responsible for displaying the current user and the buttons to go
 * to the settingsActivity and EditProfileActivity
 */
public class UserFragment extends Fragment {

    private TextView mName, mPreview, mCity;

    private TextView mVerifyButton, mUnderReview;

    private ImageView mUnVerified, mVerified;

    private TextView mShare,mContact;

    private ImageView mProfileImage, mSettings, mEditProfile;

    private DatabaseReference mUserDatabase;

    private TextView mFlag;

    private TextView mUpdateApp;

    private String userId;

    public UserFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);


        mName = view.findViewById(R.id.name);
        mProfileImage = view.findViewById(R.id.profileImage);
        mCity = view.findViewById(R.id.city);

        mPreview = view.findViewById(R.id.preview);
        mSettings = view.findViewById(R.id.settings);
        mEditProfile = view.findViewById(R.id.editProfile);


        mVerifyButton = view.findViewById(R.id.verify_button);
        mUnderReview = view.findViewById(R.id.underReview);

        mVerified = view.findViewById(R.id.verified);
        mUnVerified = view.findViewById(R.id.unverified);


        mShare = view.findViewById(R.id.share);
        mContact = view.findViewById(R.id.contact);

        mFlag = view.findViewById(R.id.flag);
        mFlag.setVisibility(View.GONE);

        mUpdateApp = view.findViewById(R.id.updateNow);
        mUpdateApp.setVisibility(View.GONE);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getUserInfo();


        mContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), WebViewActivity.class);
                intent1.putExtra("url", "https://docs.google.com/forms/d/e/1FAIpQLSfdQvWKH0hs06imu73ByOTrhIULhCffIfNOT2aiEcZsMQjCqA/viewform?usp=sf_link");
                startActivity(intent1);
            }
        });

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hey, I love this Barfi app. The perfect app for the love marriage! Checkout: https://bit.ly/Barfi_app";
                String shareSub = "Heyyo checkout this Barfi App - for love marriages";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });



        mProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ZoomCardUser.class);
            startActivity(intent);
        });

        mPreview.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ZoomCardUser.class);
            startActivity(intent);
        });


        mEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });
        mSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });





        mVerifyButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), VerifyAccount.class);
            startActivity(intent);

        });



        DatabaseReference versionDB  = FirebaseDatabase.getInstance().getReference().child("Version");
        versionDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int vc = Integer.parseInt(dataSnapshot.child("playVC").getValue().toString());

                    if(vc> BuildConfig.VERSION_CODE){
                        mUpdateApp.setVisibility(View.VISIBLE);
                    }else
                        mUpdateApp.setVisibility(View.GONE);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mUpdateApp.setOnClickListener(v -> {
            final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });


        return view;
    }






    /**
     * Fetches current user's info from the database
     */

    private void getUserInfo() {
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                UserObject mUser = new UserObject();
                mUser.parseObject(dataSnapshot);

                mName.setText(mUser.getName() + ", " + mUser.getAge());
                if(getContext() != null && !mUser.getProfileImageUrl().equals("default"))
                    Glide.with(getContext()).load(mUser.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).thumbnail(0.1f).into(mProfileImage);

                if (!mUser.getCity().equals("")) {
                    mCity.setText(mUser.getCity());
                } else mCity.setVisibility(View.GONE);


                if (mUser.getVerification().equals("")) {

                    mVerified.setVisibility(View.GONE);
                    mVerifyButton.setVisibility(View.GONE);
                    mUnderReview.setVisibility(View.VISIBLE);
                    mUnVerified.setVisibility(View.VISIBLE);

                }
                if (!mUser.getVerification().equals("")) {

                    mVerifyButton.setVisibility(View.GONE);
                    mUnderReview.setVisibility(View.GONE);
                    mUnVerified.setVisibility(View.GONE);

                    mVerified.setVisibility(View.VISIBLE);

                    // increase score of verified
                    ScoreObject mScore = new ScoreObject();
                    mScore.parseObject(dataSnapshot);
                    Integer score = mScore.getScore();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("score").setValue(score);

                }

                if (!dataSnapshot.hasChild("verified")){
                    mVerified.setVisibility(View.GONE);
                    mUnderReview.setVisibility(View.GONE);

                    mUnVerified.setVisibility(View.VISIBLE);
                    mVerifyButton.setVisibility(View.VISIBLE);

                }

                if(dataSnapshot.hasChild("xx")){
                    //  Toast.makeText(getContext(), "Your account is blocked & invisible to others. Contact us for more info.", Toast.LENGTH_LONG).show();
                    mFlag.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference().child("location").child(userId).removeValue();
                } else mFlag.setVisibility(View.GONE);


                if((CurrentUserObject) Objects.requireNonNull(getActivity()).getApplication()!=null)
                    ((CurrentUserObject) getActivity().getApplication()).initialize(dataSnapshot);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onResume(){
        super.onResume();
        getUserInfo();

    }


}