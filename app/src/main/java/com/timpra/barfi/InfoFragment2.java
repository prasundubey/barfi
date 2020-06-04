package com.timpra.barfi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.timpra.barfi.Activity.MainActivity;
import com.timpra.barfi.Objects.UserObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InfoFragment2 extends Fragment {

    private ImageView mProfileImage;
    private ImageView mImage;
    private ImageView mImage3;

    private TextView mName;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    UserObject mUser = new UserObject();
    Button mNext2;



    private Uri resultUri;
    //image1
    private Uri resultUri1;
    //image3
    private Uri resultUri3;


    public InfoFragment2() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment_2, container, false);

        mName = view.findViewById(R.id.name);

        mProfileImage = view.findViewById(R.id.profileImage);

        //image1
        mImage = view.findViewById(R.id.image);
        //image3
        mImage3 = view.findViewById(R.id.image3);




        mNext2 = view.findViewById(R.id.next2);


        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        getUserInfo();

        //on profile image click allow user to choose another pic by calling the responding intentt

        mProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);

        });


        //image1
        mImage.setOnClickListener(v -> {
            Intent intent1 = new Intent();
            intent1.setType("image/*");
            //startActivityForResult(intent1, 2);
            //intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent1.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent1, "choose other"),2);
        });

        //image3
        mImage3.setOnClickListener(v -> {
            Intent intent3 = new Intent();
            intent3.setType("image/*");
           // intent3.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent3.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent3, "choose other"),3);

        });


        mNext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.infoContainer, new InfoFragment3());
                fr.commit();
                // return;
            }
        });



        return view;

    }






    /**
     * Get user's current info data and populate the corresponding Elements
     */
    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                mUser.parseObject(dataSnapshot);

                mName.setText("Hi " + mUser.getName() + ",");


                if(!mUser.getProfileImageUrl().equals("default"))
                    Glide.with(getActivity().getApplicationContext()).load(mUser.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);


                //Image1
                if(!mUser.getImageUrl().equals("default"))
                    Glide.with(getActivity().getApplicationContext()).load(mUser.getImageUrl()).apply(RequestOptions.circleCropTransform()).into(mImage);


                //Image3
                if(!mUser.getImageUrl3().equals("default"))
                    Glide.with(getActivity().getApplicationContext()).load(mUser.getImageUrl3()).apply(RequestOptions.circleCropTransform()).into(mImage3);


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

        if(resultUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            UploadTask uploadTask = filePath.putFile(resultUri);

            uploadTask.addOnFailureListener(e -> {
                getActivity().finish();

            });
            uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage = new HashMap();
                newImage.put("profileImageUrl", uri.toString());
                mUserDatabase.updateChildren(newImage);
               // getActivity().finish();

            }).addOnFailureListener(exception -> {
              getActivity().finish();
            }));
        }else{
           // getActivity().finish();
        }


        // set new image for 2nd

        if(resultUri1 != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            UploadTask uploadTask1 = filePath.putFile(resultUri1);

            uploadTask1.addOnFailureListener(e -> {
                getActivity().finish();
                return;
            });
            uploadTask1.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage1 = new HashMap();
                newImage1.put("imageUrl", uri.toString());
                mUserDatabase.updateChildren(newImage1);
               // getActivity().finish();

            }).addOnFailureListener(exception -> {
               getActivity().finish();
            }));
        }else{
           // getActivity().finish();
        }


        if(resultUri3 != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images3").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            UploadTask uploadTask1 = filePath.putFile(resultUri3);

            uploadTask1.addOnFailureListener(e -> {
                getActivity().finish();
                return;
            });
            uploadTask1.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage3 = new HashMap();
                newImage3.put("imageUrl3", uri.toString());
                mUserDatabase.updateChildren(newImage3);
               // getActivity().finish();
            }).addOnFailureListener(exception -> {
               // getActivity().finish();
            }));
        }else{
           // getActivity().finish();
        }


    }

    /**
     * Get the uri of the image the user picked if the result is successful
     * @param requestCode - code of the request ( for the image request is 1)
     * @param resultCode - if the result was successful
     * @param data - data of the image fetched
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case 1:
                if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                    final Uri imageUri = data.getData();
                    resultUri = imageUri;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                        Glide.with(getActivity().getApplication())
                                .load(bitmap) // Uri of the picture
                                .apply(RequestOptions.circleCropTransform())
                                .into(mProfileImage);
                        mNext2.setEnabled(true);


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
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri1);
                        Glide.with(getActivity().getApplication())
                                .load(bitmap) // Uri of the picture
                                .apply(RequestOptions.circleCropTransform())
                                .into(mImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //image3
            case 3:
                if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
                    final Uri imageUri3 = data.getData();
                    resultUri3 = imageUri3;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri3);
                        Glide.with(getActivity().getApplication())
                                .load(bitmap) // Uri of the picture
                                .apply(RequestOptions.circleCropTransform())
                                .into(mImage3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }

    }



}
