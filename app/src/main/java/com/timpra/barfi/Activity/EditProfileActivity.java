package com.timpra.barfi.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.timpra.barfi.Objects.UserObject;
import com.timpra.barfi.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;


/**
 * Activity responsible for handling the edit of user's data
 */
public class EditProfileActivity extends AppCompatActivity {

    private EditText    mName,
                        mPhone,
                       // mAge,
                        mJob,
                        mAbout;
   // public String mAge;
    private TextView mAge;

    private SegmentedButtonGroup mRadioGroup;

    private ImageView mProfileImage;
    private ImageView mImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    UserObject mUser = new UserObject();



    private ProgressBar pgsBar;




    private Uri resultUri;

    //image1
    private Uri resultUri1;

    //For adding more images see this: https://www.youtube.com/watch?v=BLffEJkREMQ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pgsBar = (ProgressBar) findViewById(R.id.pBar);


        mName = findViewById(R.id.name);
        mPhone = findViewById(R.id.phone);
        mAge = findViewById(R.id.age);
        mJob = findViewById(R.id.job);
        mAbout = findViewById(R.id.about);

        mRadioGroup = findViewById(R.id.radioRealButtonGroup);

        mProfileImage = findViewById(R.id.profileImage);

        //image1
        mImage = findViewById(R.id.image);




        mAuth = FirebaseAuth.getInstance();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        getUserInfo();

        //on profile image click allow user to choose another pic by calling the responding intentt

        mProfileImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);

        });


        //image1
        mImage.setOnClickListener(view -> {
            Intent intent1 = new Intent();
            intent1.setType("image/*");
            //startActivityForResult(intent1, 2);
            intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent1.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent1, "choose other"),2);




        });
    }







    /**
     * Get user's current info data and populate the corresponding Elements
     */
    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                //age

                if(dataSnapshot.child("age").getValue()==null){
                    Intent intent = new Intent(EditProfileActivity.this, Birthday.class);
                    startActivity(intent);
                }

                mUser.parseObject(dataSnapshot);

                mName.setText(mUser.getName());
                mPhone.setText(mUser.getPhone());
                mJob.setText(mUser.getJob());
                mAbout.setText(mUser.getAbout());
                mAge.setText(mUser.getAge());
                if(!mUser.getProfileImageUrl().equals("default"))
                    Glide.with(getApplicationContext()).load(mUser.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);


                //Image1
                if(!mUser.getImageUrl().equals("default"))
                    Glide.with(getApplicationContext()).load(mUser.getImageUrl()).apply(RequestOptions.circleCropTransform()).into(mImage);




                if(mUser.getUserSex().equals("Male"))
                    mRadioGroup.setPosition(0, false);
                else
                    mRadioGroup.setPosition(1, false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Store user's info in the database
     * if the image has been changed then upload it to the storage
     */
    private void saveUserInformation() {

        String userSex;

        String name = mName.getText().toString();
        String phone = mPhone.getText().toString();
        //String age = mAge.getText().toString();
        String job = mJob.getText().toString();
        String about = mAbout.getText().toString();
        if(mRadioGroup.getPosition()==0)
            userSex = "Male";
        else
            userSex = "Female";

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
       // userInfo.put("age", age);
        userInfo.put("job", job);
        userInfo.put("sex", userSex);
        userInfo.put("about", about);
        mUserDatabase.updateChildren(userInfo);

        if(resultUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            UploadTask uploadTask = filePath.putFile(resultUri);

            uploadTask.addOnFailureListener(e -> {
                finish();

            });
            uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage = new HashMap();
                newImage.put("profileImageUrl", uri.toString());
                mUserDatabase.updateChildren(newImage);
                finish();

            }).addOnFailureListener(exception -> {
                finish();
            }));
        }else{
            finish();
        }


        // set new image for 2nd

        if(resultUri1 != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            UploadTask uploadTask1 = filePath.putFile(resultUri1);

            uploadTask1.addOnFailureListener(e -> {
                finish();
                return;
            });
            uploadTask1.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage1 = new HashMap();
                newImage1.put("imageUrl", uri.toString());
                mUserDatabase.updateChildren(newImage1);



                finish();
            }).addOnFailureListener(exception -> {
                finish();
            }));
        }else{
            finish();
        }

    }

    /**
     * Get the uri of the image the user picked if the result is successful
     * @param requestCode - code of the request ( for the image request is 1)
     * @param resultCode - if the result was successful
     * @param data - data of the image fetched
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case 1:
                if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                    final Uri imageUri = data.getData();
                    resultUri = imageUri;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        Glide.with(getApplication())
                                .load(bitmap) // Uri of the picture
                                .apply(RequestOptions.circleCropTransform())
                                .into(mProfileImage);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 2:
                if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
                    final Uri imageUri = data.getData();
                    resultUri1 = imageUri;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri1);
                        Glide.with(getApplication())
                                .load(bitmap) // Uri of the picture
                                .apply(RequestOptions.circleCropTransform())
                                .into(mImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            //image1
        }

    }


    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        saveUserInformation();
        return false;
    }


}
