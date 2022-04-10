package com.example.datacollectionapp.screens.splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.screens.projectlist.ProjectListActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, ProjectListActivity.class);
        startActivity(intent);
        finish();
    }
}