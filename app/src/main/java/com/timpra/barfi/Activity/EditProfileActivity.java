package com.timpra.barfi.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.timpra.barfi.InfoFragment4;
import com.timpra.barfi.NewUserDetails;
import com.timpra.barfi.Objects.UserObject;
import com.timpra.barfi.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;


/**
 * Activity responsible for handling the edit of user's data
 */
public class EditProfileActivity extends AppCompatActivity {

    private EditText
            mAbout,
            mJobTitle,
            mCompany,
            mSchool,
            mTown;

    private TextView
            mDegree,
            mSave;

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


    private ProgressBar pgsBar;

    private ArrayAdapter <String> adapter;


    private ImageView mProfileImage;
    private ImageView mImage;
    private ImageView mImage3;
    private Uri resultUri;
    private Uri resultUri1;
    private Uri resultUri3;

    //For adding more images see this: https://www.youtube.com/watch?v=BLffEJkREMQ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        getUserInfo();

        pgsBar = (ProgressBar) findViewById(R.id.pBar);


        mRadioGroup = findViewById(R.id.radioRealButtonGroup);

        mProfileImage = findViewById(R.id.profileImage);
        mImage = findViewById(R.id.image);
        mImage3 = findViewById(R.id.image3);

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
            builder.setTitle("Whats you degreee mf");
            builder.setIcon(R.drawable.ic_school);

            String[] deg = {"High school diploma", "In college", "Undergraduate", "In grad school", "Graduate"};

            builder.setItems(deg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = Arrays.asList(deg).get(which);
                    mDegree.setText(selectedItem);
                }

            });

            builder.setNeutralButton("Do not disclose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDegree.setText(null);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

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
            builder.setTitle("WHAT RELIGION I FOLLOW?");
            builder.setIcon(R.drawable.i_religion);
            String[] arr = {"Agnostic", "Atheist", "Buddhist", "Christian", "Hindu","Jain","jewish","Muslim","Zoroastrian","Sikh","Spritual","Others" };

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
            String[] arr = {"5,0'", "5,5'", "6,0'", "6,5'", "7,0'"};

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
            builder.setTitle("What is my zodiac sign?");
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
            String[] arr = {"Vegan", "Vegetarian", "Occasionally non-vegetarian", "Non-vegetarian", "Eggitarian", "Jain"};

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
            builder.setTitle("How do I like reading books?");
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




                mAbout.setText(mUser.getAbout());
                mTown.setText(mUser.getTown());
                mJobTitle.setText(mUser.getJobTitle());
                mCompany.setText(mUser.getCompany());
                mDegree.setText(mUser.getAbout());
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
                    Glide.with(getApplicationContext()).load(mUser.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);

                if(!mUser.getImageUrl().equals("default"))
                    Glide.with(getApplicationContext()).load(mUser.getImageUrl()).apply(RequestOptions.circleCropTransform()).into(mImage);

                if(!mUser.getImageUrl3().equals("default"))
                    Glide.with(getApplicationContext()).load(mUser.getImageUrl3()).apply(RequestOptions.circleCropTransform()).into(mImage3);

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

        if(mRadioGroup.getPosition()==0)
            userSex = "Male";
        else
            userSex = "Female";

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

        if(resultUri3 != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images3").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            UploadTask uploadTask1 = filePath.putFile(resultUri3);

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
        final String about = mAbout.getText().toString();


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
        userInfo.put("job", jobTitle + " at " + company);
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
                break;
            //image3
            case 3:
                if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
                    final Uri imageUri3 = data.getData();
                    resultUri3 = imageUri3;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri3);
                        Glide.with(getApplication())
                                .load(bitmap) // Uri of the picture
                                .apply(RequestOptions.circleCropTransform())
                                .into(mImage3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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


}
