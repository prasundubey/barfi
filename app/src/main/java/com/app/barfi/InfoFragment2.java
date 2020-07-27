package com.app.barfi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.barfi.Activity.EditProfileActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.app.barfi.Objects.UserObject;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoFragment2 extends Fragment {

    //Params to change
    private Integer imageQuality = 35;
    private Integer imageTextPermit = 12;

    private ImageView mProfileImage;
    private ImageView mImage;
    private ImageView mImage3;

    private TextView mName;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    UserObject mUser = new UserObject();
    Button mNext2;


    private ProgressBar pgsBar;

    private Uri resultUri1;
    //image1
    private Uri resultUri2;
    //image3
    private Uri resultUri3;

    private ImageView mCross2,mCross3;

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

        pgsBar = view.findViewById(R.id.pBar);
        mNext2 = view.findViewById(R.id.next2);

        mCross2 = view.findViewById(R.id.cross2);
        mCross3 = view.findViewById(R.id.cross3);


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

        //on profile image click allow user to choose another pic by calling the responding intentt

        mProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
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
                pgsBar.setVisibility(View.VISIBLE);
                saveUserInformation();
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


                if(!mUser.getProfileImageUrl().equals("default")) {
                    Glide.with(getActivity().getApplicationContext()).load(mUser.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                    mNext2.setEnabled(true);
                }

                //Image1
                if(!mUser.getImageUrl().equals("default")) {
                    Glide.with(getActivity().getApplicationContext()).load(mUser.getImageUrl()).apply(RequestOptions.circleCropTransform()).into(mImage);
                    mCross2.setVisibility(View.VISIBLE);
                }

                //Image3
                if(!mUser.getImageUrl3().equals("default")) {
                    Glide.with(getActivity().getApplicationContext()).load(mUser.getImageUrl3()).apply(RequestOptions.circleCropTransform()).into(mImage3);
                    mCross3.setVisibility(View.VISIBLE);
                }


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

        if(resultUri1 != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmRotated1.compress(Bitmap.CompressFormat.JPEG, imageQuality, baos);
            byte[] fileInBytes = baos.toByteArray();

            UploadTask uploadTask = filePath.putBytes(fileInBytes);
           // UploadTask uploadTask = filePath.putFile(resultUri1);

            uploadTask.addOnFailureListener(e -> {
              //  getActivity().finish();

            });
            uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage = new HashMap();
                newImage.put("profileImageUrl", uri.toString());
                mUserDatabase.updateChildren(newImage);

               //if successful call next fragment.
                uploadSuccessful ();


            }).addOnFailureListener(exception -> {
              getActivity().finish();
            }));

        }else{
           // getActivity().finish();
        }


        // set new image for 2nd

        if(resultUri2 != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmRotated2.compress(Bitmap.CompressFormat.JPEG, imageQuality, baos);
            byte[] fileInBytes = baos.toByteArray();

            UploadTask uploadTask1 = filePath.putBytes(fileInBytes);

            uploadTask1.addOnFailureListener(e -> {
              //  getActivity().finish();
                return;
            });
            uploadTask1.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage1 = new HashMap();
                newImage1.put("imageUrl", uri.toString());
                mUserDatabase.updateChildren(newImage1);
               // getActivity().finish();

            }).addOnFailureListener(exception -> {
              // getActivity().finish();
            }));
        }else{
           // getActivity().finish();
        }


        if(resultUri3 != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images3").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmRotated3.compress(Bitmap.CompressFormat.JPEG, imageQuality, baos);
            byte[] fileInBytes = baos.toByteArray();

            UploadTask uploadTask1 = filePath.putBytes(fileInBytes);

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

    private void uploadSuccessful () {
        pgsBar.setVisibility(View.GONE);
        FragmentTransaction fr = getFragmentManager().beginTransaction();
        fr.replace(R.id.infoContainer, new InfoFragment3());
        fr.commit();
    }



            /**
     * Get the uri of the image the user picked if the result is successful
     * @param requestCode - code of the request ( for the image request is 1)
     * @param resultCode - if the result was successful
     * @param data - data of the image fetched
     */

            private InputImage image;
    private TextRecognizer recognizer = TextRecognition.getClient();
    private FaceDetector detector = FaceDetection.getClient();

    private Bitmap bitmap1,bitmap2,bitmap3;
    private Bitmap bmRotated1,bmRotated2,bmRotated3;
    private InputStream in1,in2,in3;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case 1:
                if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                    final Uri imageUri = data.getData();
                    resultUri1 = imageUri;
                    Toast.makeText(getContext(), "Analysing Image..", Toast.LENGTH_SHORT).show();
                    try {
                        bitmap1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri1);
                        image = InputImage.fromFilePath(getContext(), resultUri1);
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
                    Toast.makeText(getContext(), "Analysing Image..", Toast.LENGTH_SHORT).show();
                    try {
                        bitmap2 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri2);
                        image = InputImage.fromFilePath(getContext(), resultUri2);
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
                    Toast.makeText(getContext(), "Analysing Image..", Toast.LENGTH_SHORT).show();
                    try {
                        bitmap3 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri3);
                        image = InputImage.fromFilePath(getContext(), resultUri3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    detectFaces(image, 3);

                }
        }

    }

    public void detectFaces(InputImage image, Integer position) {
        Task<List<Face>> resultFace = detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(List<Face> faces) {
                if (!faces.isEmpty()) {
                    Toast.makeText(getContext(), "Face detected", Toast.LENGTH_SHORT).show();
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            Glide.with(getActivity().getApplication()).load(resultUri1).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                            mNext2.setEnabled(true);
                            break;
                        case 2:
                            Glide.with(getActivity().getApplication()).load(resultUri2).apply(RequestOptions.circleCropTransform()).into(mImage);
                            mCross2.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            Glide.with(getActivity().getApplication()).load(resultUri3).apply(RequestOptions.circleCropTransform()).into(mImage3);
                            mCross3.setVisibility(View.VISIBLE);
                            break;
                    }

                    ExifInterface exifInterface = null;
                    try {

                        switch (position) {
                            case 1:
                                in1 = getActivity().getContentResolver().openInputStream(resultUri1);
                                break;
                            case 2:
                                in1 = getActivity().getContentResolver().openInputStream(resultUri2);
                                break;
                            case 3:
                                in1 = getActivity().getContentResolver().openInputStream(resultUri3);
                                break;
                        }


                        //   in1 = getContentResolver().openInputStream(resultUri1);

                        exifInterface = new ExifInterface(in1);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Unknown error", Toast.LENGTH_SHORT).show();
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                }
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Unknown error", Toast.LENGTH_SHORT).show();
            }});
    }




    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

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
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    //analyse text
/*    private void processTextBlock(Text result) {
        String resultText = result.getText();
        if (resultText.length()>=imageTextPermit)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

//analyse image

  /*  private void processFaces(List<Face> faces) {
        for (Face face : faces) {
            Rect bounds = face.getBoundingBox();
            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
            if (leftEar != null) {
                PointF leftEarPos = leftEar.getPosition();
            }

            // If contour detection was enabled:
            List<PointF> leftEyeContour =
                    face.getContour(FaceContour.LEFT_EYE).getPoints();
            List<PointF> upperLipBottomContour =
                    face.getContour(FaceContour.UPPER_LIP_BOTTOM).getPoints();

            // If classification was enabled:
            if (face.getSmilingProbability() != null) {
                float smileProb = face.getSmilingProbability();
            }
            if (face.getRightEyeOpenProbability() != null) {
                float rightEyeOpenProb = face.getRightEyeOpenProbability();
            }

            // If face tracking was enabled:
            if (face.getTrackingId() != null) {
                int id = face.getTrackingId();
            }
        }

    }*/






}
