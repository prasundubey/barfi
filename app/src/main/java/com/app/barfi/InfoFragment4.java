package com.app.barfi;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class InfoFragment4 extends Fragment {

    public InfoFragment4() {
        // Required empty public constructor
    }


    //https://www.androidcode.ninja/android-alertdialog-example/ for dialog customisation

    EditText
            //mTown,
            mJob, mCompany, mSchool;
    private TextView mSkip;
    Button mNext4;
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


    private FirebaseAuth mAuth;
    public String mAge;
    private String mUserId;


    View view;


    private ArrayAdapter<String> adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_fragment_4, container, false);



        mStatus = (TextView) view.findViewById(R.id.status);
                mReligion = (TextView) view.findViewById(R.id.religion);
                mHeight = (TextView) view.findViewById(R.id.height);
                mZodiac = (TextView) view.findViewById(R.id.zodiac);
                mDrinking = (TextView) view.findViewById(R.id.drinking);
                mSmoking = (TextView) view.findViewById(R.id.smoking);
                mExercise = (TextView) view.findViewById(R.id.exercise);
                mPets = (TextView) view.findViewById(R.id.pets);
                mKids = (TextView) view.findViewById(R.id.kids);
                mDiet = (TextView) view.findViewById(R.id.diet);
                mPolitics = (TextView) view.findViewById(R.id.politics);
                mReading = (TextView) view.findViewById(R.id.reading);




        mNext4 = (Button) view.findViewById(R.id.next4);

        mSkip = (TextView) view.findViewById(R.id.skip2);





        mStatus.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("What's your marital status?");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("What religion do you follow?");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("How tall are you?");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("What's your zodiac sign?");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("How often do you drink?");
            builder.setIcon(R.drawable.i_drinking);
            String[] arr = {"Never", "Socially",  "Frequently"};

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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("How often do you smoke?");
            builder.setIcon(R.drawable.i_smoking);
            String[] arr = {"Never", "Socially",  "Regularly"};

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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("How often do you workout/exercise?");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("How do you feel about pets?");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Do you have or want babies?");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("What kind of diet do you follow?");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("What are your political views?");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Do you like reading books?");
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




        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.infoContainer, new InfoFragment5());
                fr.commit();
            }
        });



        mNext4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.infoContainer, new InfoFragment5());
                fr.commit();
            }
        });


        return view;
    }


    private void saveUserInformation() {

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


        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map userInfo = new HashMap();
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



        FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("details").updateChildren(userInfo);




        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();


    }


}