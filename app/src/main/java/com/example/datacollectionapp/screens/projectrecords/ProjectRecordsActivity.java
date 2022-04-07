package com.example.datacollectionapp.screens.projectrecords;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.RecordFirestoreManager;

public class ProjectRecordsActivity extends AppCompatActivity {

    private RecordFirestoreManager recordFireStoreManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_records);
        recordFireStoreManager = RecordFirestoreManager.getInstance();
    }

    public void share(){
        //recordFireStoreManager.getRecordsByProject();
    }
}