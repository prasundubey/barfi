package com.app.barfi.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.app.barfi.Activity.PaymentActivity;
import com.app.barfi.Activity.WebViewActivity;
import com.app.barfi.Objects.CurrentUserObject;
import com.app.barfi.Objects.ScoreObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.app.barfi.Activity.ChatActivity;
import com.app.barfi.Objects.UserObject;
import com.app.barfi.Adapter.CardAdapter;
import com.app.barfi.Activity.MainActivity;
import com.app.barfi.R;
import com.app.barfi.Utils.SendNotification;
import com.app.barfi.Activity.ZoomCardActivity;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Activity that displays the cards to the user
 *
 * It displays them in a way that is within the search params of the current logged in user
 */
public class CardFragment  extends Fragment {

    private CardAdapter cardAdapter;
    int searchDistance = 2000;

    private FirebaseAuth mAuth;
    private String currentUId;
    private DatabaseReference mUserDatabase;
    private DatabaseReference usersDb;

    private Integer ageMax= 50;
    private Integer ageMin= 20;

    public static SwipeFlingAdapterView flingContainer;


    Dialog myDialog;


    UserObject user = new UserObject();
    UserObject currentProfile = new UserObject();
    private DatabaseReference mMatchDatabase;
    private String matchUId;

    private LinearLayout pgsBar;
    private TextView mNoPeople;
    private TextView mNotOnline;


    List<UserObject> rowItems;


    private Integer mLimit = 20,fLimit = 50, premiumLimit = 1000, swipesLimit = 10;
    private Integer swipes,totalSwipes=0;

    private Integer i = 0;

    private Integer currentDay = 1,swipeDay = 1;

    private Location fLastKnownLocation;

    private ProgressBar swipePgs;

    private Integer counter=0;


    private Integer dynamicRadius = 50;
    private List<String> closeUsers;
    private List<String> swipedUIDs;

    private Integer rowLimit = 10;

    private RippleBackground pgsWaves;
    private ImageView profileImage;

    private Integer rAdCount=0;



    View view;

