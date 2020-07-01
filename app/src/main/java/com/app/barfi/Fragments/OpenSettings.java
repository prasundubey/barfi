package com.app.barfi.Fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.barfi.R;



public class OpenSettings extends AppCompatActivity {

    private boolean gps_enabled = false;


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
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    finish();

                }else{
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    finish();
                }
            }

        });
    }

    public void onBackPressed() {
        // super.onBackPressed();
        Toast.makeText(getApplicationContext(), "Enable location permission continue", Toast.LENGTH_SHORT).show();
    }
}