package com.timpra.barfi.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.timpra.barfi.Activity.ChatActivity;
import com.timpra.barfi.InfoFragment2;
import com.timpra.barfi.Objects.UserObject;
import com.timpra.barfi.Adapter.CardAdapter;
import com.timpra.barfi.Activity.MainActivity;
import com.timpra.barfi.R;
import com.timpra.barfi.Utils.SendNotification;
import com.timpra.barfi.Activity.ZoomCardActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;


/**
 * Activity that displays the cards to the user
 *
 * It displays them in a way that is within the search params of the current logged in user
 */
public class CardFragment  extends Fragment {

    private CardAdapter cardAdapter;
    int searchDistance = 100;

    private FirebaseAuth mAuth;
    private String currentUId;
    private DatabaseReference mUserDatabase;
    private DatabaseReference usersDb;

    private Integer ageMax= 50;
    private Integer ageMin= 20;


    Dialog myDialog;

    //private Button mPopup;

    UserObject user = new UserObject();
    UserObject currentProfile = new UserObject();
    private DatabaseReference mMatchDatabase;
    private String matchUId;

    private ProgressBar pgsBar;


    List<UserObject> rowItems;

    View view;

    public CardFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_card, container, false);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null)
            return view;
        currentUId = mAuth.getCurrentUser().getUid();



        fetchUserSearchParams();

        rowItems = new ArrayList<>();

        cardAdapter = new CardAdapter(getContext(), R.layout.item_card, rowItems );

        final SwipeFlingAdapterView flingContainer = view.findViewById(R.id.frame);

        flingContainer.setAdapter(cardAdapter);

        //Handling swipe of cards
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                cardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                UserObject obj = (UserObject) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                UserObject obj = (UserObject) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                isConnectionMatch(userId);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> {
            UserObject UserObject = (UserObject) dataObject;
            Intent i = new Intent(getContext(), ZoomCardActivity.class);
            i.putExtra("UserObject", UserObject);
            startActivity(i);
        });



        //loading bar

        pgsBar = (ProgressBar) view.findViewById(R.id.pBar);
        pgsBar.setVisibility(View.VISIBLE);




        myDialog = new Dialog(getActivity());

       /* mPopup = (Button) view.findViewById(R.id.popup);
        mPopup.setOnClickListener(v -> {
            ShowSwipePopup(v);
        });*/


        FloatingActionButton fabLike = view.findViewById(R.id.fabLike);
        FloatingActionButton fabNope = view.findViewById(R.id.fabNope);

        //Listeners for the fab buttons, they do the same as the swipe feature, but withe the click of the buttons
        fabLike.setOnClickListener(v -> {
            if(rowItems.size()!=0)
                flingContainer.getTopCardListener().selectRight();
        });
        fabNope.setOnClickListener(v -> {
            if(rowItems.size()!=0)
                flingContainer.getTopCardListener().selectLeft();
        });

        return view;
    }


    GeoQuery geoQuery;
    /**
     * Fetch closest users to the current user using a GeoQuery.
     *
     * The users found are within a radius defined in the SearchObject and the center of
     * the radius is the current user's location
     * @param lastKnowLocation - user last know location
     */
    public void getCloseUsers(Location lastKnowLocation){
        rowItems.clear();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("location");
        GeoFire geoFire = new GeoFire(ref);

        if(geoQuery!=null)
            geoQuery.removeAllListeners();
        geoQuery = geoFire.queryAtLocation(new GeoLocation(lastKnowLocation.getLatitude(),lastKnowLocation.getLongitude()), 100);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                getUsersInfo(key);
            }
            @Override
            public void onKeyExited(String key) {
            }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }
            @Override
            public void onGeoQueryReady() {
            }
            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }

    /**
     * Checks if new connection is a match if it is then add it to the database and create a new chat
     * @param userId
     */
    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yeps").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    // Toast.makeText(getContext(), "New Connection", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
                    usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);

                    SendNotification sendNotification = new SendNotification();
                    sendNotification.SendNotification("check it out!", "new Connection!", dataSnapshot.getKey());

                    matchUId = userId;

                    ShowPopup(getView());
                    //Vibrate
                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = { 0, 3000, 3000 };
                    v.vibrate(pattern, -1);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String  userInterest = "Male";

    /**
     * Fetches user search params from the database
     *
     * After that call isLocationEnabled which will see if the location services are enabled
     * and then fetch the last location known.
     */
    private void fetchUserSearchParams(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if (dataSnapshot.child("interest").getValue() != null)
                        userInterest = dataSnapshot.child("interest").getValue().toString();
                    if (dataSnapshot.child("search_distance").getValue() != null)
                        searchDistance = Integer.parseInt(dataSnapshot.child("search_distance").getValue().toString());

                   // check user age max & min
                    if (dataSnapshot.child("ageMax").getValue() != null)
                        ageMax = Integer.parseInt(dataSnapshot.child("ageMax").getValue().toString());

                    if (dataSnapshot.child("ageMin").getValue() != null)
                        ageMin = Integer.parseInt(dataSnapshot.child("ageMin").getValue().toString());

                    // the line below throwing error
                    ((MainActivity)getActivity()).isLocationEnable();


                    rowItems.clear();
                    cardAdapter.notifyDataSetChanged();


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Get info of a user and check if that user is within the search params, if it is then
     * add it to the list and update the adapter.
     *
     * Does not add the user if it is already a connection.
     * @param userId - id of the user that's a possible user to display the card of
     */
    private void getUsersInfo(String userId){
        for(UserObject mCard : rowItems){
            if(mCard.getUserId().equals(userId)){return;}
        }
        usersDb.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){return;}

                for(UserObject mCard : rowItems){
                    if(mCard.getUserId().equals(dataSnapshot.getKey())){return;}
                }

                UserObject mUser = new UserObject();
                mUser.parseObject(dataSnapshot);


                pgsBar.setVisibility(View.GONE);

                Integer age = Integer.parseInt(mUser.getAge());
                //user age range

                if(mUser.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){return;}
                if(dataSnapshot.child("connections").child("nope").hasChild(currentUId)){return;}
                if(dataSnapshot.child("connections").child("yeps").hasChild(currentUId)) {return;}
                if(!mUser.getUserSex().equals(userInterest) && !userInterest.equals("Both")){return;}
                if(age>ageMax || age<ageMin){return;}

                for(UserObject mCard : rowItems){
                    if(mCard.getUserId().equals(userId)){return;}
                }

                rowItems.add(mUser);
                cardAdapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void ShowPopup(View v) {

        Button mChat;
        TextView mBack;
        TextView mName;
        ImageView mImage1;
        ImageView mImage2;
        myDialog.setContentView(R.layout.connection_popup);

        mChat = (Button) myDialog.findViewById(R.id.gochat);
        mBack = (TextView) myDialog.findViewById(R.id.back);
        mName = (TextView) myDialog.findViewById(R.id.name);

        mImage1 = (ImageView) myDialog.findViewById(R.id.image1);
        mImage2 = (ImageView) myDialog.findViewById(R.id.image2);

        //Image of user
        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user.parseObject(dataSnapshot);

               if(!user.getProfileImageUrl().equals("default"))
                    Glide.with(getActivity().getApplicationContext()).load(user.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(mImage1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //image of current match profile

        usersDb.child(matchUId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserObject mUser = new UserObject();
                mUser.parseObject(dataSnapshot);

                if(!mUser.getProfileImageUrl().equals("default"))
                    Glide.with(getActivity().getApplicationContext()).load(mUser.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(mImage2);

                mName.setText(mUser.getName() + " likes you too.");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO chat window
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("matchId", matchUId);
                view.getContext().startActivity(intent);

                myDialog.dismiss();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();


    }



}