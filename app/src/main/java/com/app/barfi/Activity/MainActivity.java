package com.app.barfi.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.app.barfi.BuildConfig;
import com.app.barfi.Objects.CurrentUserObject;
import com.app.barfi.Objects.ScoreObject;

import com.app.barfi.Objects.UserObject;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.skyfishjy.library.RippleBackground;
import com.app.barfi.Fragments.CardFragment;
import com.app.barfi.Fragments.MatchesFragment;
import com.app.barfi.Fragments.OpenLocationSettings;
import com.app.barfi.Fragments.UserFragment;
import com.app.barfi.NewUserDetails;
import com.app.barfi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

/**
 * This Activity controls the display of main fragments of the app:
 *  -UserFragment
 *  -CardFragment
 *  -ChatFragment
 *
 *  It is also responsible for initializing the onesignal API for the current user
 */

public class MainActivity extends AppCompatActivity {

    //Used for city location only
    private FirebaseAuth mAuth;
    private String mUserId;
    private DatabaseReference mUserDatabase;

    public ImageView chatBadge;

    private Location lastKnownLocation ;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_tab_user,
            R.drawable.ic_tab_card,
            R.drawable.ic_tab_chat
    };


    public BillingClient billingClient;
    public List skuList = new ArrayList();
    public String sku = "inapp_premium_subs";
    private Boolean everPaid = false;


    private Integer updateCurrentDay = 0;

    //check internet connectivity
    private Integer i = 0;
    private ImageView mOffline;


    //admin premium access
    private Boolean admin = false;

    private TextView mLoading;
    private String city="";

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
        ImageView imageView = (ImageView) findViewById(R.id.centerImage);
        rippleBackground.startRippleAnimation();




        mLoading = findViewById(R.id.loadingText);
        mLoading.setVisibility(View.GONE);

        Handler loadHandler = new Handler();
        loadHandler.postDelayed(new Runnable() {
            public void run() {
                mLoading.setVisibility(View.VISIBLE);
            }
        }, 12000);



        chatBadge = findViewById(R.id.chatBadge);

        mOffline = findViewById(R.id.offline);
        mOffline.setVisibility(View.GONE);


        mAuth = FirebaseAuth.getInstance();
        mUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUserId);
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //check if name is updated name
                if (!dataSnapshot.hasChild("name") || !dataSnapshot.hasChild("profileImageUrl")) {
                    Intent intent = new Intent(MainActivity.this, NewUserDetails.class);
                    startActivity(intent);
                    //added later
                    finish();

                } else {

                    viewPager = findViewById(R.id.viewpager);

                    // Added to not refresh fragments 0 and 2
                    viewPager.setOffscreenPageLimit(2);

                    //testing: for introducing a global object from where any activity can extract data
                    ((CurrentUserObject) getApplication()).initialize(dataSnapshot);

                    setupViewPager(viewPager);

                    tabLayout = findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(viewPager);

                    updateCurrentDay = 1;

                    //Listener responsible for changing the color of the elected fragment's icon
                    tabLayout.addOnTabSelectedListener(
                            new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    super.onTabSelected(tab);
                                    int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorGray);
                                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                                    //change status bar theme

                                    if (tab == tabLayout.getTabAt(0)) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBgLightPinkCanvas, getApplication().getTheme()));
                                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBgLightPinkCanvas));
                                        }
                                    } else {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            getWindow().setStatusBarColor(getResources().getColor(R.color.colorDeadBackground, getApplication().getTheme()));
                                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            getWindow().setStatusBarColor(getResources().getColor(R.color.colorDeadBackground));
                                        }
                                    }


                                    if (tab == tabLayout.getTabAt(2))
                                        chatBadge.setVisibility(View.GONE);

                                }

                                @Override
                                public void onTabUnselected(TabLayout.Tab tab) {
                                    super.onTabUnselected(tab);
                                    int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.colorGrayMid);
                                    tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                                }

                                @Override
                                public void onTabReselected(TabLayout.Tab tab) {
                                    super.onTabReselected(tab);
                                }
                            }
                    );
                    setupTabIcons();


                    //Starts the custom view for each tab
                    for (int i = 0; i < tabLayout.getTabCount(); i++) {
                        TabLayout.Tab tab = tabLayout.getTabAt(i);
                        if (tab != null) tab.setCustomView(R.layout.view_home_tab);
                    }

                    //Makes it so that the first fragment displayed is the CardFragment
                    viewPager.setCurrentItem(1, false);

                    getPermissions();
                    isLocationEnable();

                    setupBillingClient();
                    //check app update
                    checkUpdate();



                    mLoading.setVisibility(View.GONE);
                    loadHandler.removeCallbacksAndMessages(null);
                    rippleBackground.stopRippleAnimation();
                    imageView.setVisibility(View.GONE);




                    ScoreObject mScore = new ScoreObject();
                    mScore.parseObject(dataSnapshot);
                    Integer score = mScore.getScore();
                    mUserDatabase.child("score").setValue(score);

                    //for chatBadge test
                    checkChatBadge();

                    //save the notificationID to the database
                    OneSignal.startInit(MainActivity.this).init();
                    OneSignal.sendTag("User_ID", mUserId);
                    OneSignal.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);
                    OneSignal.idsAvailable((userId, registrationId) -> FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationKey").setValue(userId));


                    //account status check and payment state checked
                    if (dataSnapshot.child("status").hasChild("everPaid")) {
                        if (dataSnapshot.child("status").child("everPaid").getValue().toString().equals("yes")) {
                            everPaid = true;
                        }
                    }


                    // calculate age
                    if(!dataSnapshot.hasChild("dob")){
                        Intent intent = new Intent(MainActivity.this, Birthday.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Calendar today = Calendar.getInstance();

                        int age = Integer.parseInt(dataSnapshot.child("age").getValue().toString());
                        int bDay = Integer.parseInt(dataSnapshot.child("dob").child("day").getValue().toString());
                        int bYear = Integer.parseInt(dataSnapshot.child("dob").child("year").getValue().toString());

                        int tAge = today.get(Calendar.YEAR) - bYear;
                        if (today.get(Calendar.DAY_OF_YEAR) < bDay){
                            tAge--;
                        }

                        if(tAge!=age)
                            mUserDatabase.child("age").setValue(tAge);
                    }

                   // if(!dataSnapshot.child("vc").getValue().toString().equals(BuildConfig.VERSION_CODE))
                    mUserDatabase.child("vc").setValue(BuildConfig.VERSION_CODE);

                    if (dataSnapshot.hasChild("admin"))
                        admin = true;





                }


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void checkChatBadge(){
        Query matchDb = mUserDatabase.child("connections").child("matches").orderByChild("chatted");
        matchDb.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    chatBadge.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    /**
     * if gps location is disabled then ask user to enable it with a dialog
     */
    public void isLocationEnable() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
        }

        if (!gps_enabled) {
            goToOpenSettings();
        }
        else {
            if(lastKnownLocation!=null)
                cardFragment.getCloseUsers(lastKnownLocation);
            else
                reCheckLastLocation = true;

            getLocation();
        }

        if (!isOnline())
            Toast.makeText(MainActivity.this, "Slow or no internet connection! Please check your internet connectivity.", Toast.LENGTH_LONG).show();

    }



    /**
     * Get Current User Location if location is enabled,
     * if it is available and the app is able to find a valid location
     * then update the user location's database with the most updated location
     */
    CardFragment cardFragment = new CardFragment();
    private Boolean reCheckLastLocation = false;

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           // Toast.makeText(MainActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }


        LocationGooglePlayServicesProvider provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);
        SmartLocation.with(this).location(provider).start(location -> {
            if (location != null && location != lastKnownLocation) {
                lastKnownLocation = location;
                if(reCheckLastLocation){
                    cardFragment.getCloseUsers(lastKnownLocation);
                    reCheckLastLocation = false;

                }

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("location");
                GeoFire geoFire = new GeoFire(ref);

                geoFire.setLocation(mUserId, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                    }
                });

                //forcing to take a manual location
                /*if ((location.getLatitude()<30 && location.getLatitude()>10) && (location.getLongitude()<90 && location.getLongitude()>60)){

                    geoFire.setLocation(FirebaseAuth.getInstance().getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {

                       @Override
                        public void onComplete(String key, DatabaseError error) {
                        }
                    });
                }
                else {
                    geoFire.setLocation(FirebaseAuth.getInstance().getUid(), new GeoLocation(13.0234517, 77.6582622), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                        }
                    });
                }*/


                // Get city to DB || causing problem in one plus
                try {
                    List<Address> addresses =null;

                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(addresses!=null && addresses.get(0).getLocality()!=null && addresses.get(0).getLocality().length()>0)
                    mUserDatabase.child("city").setValue(addresses.get(0).getLocality());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else if (location != lastKnownLocation) {


                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return; }
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if (lastKnownLocation != null && reCheckLastLocation) {
                    cardFragment.getCloseUsers(lastKnownLocation);
                    reCheckLastLocation = false;
                } else
                    isLocationEnable();
            }

        });
    }





    /**
     * Get Permissions needed to get location
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    isLocationEnable();

                } else {

                    //Enable setting manually
                    goToOpenSettings();

                    Toast.makeText(MainActivity.this, "Location permission required", Toast.LENGTH_SHORT).show();

                }
                return;
            }

        }
    }



    /**
     * Chooses the tab icons for each fragment
     */
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);


    }

    /**
     * Set up of the view pager and add the fragments to the view pager
     * @param viewPager - Layout manager of the fragments
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserFragment(), "ONE");
        adapter.addFragment(cardFragment, "TWO");
        adapter.addFragment(new MatchesFragment(), "THREE");
        viewPager.setAdapter(adapter);

    }



    /**
     * Controls the fragments being displayed
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        //Uncomment to display titles
        @Override
        public CharSequence getPageTitle(int position) {
            //return mFragmentTitleList.get(position);
            return null;
        }
    }




    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            // Toast.makeText(getContext(), "online", Toast.LENGTH_LONG).show();
            return (exitValue == 0);
        }
        catch (IOException e)          {
            e.printStackTrace(); }
        catch (InterruptedException e) {
            e.printStackTrace(); }

        Toast.makeText(MainActivity.this, "offline", Toast.LENGTH_LONG).show();
        return false;
    }



    private void goToOpenSettings() {
        Intent intent = new Intent(MainActivity.this, OpenLocationSettings.class);
        startActivity(intent);
        finish();

    }

    private void setupBillingClient() {

        // https://www.youtube.com/watch?v=hLYCrT4zzOY

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
            }
        }).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.SUBS);
                    if (purchasesResult.getPurchasesList() != null) {

                        boolean isOwned = false;
                        for (Purchase purchase : purchasesResult.getPurchasesList()) {
                            String thisSku = purchase.getSku();
                            if (thisSku.equals(sku)) {

                                String orderID = purchase.getOrderId();
                                String purchaseTime = String.valueOf(purchase.getPurchaseTime());

                                Map userInfo = new HashMap();
                                Map userInfo1 = new HashMap();
                                userInfo.put("level", "premium");

                                userInfo1.put("orderID", orderID);
                                userInfo1.put("productID", purchase.getSku());
                                userInfo1.put("purchaseState", purchase.getPurchaseState());
                                userInfo1.put("purchaseTime", purchaseTime);
                                userInfo1.put("autoRenewing", purchase.isAutoRenewing());
                                userInfo1.put("acknowledged", purchase.isAcknowledged());



                                mUserDatabase.child("status").updateChildren(userInfo);

                                FirebaseDatabase.getInstance().getReference().child("Purchases").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(userInfo);
                                FirebaseDatabase.getInstance().getReference().child("Purchases").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(orderID.replace(".","-")).updateChildren(userInfo1);


                                isOwned = true;
                            }

                           /* if (thisSku.equals(sku) && !purchase.isAutoRenewing()) {

                                Toast.makeText(MainActivity.this, "inactive & NOT auto renewing", Toast.LENGTH_LONG).show();
                                mUserDatabase.child("status").child("purchase").child("autoRenewing").setValue(purchase.isAutoRenewing());

                            }*/

                        }

                        if(!isOwned) {
                            mUserDatabase.child("status").child("level").setValue("basic");
                            if(everPaid)
                            FirebaseDatabase.getInstance().getReference().child("Purchases").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("level").setValue("basic");
                        }


                    } else
                        Toast.makeText(MainActivity.this, "*", Toast.LENGTH_LONG).show();


                } else mUserDatabase.child("status").child("level").setValue("basic");

            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }


    private void checkUpdate (){
        DatabaseReference versionDB  = FirebaseDatabase.getInstance().getReference().child("Version");
        versionDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int vc = Integer.parseInt(dataSnapshot.child("forceVC").getValue().toString());
                    Boolean forceUpdate = (Boolean) dataSnapshot.child("forceUpdate").getValue();

                    if(vc> BuildConfig.VERSION_CODE && forceUpdate){
                        Intent intent = new Intent(MainActivity.this, ForceUpdateActivity.class);
                        startActivity(intent);
                        finish();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();


        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        if(updateCurrentDay==1) {
            mUserDatabase.child("status").child("swipeClock").child("currentDay").setValue(currentDay);
            isLocationEnable();
        }


        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if (!is3g && !isWifi) {
            i=1;
            mOffline.setVisibility(View.VISIBLE);
            checkNetwork();
        }

        //set the user to premium if admin
        if(admin)
            mUserDatabase.child("status").child("level").setValue("premium");



    }





    private void checkNetwork(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
                boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();


                if (is3g || isWifi){
                    mOffline.setVisibility(View.GONE);
                    i = 0;
                }

                if(i==1)
                    handler.postDelayed(this,3000);
            }
        },3000);


    }



}
