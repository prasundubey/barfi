package com.app.barfi.Fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.barfi.Activity.MainActivity;
import com.app.barfi.Activity.WebViewActivity;
import com.app.barfi.R;

import java.util.Calendar;
import java.util.Objects;


public class OpenLocationSettings extends AppCompatActivity {

    private boolean gps_enabled = false;
    Boolean stop = false;
    private Boolean clicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_settings);


        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }


        TextView mSettings = (TextView) findViewById(R.id.openSettings);

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!gps_enabled) {
                    clicked = true;
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    finish();

                }else{
                    clicked = true;
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    finish();
                }
            }

        });


        //check internet on 5 sec intervals

      /*  Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception ex) {
                }

               if(Build.VERSION.SDK_INT >= 23
                       && ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED
                       && ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                       && gps_enabled) {

                   stop = true;

                   Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                   startActivity(intent);
                   finish();

               }if(Build.VERSION.SDK_INT < 23)
                   stop = true;


                if (!stop && clicked) {
                    handler.postDelayed(this,5000);
                }


            }
        },5000);*/


    }


    @Override
    protected void onResume() {
        super.onResume();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if(Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && gps_enabled) {


            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }


    }

    public void onBackPressed() {
        // super.onBackPressed();
        Toast.makeText(getApplicationContext(), "Enable location permission continue", Toast.LENGTH_SHORT).show();
    }
}