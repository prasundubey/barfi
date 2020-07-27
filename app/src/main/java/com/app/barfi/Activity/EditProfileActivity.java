package com.app.barfi.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.exifinterface.media.ExifInterface;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.app.barfi.BuildConfig;
import com.app.barfi.Login.AuthenticationActivity;
import com.app.barfi.Objects.ScoreObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.app.barfi.Objects.UserObject;
import com.app.barfi.R;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceLandmark;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Activity responsible for handling the edit of user's data
 */
public class EditProfileActivity extends AppCompatActivity {

    //Params to change
    private Integer imageQuality = 35;
    private Integer imageTextPermit = 12;

    private EditText
            mAbout,
            mJobTitle,
            mCompany,
            mSchool,
            mTown;

    private TextView
            mDegree,
            mSave;

    private Switch mSwitch;
    private TextView mFullName;
    private TextView mGender;

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

    private SegmentedButtonGroup mRadioGroup;


    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    UserObject mUser = new UserObject();


    private LinearLayout mPgs;

    private ArrayAdapter <String> adapter;


    private ImageView mProfileImage;
    private ImageView mImage;
    private ImageView mImage3;
    private Uri resultUri1;
    private Uri resultUri2;
    private Uri resultUri3;


    private ImageView mCross2,mCross3;

    //For adding more images see this: https://www.youtube.com/watch?v=BLffEJkREMQ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPgs = findViewById(R.id.pgsLL);
        mPgs.setVisibility(View.VISIBLE);

        mFullName = (TextView) findViewById(R.id.name);
        mGender = (TextView) findViewById(R.id.gender);

        mSwitch = (Switch) findViewById(R.id.switch1);

        mRadioGroup = findViewById(R.id.radioRealButtonGroup);

        mProfileImage = findViewById(R.id.profileImage);
        mImage = findViewById(R.id.image);
        mImage3 = findViewById(R.id.image3);

        mCross2 = findViewById(R.id.cross2);
        mCross3 = findViewById(R.id.cross3);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        getUserInfo();

        mCross2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImage.setImageResource(R.drawable.icons_add_image);
                mUserDatabase.child("imageUrl").removeValue();
                resultUri2=null;
                mCross2.setVisibility(View.GONE);

            }
        });

        mCross3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImage3.setImageResource(R.drawable.icons_add_image);
                mUserDatabase.child("imageUrl3").removeValue();
                resultUri3=null;
                mCross3.setVisibility(View.GONE);

            }
        });


        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final String name = mUser.getFullName().substring(0,1);
                    mFullName.setText(name);
                    Map userInfo1 = new HashMap();
                    userInfo1.put("name", name);
                    mUserDatabase.updateChildren(userInfo1);


                } else {
                    final String name = mUser.getFullName();
                    mFullName.setText(mUser.getFullName());
                    Map userInfo1 = new HashMap();
                    userInfo1.put("name", name);
                    mUserDatabase.updateChildren(userInfo1);
                }
            }
        });


        //on profile image click allow user to choose another pic by calling the responding intentt
        mProfileImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        //image1
        mImage.setOnClickListener(view -> {
            Intent intent1 = new Intent();
            intent1.setType("image/*");
            //intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent1.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent1, "choose other"),2);
        });

        //image3
        mImage3.setOnClickListener(view -> {
            Intent intent3 = new Intent();
            intent3.setType("image/*");
            // intent3.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent3.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent3, "choose other"),3);

        });


        mSave = (TextView) findViewById(R.id.save);

        mTown = (EditText) findViewById(R.id.homeTown);
        mJobTitle = (EditText) findViewById(R.id.jobTitle);
        mCompany = (EditText) findViewById(R.id.company);
        mAbout = (EditText) findViewById(R.id.bio);
        mDegree = (TextView) findViewById(R.id.degree);
        mSchool = (EditText) findViewById(R.id.school);


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






        mDegree.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("What's my highest qualification?");
            builder.setIcon(R.drawable.ic_school);

            String[] deg = {"High school diploma", "In college", "Undergraduate", "In grad school", "Graduate"};

            builder.setItems(deg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(deg).get(which);
                    mDegree.setText(selectedItem);
                }

            });


            builder.setNeutralButton( "Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDegree.setText(null);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            // dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(R.color.colorBlack);



        });

