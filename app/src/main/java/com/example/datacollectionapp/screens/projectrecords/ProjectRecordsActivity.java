package com.example.datacollectionapp.screens.projectrecords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.RecordFirestoreManager;
import com.example.datacollectionapp.screens.projectlist.ProjectListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectRecordsActivity extends AppCompatActivity {

    private RecordFirestoreManager recordFireStoreManager;
    String projectId, projectName;
    private String TAG = "Record List";
    List<String[]> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_records);
        data = new ArrayList<String[]>();
        Intent intent = getIntent();
        projectId = intent.getStringExtra(ProjectListActivity.Project_Id);
        projectName = intent.getStringExtra(ProjectListActivity.Project_Name);
        recordFireStoreManager = RecordFirestoreManager.getInstance();
        recordFireStoreManager.getRecordsByProject(projectId,onCompleteListener);

    }

    public void share(View view){
        String csv = createAndWriteFile();
        try {
            File file = new File(csv);
            Uri contentUri = FileProvider.getUriForFile(ProjectRecordsActivity.this, "com.example.datacollectionapp.provider", file);
            Log.d(TAG, String.valueOf(contentUri));
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            //sendIntent.setType("application/zip");
            sendIntent.setType("text/csv");
            startActivity(sendIntent);
        } catch (IllegalArgumentException e){
            Log.e("File Selector","The selected file can't be shared: " + e);
        }
    }

    private String createAndWriteFile(){
        String csv = (this.getFilesDir().getAbsolutePath() + "/"+projectName+".csv");
        CSVWriter writer = null;
        Log.d(TAG,csv);
        try {
            writer = new CSVWriter(new FileWriter(csv));
            writer.writeAll(data); // data is adding to csv
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csv;
    }

    private OnCompleteListener onCompleteListener = new OnCompleteListener<QuerySnapshot>() {
        String headers = "";
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot firstDocument = task.getResult().getDocuments().get(0);
                ArrayList forHeaders = (ArrayList) firstDocument.getData().get("recordFields");
                for(Object i : forHeaders ){
                    HashMap recordList = (HashMap) i;
                    headers = headers + "," +(String) recordList.get("fieldName");
                }
                String[] csvHeaders = headers.split(",");
                data.add(csvHeaders);
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String dataRow = "";
                    //Log.d(TAG, document.getId() + " => " + document.getData());
                    //Log.d(TAG, document.getId() + " => " + document.get("recordFields"));
                    ArrayList abc = (ArrayList) document.get("recordFields");
                    //Log.d(TAG, String.valueOf(abc.get(0).getClass().getName()));
                    // Log.d(TAG, String.valueOf(abc.get(0)));
                    Log.d(TAG, String.valueOf(abc));
                    for(Object i : abc){
                        HashMap recordList = (HashMap) i;
                        Log.d(TAG, String.valueOf(recordList.get("fieldName")));
                        dataRow += "," + (String) recordList.get("value");
                    }
                    String[] csvData = dataRow.split(",");
                    data.add(csvData);
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        }
    };
}