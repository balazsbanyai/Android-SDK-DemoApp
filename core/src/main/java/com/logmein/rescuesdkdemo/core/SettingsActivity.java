package com.logmein.rescuesdkdemo.core;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.logmein.rescuesdkdemo.displaystreamingapp.rescuesdkdemo.core.R;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
