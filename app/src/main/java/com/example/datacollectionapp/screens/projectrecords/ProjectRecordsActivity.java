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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.RecordFirestoreManager;
import com.example.datacollectionapp.screens.projectlist.ProjectListActivity;
import com.example.datacollectionapp.screens.record.ViewRecordActivity;
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
    String projectId, projectName, recordId;
    private String TAG = "Record List";
    List<String[]> data;
    private ListView recordListView;
    private ArrayAdapter recordNamesAdapter;
    ArrayList recordNames;
    ArrayList recordIDs;
    ArrayList imageLinks;
    ArrayList audioLinks;
    String selectedRecord;
    public static final String Project_Id = "com.example.datacollectionapp.Project_Id";
    public static final String Record_Id = "com.example.datacollectionapp.Record_Id ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_records);
        data = new ArrayList<String[]>();
        recordNames = new ArrayList();
        recordIDs = new ArrayList();
        imageLinks = new ArrayList();
        audioLinks = new ArrayList();
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

    private void downloadImage(){
        String a = "https://firebasestorage.googleapis.com/v0/b/data-collection-app-6221e.appspot.com/o/images%2F079247ac-d16b-47dc-85b6-dabb860cfb29?alt=media&token=8187f920-1971-4032-92b5-b17df313146f";

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
                if (!task.getResult().getDocuments().isEmpty()){
                    DocumentSnapshot firstDocument = task.getResult().getDocuments().get(0);
                    ArrayList forHeaders = (ArrayList) firstDocument.getData().get("recordFields");
                    for(Object i : forHeaders ){
                        HashMap recordList = (HashMap) i;
                        headers = headers + "," +(String) recordList.get("fieldName");
                    }
                    String[] csvHeaders = headers.split(",");
                    data.add(csvHeaders);
                }
                int recordCount = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String dataRow = "";
                    //Log.d(TAG, document.getId() + " => " + document.getData());
                    recordIDs.add(document.getId());
                    ArrayList abc = (ArrayList) document.get("recordFields");
                    Log.d(TAG, String.valueOf(abc));
                    for(Object i : abc){
                        HashMap recordList = (HashMap) i;
                        Log.d(TAG, String.valueOf(recordList.get("fieldName")));
                        if(((String) recordList.get("dataType")).contains("IMAGE")){
                            imageLinks.add(recordList.get("value"));
                        }
                        else if(((String) recordList.get("dataType")).contains("AUDIO")){
                            audioLinks.add(recordList.get("value"));
                        }

                        dataRow += "," + (String) recordList.get("value");
                    }
                    recordCount += 1;
                    recordNames.add("Record " + String.valueOf(recordCount));
                    String[] csvData = dataRow.split(",");
                    data.add(csvData);
                    recordView();
                }

            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        }
    };

    private void recordView(){
        recordListView = (ListView) findViewById(R.id.recordListView);
        recordNamesAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, recordNames);
        recordListView.setAdapter(recordNamesAdapter);
        recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedRecord = (String) recordNames.get(i);
                //recordId = (String) recordIDs.get(i);
                Log.i(TAG,selectedRecord);
                goToViewRecord();
            }
        });
    }

    private void goToViewRecord(){
        Intent intent = new Intent(this, ViewRecordActivity.class);
        intent.putExtra(Project_Id, projectId);
        intent.putExtra(Record_Id, recordId);
        startActivity(intent);
    }
}