    public CardFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_card, container, false);


        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null) return view;
        currentUId = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId);


        pgsWaves = (RippleBackground) view.findViewById(R.id.pgsWaves);
        profileImage = view.findViewById(R.id.dp);
        pgsWaves.startRippleAnimation();
        pgsWaves.setVisibility(View.VISIBLE);
        String profileImageUrl = ((CurrentUserObject) getActivity().getApplication()).getProfileImageUrl();
        if(!profileImageUrl.equals("default"))
            Glide.with(getApplicationContext()).load(profileImageUrl).apply(RequestOptions.circleCropTransform()).thumbnail(0.1f).into(profileImage);


        mNoPeople = (TextView) view.findViewById(R.id.noPeople);
        mNoPeople.setVisibility(View.GONE);

        mNotOnline = (TextView) view.findViewById(R.id.notOnline);
        mNotOnline.setVisibility(View.GONE);


        pgsBar = (LinearLayout) view.findViewById(R.id.pBar);

        swipePgs = view.findViewById(R.id.swipesPgs);
        swipePgs.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);
        swipePgs.setVisibility(View.GONE);


        LottieAnimationView animationView1 = view.findViewById(R.id.animationView1);
        LottieAnimationView animationView2 = view.findViewById(R.id.animationView2);

        myDialog = new Dialog(getActivity());

        rowItems = new ArrayList<>();

        initSwipes();
        fetchUserSearchParams();


        cardAdapter = new CardAdapter(getContext(), R.layout.item_card, rowItems );
        flingContainer = view.findViewById(R.id.frame);
        flingContainer.setAdapter(cardAdapter);
        FloatingActionButton fabLike = view.findViewById(R.id.fabLike);
        FloatingActionButton fabNope = view.findViewById(R.id.fabNope);

        //Handling swipe of cards
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                @Override
                public void removeFirstObjectInAdapter() {
                    Log.d("LIST", "removed object!");

                    if (swipes!=0) {
                        rowItems.remove(0);
                        cardAdapter.notifyDataSetChanged();
                        swipes--;
                        totalSwipes++;
                        usersDb.child(currentUId).child("status").child("totalSwipes").setValue(totalSwipes);
                        usersDb.child(currentUId).child("status").child("swipes").setValue(swipes);


                        if(cardAdapter.isEmpty()) {
                            mNoPeople.setVisibility(View.GONE);
                            pgsWaves.setVisibility(View.VISIBLE);

                            if(fLastKnownLocation!=null)
                                checkRadius();
                            else {
                                mNoPeople.setVisibility(View.VISIBLE);
                                pgsWaves.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Location error! Please re-open Barfi.", Toast.LENGTH_LONG).show();
                            }

                        } else mNoPeople.setVisibility(View.GONE);

                        //testing
                        if(swipes<5) {
                            swipePgs.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                        }
                        else
                            swipePgs.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);
                        swipePgs.setProgress((swipes*100)/swipesLimit);
                        swipePgs.setVisibility(View.VISIBLE);



                    } else {
                        Log.d("LIST", "object not removed!");
                        cardAdapter.notifyDataSetChanged();

                        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                        mUserDatabase.child("status").child("swipeClock").child("currentDay").setValue(currentDay);


                        rewardedAdsCount();

                        Intent intent = new Intent(view.getContext(), PaymentActivity.class);
                        intent.putExtra("lPremium", false);
                        intent.putExtra("count", adSession);
                        startActivity(intent);



                    }

                }

                @Override
                public void onLeftCardExit(Object dataObject) {

                    if (swipes!=0) {
                        animationView2.setVisibility(View.VISIBLE);
                        animationView2.playAnimation();
                        animationView2.addAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                animationView2.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        });

                        UserObject obj = (UserObject) dataObject;
                        String userId = obj.getUserId();

                        usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);

                        //save instance of UserId in own db
                        usersDb.child(currentUId).child("swipeHistory").child("nope").child(userId).setValue(true);
                    }

                }

                @Override
                public void onRightCardExit(Object dataObject) {

                    if (swipes!=0) {
                        animationView1.setVisibility(View.VISIBLE);
                        animationView1.playAnimation();
                        animationView1.addAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                animationView1.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        });

                        UserObject obj = (UserObject) dataObject;
                        String userId = obj.getUserId();
                        usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                        isConnectionMatch(userId);

                        //add uid to own DB
                        usersDb.child(currentUId).child("swipeHistory").child("yeps").child(userId).setValue(true);
                    }
                }

                @Override
                public void onAdapterAboutToEmpty(int itemsInAdapter) { }

                @Override
                public void onScroll(float scrollProgressPercent) { }
            });


        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> {
            UserObject UserObject = (UserObject) dataObject;
            Intent i = new Intent(getContext(), ZoomCardActivity.class);
            i.putExtra("UserObject", UserObject);
            i.putExtra("ShowFab", true);
            i.putExtra("CardFrag",true);
            startActivity(i);
        });


        //Listeners for the fab buttons, they do the same as the swipe feature, but withe the click of the buttons
        fabLike.setOnClickListener(v -> {
            if(rowItems.size()!=0)
                flingContainer.getTopCardListener().selectRight();
        });
        fabNope.setOnClickListener(v -> {
            if(rowItems.size()!=0)
                flingContainer.getTopCardListener().selectLeft();
        });


        swipedUIDs = ((CurrentUserObject) getApplicationContext()).getSwipedUIDs();


        //initialise ads
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        return view;
    }



    private void getSwipeLimit(){
         /*DatabaseReference mSwipeLimit = FirebaseDatabase.getInstance().getReference().child("Params");
        mSwipeLimit.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mLimit = Integer.parseInt(dataSnapshot.child("mLimit").getValue().toString());
                    fLimit = Integer.parseInt(dataSnapshot.child("fLimit").getValue().toString());
                    premiumLimit = Integer.parseInt(dataSnapshot.child("premiumLimit").getValue().toString());

                    Toast.makeText(getContext(), "Limit params", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        mUserDatabase.child("status").child("level").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    if (dataSnapshot.getValue().toString().equals("premium")) {
                        swipesLimit = premiumLimit;
                        swipes = swipesLimit;
                        mUserDatabase.child("status").child("swipes").setValue(swipes);

                    } else {
                        if (userInterest.equals("Female"))  //limit for male in general
                            swipesLimit = mLimit;
                        else swipesLimit = fLimit;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initSwipes() {

        getSwipeLimit();

        //check if swipeDate is there. if not add it.
        mUserDatabase.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (!dataSnapshot.child("swipeClock").hasChild("swipeDay")) {
                        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                        swipes = swipesLimit;
                        swipeDay = currentDay;
                        mUserDatabase.child("status").child("swipes").setValue(swipes);
                        mUserDatabase.child("status").child("swipeClock").child("swipeDay").setValue(swipeDay);
                        mUserDatabase.child("status").child("swipeClock").child("currentDay").setValue(currentDay);

                    } else {

                        if (dataSnapshot.child("swipes").getValue() != null)
                            swipes = Integer.parseInt(dataSnapshot.child("swipes").getValue().toString());
                        if (dataSnapshot.hasChild("totalSwipes"))
                            totalSwipes = Integer.parseInt(dataSnapshot.child("totalSwipes").getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkCurrentDay();

    }

    private void checkCurrentDay(){

        mUserDatabase.child("status").child("swipeClock").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("currentDay").exists() && dataSnapshot.child("swipeDay").exists()) {

                   getSwipeLimit();

                   currentDay = Integer.parseInt(dataSnapshot.child("currentDay").getValue().toString());
                   swipeDay = Integer.parseInt(dataSnapshot.child("swipeDay").getValue().toString());

                    if (!currentDay.equals(swipeDay)) {

                        swipes = swipesLimit;
                        swipeDay = currentDay;
                        mUserDatabase.child("status").child("swipes").setValue(swipes);
                        mUserDatabase.child("status").child("swipeClock").child("swipeDay").setValue(swipeDay);
                        updateScore();

                        adSession = 0;
                    }


                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    /**
     * Fetches user search params from the database
     *
     * After that call isLocationEnabled which will see if the location services are enabled
     * and then fetch the last location known.
     */

    private String  userInterest = "Male";

    private void fetchUserSearchParams(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid()).child("filters");

        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    counter = 0;
                    dynamicRadius = 20;
                    closeUsers = new ArrayList<>();

                  //((MainActivity)getActivity()).getLastKnownLocation();

                    if (dataSnapshot.child("interest").getValue() != null)
                        userInterest = dataSnapshot.child("interest").getValue().toString();
                    if (dataSnapshot.child("search_distance").getValue() != null)
                        searchDistance = Integer.parseInt(dataSnapshot.child("search_distance").getValue().toString());
                    if (dataSnapshot.child("ageMax").getValue() != null)
                        ageMax = Integer.parseInt(dataSnapshot.child("ageMax").getValue().toString());
                    if (dataSnapshot.child("ageMin").getValue() != null)
                        ageMin = Integer.parseInt(dataSnapshot.child("ageMin").getValue().toString());


                    if(rowItems!=null) {
                        rowItems.clear();
                        cardAdapter.notifyDataSetChanged();
                        mNoPeople.setVisibility(View.GONE);
                        pgsWaves.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * Fetch closest users to the current user using a GeoQuery.
     *
     * The users found are within a radius defined in the SearchObject and the center of
     * the radius is the current user's location
     * @param lastKnowLocation - user last know location
     */

    private Boolean geoQueryReady = false;
    GeoQuery geoQuery;
    public void getCloseUsers(Location lastKnowLocation){

        if(rowItems!=null && rowItems.size()>=rowLimit) return;

        //testing
        //Toast.makeText(getContext(), "GetCloseUsers entered" , Toast.LENGTH_LONG).show();

        fLastKnownLocation = lastKnowLocation;
        geoQueryReady = false;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("location");
        GeoFire geoFire = new GeoFire(ref);
        if (geoQuery != null)
            geoQuery.removeAllListeners();

        geoQuery = geoFire.queryAtLocation(new GeoLocation(fLastKnownLocation.getLatitude(), fLastKnownLocation.getLongitude()), dynamicRadius);

        //for testing & nudging
        /* if ((lastKnowLocation.getLatitude()<30 && lastKnowLocation.getLatitude()>10) && (lastKnowLocation.getLongitude()<90 && lastKnowLocation.getLongitude()>60))
                geoQuery = geoFire.queryAtLocation(new GeoLocation(lastKnowLocation.getLatitude(),lastKnowLocation.getLongitude()), dynamicRadius);
            else
                geoQuery = geoFire.queryAtLocation(new GeoLocation(13.0234517,77.6582622), dynamicRadius);
         */
         //Delhi
       // geoQuery = geoFire.queryAtLocation(new GeoLocation(28.8458429,77.0894171), dynamicRadius);


        //Bangalore
       // geoQuery = geoFire.queryAtLocation(new GeoLocation(13.0234517,77.6582622), dynamicRadius);
       // Toast.makeText(getContext(), "test location" , Toast.LENGTH_LONG).show();


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!closeUsers.contains(key) && !swipedUIDs.contains(key)){
                    getUsersInfo(key);
                    counter++;
                    closeUsers.add(key);
                }
            }
            @Override
            public void onKeyExited(String key) { }
            @Override
            public void onKeyMoved(String key, GeoLocation location) { }
            @Override
            public void onGeoQueryReady() {
                geoQueryReady = true;
              //  Toast.makeText(getContext(), "Query ended " + counter, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onGeoQueryError(DatabaseError error) {
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

    private Integer threadCounter = 0;
    private void getUsersInfo(String userId){

        for(UserObject mCard : rowItems){
            if(mCard.getUserId().equals(userId)){ counter--; checkRadius(); return;}
        }

        if(userId.equals(currentUId)) { counter--; checkRadius(); return; }

        //Checks if the UID is already swiped
       /* for(String Uid : swipedUIDs){
            if(userId.equals(Uid)) { counter--; checkRadius(); return; }
        }*/

        usersDb.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //testing
                threadCounter++;

                if(!dataSnapshot.exists()) { counter--; checkRadius(); return; }
                if(dataSnapshot.hasChild("xx")){ counter--; checkRadius(); return;}
                if(dataSnapshot.hasChild("hide")){ counter--;  checkRadius(); return;}
                if(!dataSnapshot.child("name").exists()){ counter--;  checkRadius(); return;}
                if(!dataSnapshot.child("score").exists()){ counter--;  checkRadius(); return;}
                if(!dataSnapshot.child("profileImageUrl").exists()){ counter--;  checkRadius(); return;}

                UserObject mUser = new UserObject();
                mUser.parseObject(dataSnapshot);

                int age = Integer.parseInt(mUser.getAge());

                //Swipe history check 2
                if(mUser.getUserId().equals(currentUId)){ counter--; checkRadius(); return;}
                if(dataSnapshot.child("connections").child("nope").hasChild(currentUId)){ counter--; checkRadius(); return;}
                if(dataSnapshot.child("connections").child("yeps").hasChild(currentUId)) { counter--; checkRadius(); return;}

                //Filters check
                if(!mUser.getUserSex().equals(userInterest) && !userInterest.equals("All")){ counter--; checkRadius(); return;}
                if(age>ageMax || age<ageMin){ counter--; checkRadius(); return;}

                /*for(UserObject mCard : rowItems){
                    if(mCard.getUserId().equals(userId)){ counter--;  checkRadius(); return;}
                }*/

                //Redundant check of existing IDs required to make sure of the IDs added later
                for(UserObject mCard : rowItems){
                    if(mCard.getUserId().equals(dataSnapshot.getKey())){ counter--; checkRadius(); return;}
                }

                rowItems.add(mUser);
                counter--;
                Collections.sort(rowItems, Collections.reverseOrder(new Comparator<UserObject>() {
                    @Override
                    public int compare(UserObject lhs, UserObject rhs) {
                        return lhs.getScore().compareTo(rhs.getScore());
                    }
                }));
                cardAdapter.notifyDataSetChanged();
                checkRadius();

                /*if(threadCounter>200)
                    Toast.makeText(getContext(), "thread "+ threadCounter +", Row count "+rowItems.size() + ", CloseUsers "+closeUsers.size()  , Toast.LENGTH_LONG).show();
*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if(cardAdapter.isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pgsWaves.setVisibility(View.GONE);
                    mNoPeople.setVisibility(View.VISIBLE);
                }
            }, 10000);
        } else
         pgsWaves.setVisibility(View.GONE);

    }


    private Integer checkRadiusCount =1;
    private void checkRadius() {
        //TO-DO: check this condition when the row is empty and null
        if( geoQueryReady && counter==0 && rowItems!=null && rowItems.size()<rowLimit ) {
            if (searchDistance > dynamicRadius) {
                dynamicRadius = dynamicRadius + 25*checkRadiusCount;
                getCloseUsers(fLastKnownLocation);

               // Toast.makeText(getContext(), "Check Radius Run " + checkRadiusCount, Toast.LENGTH_LONG).show();

                checkRadiusCount++;
            }
        }

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

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
                    usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);

                    //add match time stamp to the matches DB
                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("matchTimestamp").setValue(ServerValue.TIMESTAMP);
                    usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("matchTimestamp").setValue(ServerValue.TIMESTAMP);

                    SendNotification sendNotification = new SendNotification();
                    sendNotification.SendNotification("Check it out!", "New Barfi Connection!", dataSnapshot.getKey());

                    matchUId = userId;

                    ShowPopup(getView());
                    //Vibrate
                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = { 0, 1000, 1000 };
                    v.vibrate(pattern, -1);

                }
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
        myDialog.setContentView(R.layout.popup_connection);

        mChat = (Button) myDialog.findViewById(R.id.gochat);
        mBack = (TextView) myDialog.findViewById(R.id.back);
        mName = (TextView) myDialog.findViewById(R.id.name);

        mImage1 = (ImageView) myDialog.findViewById(R.id.image1);
        mImage2 = (ImageView) myDialog.findViewById(R.id.image2);

        //Image of user
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user.parseObject(dataSnapshot);

               if(!user.getProfileImageUrl().equals("default"))
                    Glide.with(getActivity().getApplicationContext()).load(user.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).thumbnail(0.1f).into(mImage1);
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
                    Glide.with(getActivity().getApplicationContext()).load(mUser.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).thumbnail(0.1f).into(mImage2);

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

    //update score on updating the info
    private void updateScore() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ScoreObject mScore = new ScoreObject();
                mScore.parseObject(dataSnapshot);
                Integer score = mScore.getScore();
                mUserDatabase.child("score").setValue(score);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          {
            e.printStackTrace(); }
        catch (InterruptedException e) {
            e.printStackTrace(); }

        Toast.makeText(getContext(), "offline", Toast.LENGTH_LONG).show();
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        //For 3G check
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        //For WiFi Check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if (!is3g && !isWifi) {
            rowItems.clear();
            cardAdapter.notifyDataSetChanged();
            mNoPeople.setVisibility(View.GONE);
            mNotOnline.setVisibility(View.VISIBLE);
            i=1;

            checkNetwork();
        }

    }

    private void checkNetwork(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
                boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();


                if (is3g || isWifi){
                    if(fLastKnownLocation!=null)
                        getCloseUsers(fLastKnownLocation);
                    else  Toast.makeText(getContext(), "Location error! Please re-open Barfi.", Toast.LENGTH_LONG).show();
                    mNotOnline.setVisibility(View.GONE);

                    mNoPeople.setVisibility(View.GONE);
                    pgsWaves.setVisibility(View.VISIBLE);

                    i = 0;
                }

                if(i==1)
                handler.postDelayed(this,3000);
            }
        },3000);


    }

    private Integer adSession = 0;
    public void rewardedAdsCount(){
        mUserDatabase.child("status").child("rAdCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(rAdCount==0 && snapshot.getValue()!=null){
                        rAdCount = Integer.parseInt(snapshot.getValue().toString());

                    } else if (Integer.parseInt(snapshot.getValue().toString()) == (rAdCount+2)) {
                        rAdCount+=2;
                        swipes = 5;
                        mUserDatabase.child("status").child("swipes").setValue(swipes);
                        Toast.makeText(getContext(), "5 Swipes Added", Toast.LENGTH_LONG).show();
                        adSession++;

                    } else if (rAdCount==1 && Integer.parseInt(snapshot.getValue().toString()) == (rAdCount+1)) {
                        rAdCount++;
                        swipes = 5;
                        mUserDatabase.child("status").child("swipes").setValue(swipes);
                        Toast.makeText(getContext(), "5 Swipes Added", Toast.LENGTH_LONG).show();
                        adSession++;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}