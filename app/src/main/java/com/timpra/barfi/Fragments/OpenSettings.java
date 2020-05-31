package com.timpra.barfi.Fragments;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.timpra.barfi.Activity.MainActivity;
import com.timpra.barfi.R;



public class OpenSettings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_settings);

        setContentView(R.layout.activity_open_settings);



        TextView mSettings = (TextView) findViewById(R.id.openSettings);

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                finish();
            }

        });
    }

    public void onBackPressed() {
        // super.onBackPressed();
        Toast.makeText(getApplicationContext(), "Enable location permission continue", Toast.LENGTH_SHORT).show();
    }
}