//infopage 4

        mStatus.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("What's my marital status?");
            builder.setIcon(R.drawable.i_status);
            String[] arr = {"Never married", "Divorced", "Awaiting Divorce"};

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mStatus.setText(selectedItem);
                }
            });

            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mStatus.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        mReligion.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("What religion do I follow?");
            builder.setIcon(R.drawable.i_religion);
            String[] arr = {"Agnostic", "Atheist", "Buddhist", "Christian", "Hindu","Jain","Jewish","Muslim","Zoroastrian","Sikh","Spiritual","Others" };

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mReligion.setText(selectedItem);
                }
            });
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mReligion.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        mHeight.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("How tall am I?");
            builder.setIcon(R.drawable.i_height);
            String[] arr = {"<4.10'", "4.10'", "4.11'", "5'0","5.1'","5.2'","5.3'","5.4'", "5.5'","5.6'","5.7'","5.8'","5.9'","5.10'","5.11'", "6.0'","6.1'","6.2'","6.3'","6.4'", "6.5'", ">6.5'"};

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mHeight.setText(selectedItem);
                }
            });
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mHeight.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        mZodiac.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("What's my zodiac sign?");
            builder.setIcon(R.drawable.i_zodiac);
            String[] arr = {"Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo", "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"};

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mZodiac.setText(selectedItem);
                }
            });
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mZodiac.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        mDrinking.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("How often do I drink?");
            builder.setIcon(R.drawable.i_drinking);
            String[] arr = {"Socially", "Never", "Frequently"};

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mDrinking.setText(selectedItem);
                }
            });
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mDrinking.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        mSmoking.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("How often do I smoke?");
            builder.setIcon(R.drawable.i_smoking);
            String[] arr = {"Socially", "Never", "Regularly"};

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mSmoking.setText(selectedItem);
                }
            });
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mSmoking.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        mExercise.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("How often do I workout/exercise?");
            builder.setIcon(R.drawable.i_exercise);
            String[] arr = {"Actively", "Sometimes", "Almost never", "Never"};

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mExercise.setText(selectedItem);
                }
            });
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mExercise.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        mPets.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("How do I feel about pets?");
            builder.setIcon(R.drawable.i_pet);
            String[] arr = {"Dogs", "Cats", "Any pet", "Don't want"};

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mPets.setText(selectedItem);
                }
            });
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mPets.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        mKids.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do I have or want babies?");
            builder.setIcon(R.drawable.i_kids);
            String[] arr = {"Want someday", "Don't want", "Have and want more", "Have and don't want more"};

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mKids.setText(selectedItem);
                }
            });
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mKids.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        mDiet.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("What kind of diet do I follow?");
            builder.setIcon(R.drawable.i_diet);
            String[] arr = {"Vegan", "Vegetarian", "Occasionally non-vegetarian", "Non-vegetarian", "Eggiterian", "Jain"};

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mDiet.setText(selectedItem);
                }
            });
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mDiet.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        mPolitics.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("What are my political views?");
            builder.setIcon(R.drawable.i_politics);
            String[] arr = {"Apolitical", "Neutral", "Left", "Right"};

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mPolitics.setText(selectedItem);
                }
            });
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mPolitics.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        mReading.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do I like reading books?");
            builder.setIcon(R.drawable.i_reading);
            String[] arr = {"Love reading", "Read sometimes", "Don't read much", "Never"};

            builder.setItems(arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(arr).get(which);
                    mReading.setText(selectedItem);
                }
            });
            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { mReading.setText(null); }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });



        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                onBackPressed();
            }
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

                //switched to control name displayed
                if (mUser.getName().length()==1) {
                    mSwitch.setChecked(true);
                    mFullName.setText(mUser.getName());
                }
                else {
                    mSwitch.setChecked(false);
                    mFullName.setText(mUser.getFullName());
                }

                mGender.setText(mUser.getUserSex());

                mAbout.setText(mUser.getAbout());
                mTown.setText(mUser.getTown());
                mJobTitle.setText(mUser.getJobTitle());
                mCompany.setText(mUser.getCompany());
                mDegree.setText(mUser.getDegree());
                mSchool.setText(mUser.getSchool());


                mStatus.setText(mUser.getStatus());
                        mReligion.setText(mUser.getReligion());
                        mHeight.setText(mUser.getHeight());
                        mZodiac.setText(mUser.getZodiac());
                        mDrinking.setText(mUser.getDrinking());
                        mSmoking.setText(mUser.getSmoking());
                        mExercise.setText(mUser.getExercise());
                        mPets.setText(mUser.getPets());
                        mKids.setText(mUser.getKids());
                        mDiet.setText(mUser.getDiet());
                        mPolitics.setText(mUser.getPolitics());
                        mReading.setText(mUser.getReading());




                if(!mUser.getProfileImageUrl().equals("default"))
                    Glide.with(getApplicationContext()).load(mUser.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).thumbnail(0.1f).into(mProfileImage);

                if(!mUser.getImageUrl().equals("default")) {
                    Glide.with(getApplicationContext()).load(mUser.getImageUrl()).apply(RequestOptions.circleCropTransform()).thumbnail(0.1f).into(mImage);
                    mCross2.setVisibility(View.VISIBLE);
                }
                if(!mUser.getImageUrl3().equals("default")) {
                    Glide.with(getApplicationContext()).load(mUser.getImageUrl3()).apply(RequestOptions.circleCropTransform()).thumbnail(0.1f).into(mImage3);
                    mCross3.setVisibility(View.VISIBLE);
                }


                if(mUser.getUserSex().equals("Male"))
                    mRadioGroup.setPosition(0, false);
                else
                    mRadioGroup.setPosition(1, false);

                mPgs.setVisibility(View.GONE);
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

        if(mRadioGroup.getPosition()==0)
            userSex = "Male";
        else
            userSex = "Female";

        if(resultUri1 != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmRotated1.compress(Bitmap.CompressFormat.JPEG, imageQuality, baos);
            byte[] fileInBytes = baos.toByteArray();

            UploadTask uploadTask = filePath.putBytes(fileInBytes);

            //UploadTask uploadTask = filePath.putFile(resultUri1);

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

        if(resultUri2 != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmRotated2.compress(Bitmap.CompressFormat.JPEG, imageQuality, baos);
            byte[] fileInBytes = baos.toByteArray();

            UploadTask uploadTask1 = filePath.putBytes(fileInBytes);


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

        if(resultUri3 != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images3").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmRotated3.compress(Bitmap.CompressFormat.JPEG, imageQuality, baos);
            byte[] fileInBytes = baos.toByteArray();

            UploadTask uploadTask1 = filePath.putBytes(fileInBytes);

          //  UploadTask uploadTask1 = filePath.putFile(resultUri3);

            uploadTask1.addOnFailureListener(e -> {
                finish();
                return;
            });
            uploadTask1.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage3 = new HashMap();
                newImage3.put("imageUrl3", uri.toString());
                mUserDatabase.updateChildren(newImage3);

                finish();
            }).addOnFailureListener(exception -> {
                finish();
            }));
        }else{
            finish();
        }



        final String town = mTown.getText().toString();
        final String jobTitle = mJobTitle.getText().toString();
        final String company = mCompany.getText().toString();
        final String school = mSchool.getText().toString();
        final String degree = mDegree.getText().toString();
        final String about = mAbout.getText().toString().trim();


        final String status = mStatus.getText().toString();
        final String religion = mReligion.getText().toString();
        final String height = mHeight.getText().toString();
        final String zodiac = mZodiac.getText().toString();
        final String drinking = mDrinking.getText().toString();
        final String smoking = mSmoking.getText().toString();
        final String exercise = mExercise.getText().toString();
        final String pets = mPets.getText().toString();
        final String kids = mKids.getText().toString();
        final String diet = mDiet.getText().toString();
        final String politics = mPolitics.getText().toString();
        final String reading = mReading.getText().toString();



        Map userInfo = new HashMap();

        userInfo.put("town", town);
        userInfo.put("jobTitle", jobTitle);
        userInfo.put("company", company);
        if(!jobTitle.isEmpty()&& !company.isEmpty())
            userInfo.put("job", jobTitle + " at " + company);
        if(jobTitle.isEmpty()&& !company.isEmpty())
            userInfo.put("job", company);
        if(!jobTitle.isEmpty()&& company.isEmpty())
            userInfo.put("job", jobTitle);
        if(jobTitle.isEmpty() && company.isEmpty())
            userInfo.put("job", jobTitle);
        userInfo.put("school", school);
        userInfo.put("degree", degree);
        userInfo.put("about", about);

        userInfo.put("status", status);
        userInfo.put("religion", religion);
        userInfo.put("height", height);
        userInfo.put("zodiac", zodiac);
        userInfo.put("drinking", drinking);
        userInfo.put("smoking", smoking);
        userInfo.put("exercise", exercise);
        userInfo.put("pets", pets);
        userInfo.put("kids", kids);
        userInfo.put("diet", diet);
        userInfo.put("politics", politics);
        userInfo.put("reading", reading);


        mUserDatabase.child("details").updateChildren(userInfo);

        updateScore();

    }

    //update score on updating the info
    private void updateScore() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ScoreObject mScore = new ScoreObject();
                mScore.parseObject(dataSnapshot);
                Integer score = mScore.getScore();
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("score").setValue(score);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * Get the uri of the image the user picked if the result is successful
     * @param requestCode - code of the request ( for the image request is 1)
     * @param resultCode - if the result was successful
     * @param data - data of the image fetched
     */

    InputImage image;
    TextRecognizer recognizer = TextRecognition.getClient();
    FaceDetector detector = FaceDetection.getClient();


    private Bitmap bitmap1,bitmap2,bitmap3;
    private Bitmap bmRotated1,bmRotated2,bmRotated3;
    private InputStream in1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        switch (requestCode) {
            case 1:
                if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                    final Uri imageUri = data.getData();
                    resultUri1 = imageUri;
                    Toast.makeText(EditProfileActivity.this, "Analysing Image..", Toast.LENGTH_SHORT).show();

                    try {
                        bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri1);
                        image = InputImage.fromFilePath(getApplicationContext(), resultUri1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    detectFaces(image, 1);
                }
                break;

            case 2:
                if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
                    final Uri imageUri = data.getData();
                    resultUri2 = imageUri;
                    Toast.makeText(EditProfileActivity.this, "Analysing Image..", Toast.LENGTH_SHORT).show();

                    try {
                        bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri2);
                        image = InputImage.fromFilePath(getApplicationContext(), resultUri2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    detectFaces(image, 2);
                }
                break;

            //image3
            case 3:
                if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
                    final Uri imageUri3 = data.getData();
                    resultUri3 = imageUri3;
                    Toast.makeText(EditProfileActivity.this, "Analysing Image..", Toast.LENGTH_SHORT).show();

                    try {
                        bitmap3 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri3);
                        image = InputImage.fromFilePath(getApplicationContext(), resultUri3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    detectFaces(image, 3);

                }
        }

    }



    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        saveUserInformation();
        return false;
    }

      @Override
    public void onBackPressed() {
        saveUserInformation();
        Toast.makeText(getApplicationContext(), "Details updated", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }



    public void detectFaces(InputImage image, Integer position) {
        Task<List<Face>> resultFace = detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(List<Face> faces) {
                if (!faces.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Face detected", Toast.LENGTH_SHORT).show();
                    detectText(image,position);
                } else {

                    switch (position) {
                        case 1:
                            resultUri1 = null;
                            break;
                        case 2:
                            resultUri2 = null;
                            mCross2.setVisibility(View.GONE);
                            break;
                        case 3:
                            resultUri3 = null;
                            mCross3.setVisibility(View.GONE);
                            break;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                    builder.setMessage( Html.fromHtml("<font color='#2d2d2d'>Please select another photo to continue. </font>"));
                    builder.setTitle( Html.fromHtml("<font color='#f76c7f'>Your face is not clearly visible in this photo!</font>"));
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Task failed with an exception
                // ...
            }
        });

    }

    public void detectText(InputImage image, Integer position) {
        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text visionText) {
               // processTextBlock(visionText);
                if (visionText.getText().length() < imageTextPermit) {
                    switch (position) {
                        case 1:
                            Glide.with(getApplication()).load(resultUri1).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                            break;
                        case 2:
                            Glide.with(getApplication()).load(resultUri2).apply(RequestOptions.circleCropTransform()).into(mImage);
                            mCross2.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            Glide.with(getApplication()).load(resultUri3).apply(RequestOptions.circleCropTransform()).into(mImage3);
                            mCross3.setVisibility(View.VISIBLE);
                            break;
                    }

                    ExifInterface exifInterface = null;
                    try {

                        switch (position) {
                            case 1:
                                in1 = getContentResolver().openInputStream(resultUri1);
                                break;
                            case 2:
                                in1 = getContentResolver().openInputStream(resultUri2);
                                break;
                            case 3:
                                in1 = getContentResolver().openInputStream(resultUri3);
                                break;
                        }


                     //   in1 = getContentResolver().openInputStream(resultUri1);

                        exifInterface = new ExifInterface(in1);
                    } catch (IOException e) {
                        Toast.makeText(EditProfileActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                    } finally {
                        if (in1 != null) {
                            try {
                                in1.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    switch (position) {
                        case 1:
                            bmRotated1 = rotateBitmap(bitmap1, orientation);
                            break;
                        case 2:
                            bmRotated2 = rotateBitmap(bitmap2, orientation);
                            break;
                        case 3:
                            bmRotated3 = rotateBitmap(bitmap3, orientation);
                            break;
                    }

                    // bmRotated1 = rotateBitmap(bitmap1, orientation);

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                    builder.setMessage( Html.fromHtml("<font color='#2d2d2d'>Photos with text cannot be uploaded to your profile.\nPlease select another photo to continue. </font>"));
                    builder.setTitle( Html.fromHtml("<font color='#f76c7f'>Selected Image Has Text!</font>"));
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    switch (position) {
                        case 1:
                            resultUri1 = null;
                            break;
                        case 2:
                            resultUri2 = null;
                            mCross2.setVisibility(View.GONE);
                            break;
                        case 3:
                            resultUri3 = null;
                            mCross3.setVisibility(View.GONE);
                            break;
                    }

                    //resultUri1 = null;
                }
            }}).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                    }});
    }






    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }


    }

    //process text
    /* private void processTextBlock(Text result) {
        String resultText = result.getText();
        if (resultText.length()>=imageTextPermit)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder.setMessage( Html.fromHtml("<font color='#2d2d2d'>Photos with text cannot be uploaded to your profile.\nPlease select another photo to continue. </font>"));
            builder.setTitle( Html.fromHtml("<font color='#f76c7f'>Selected Image Has Text!</font>"));
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }


        for (Text.TextBlock block : result.getTextBlocks()) {
            String blockText = block.getText();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (Text.Line line : block.getLines()) {
                String lineText = line.getText();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (Text.Element element : line.getElements()) {
                    String elementText = element.getText();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }

    }*/

    //process face function example is in infoFrag2
}
