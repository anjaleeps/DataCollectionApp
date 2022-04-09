package com.example.datacollectionapp.screens.projectrecords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.FirebaseAuthentication;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.database.connectionmanagers.RecordFirestoreManager;
import com.example.datacollectionapp.models.Project;
import com.example.datacollectionapp.models.Record;
import com.example.datacollectionapp.models.RecordField;
import com.example.datacollectionapp.screens.formtemplate.NewFormTemplateActivity;
import com.example.datacollectionapp.screens.record.NewRecordActivity;
import com.example.datacollectionapp.screens.user.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectRecordsActivity extends AppCompatActivity {

    private RecordFirestoreManager recordFireStoreManager;
    private FirebaseAuthentication firebaseAuthentication;
    private ProjectFirestoreManager projectFirestoreManager;
    private Project project;
    private String projectId;
    private String TAG = "Record List";
    private List<Record> recordList = new ArrayList<>();
    List<String[]> data;
    private ListView recordListView;
    private ProjectRecordsAdapter projectRecordsAdapter;
    ArrayList<String> recordNames;
    ArrayList<String> recordIDs;
    ArrayList<String> imageLinks;
    ArrayList<String> audioLinks;
    public static final String PROJECT_ID = "com.example.datacollectionapp.Project_Id";
    public static final String PROJECT_NAME = "com.example.datacollectionapp.Project_Id";
    public static final String RECORD_ID = "com.example.datacollectionapp.Record_Id ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_records);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        data = new ArrayList<>();
        recordNames = new ArrayList();
        recordIDs = new ArrayList<>();
        imageLinks = new ArrayList<>();
        audioLinks = new ArrayList<>();
        Intent intent = getIntent();
//        projectId = intent.getStringExtra(ProjectListActivity.PROJECT_ID);
        projectId = "BlQEPLJfcHY9Fxd8A1XR";
        recordFireStoreManager = RecordFirestoreManager.getInstance();
        projectFirestoreManager = ProjectFirestoreManager.getInstance();
        firebaseAuthentication = FirebaseAuthentication.getInstance();

        checkFormTemplate();
    }

    public void onStart() {
        super.onStart();
        if (!firebaseAuthentication.isUserSet()) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    public void checkFormTemplate() {
        projectFirestoreManager.getProjectById(projectId, task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    project = task.getResult().toObject(Project.class);
                    if (project.getFormTemplate() != null && project.getFormTemplate().size() > 0) {
                        recordFireStoreManager.getRecordsByProject(projectId,onCompleteListener);
                    } else {
                        Intent intent = new Intent(this, NewFormTemplateActivity.class);
                        intent.putExtra(NewFormTemplateActivity.PROJECT_DATA, project);
                        startActivity(intent);
                    }
                } else {
                    Log.e(TAG, "No project with the given ID " + projectId);
                }

            } else {
                Log.e(TAG, "Couldn't retrieve project details", task.getException());
            }
        });
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
        List<String[]> data = getData();
        String csv = (this.getFilesDir().getAbsolutePath() + "/"+project.getProjectName()+".csv");
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

    private List<String[]> getData() {
        List<String[]> data = new ArrayList<>();
        String headers = "";
        for (RecordField recordField : recordList.get(0).getRecordFields()) {
            headers = headers + "," + recordField.getFieldName();
        }
        String[] csvHeaders = headers.split(",");
        data.add(csvHeaders);

        for (Record record : recordList) {
            String dataRow = "";
            for (RecordField recordField : record.getRecordFields()) {
                switch (recordField.getDataType()) {
                    case IMAGE:
                        imageLinks.add(recordField.getValue());
                    case AUDIO:
                        audioLinks.add(recordField.getValue());
                }

                dataRow += "," + recordField.getValue();
            }
            String[] csvDataRow = dataRow.split(",");
            data.add(csvDataRow);
        }
        return data;
    }

    private OnCompleteListener onCompleteListener = new OnCompleteListener<QuerySnapshot>() {

        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Record record = documentSnapshot.toObject(Record.class);
                        recordList.add(record);
                    }
                    showRecordList();
                } else {
                    Log.e(TAG, "Error retrieving record details", task.getException());
                }
            }
        }
    };

    private void showRecordList(){
        TextView textProjectName = findViewById(R.id.textProjectName);
        textProjectName.setText(project.getProjectName());
        recordListView = (ListView) findViewById(R.id.recordListView);
        projectRecordsAdapter = new ProjectRecordsAdapter(this, recordList);
        recordListView.setAdapter(projectRecordsAdapter);
    }

    public void deleteRecord(String recordId, int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Delete Record");
        alertDialog.setMessage("Are you sure you want to delete this record?");

        alertDialog.setPositiveButton("Delete", (dialog, which) -> {
            recordFireStoreManager.deleteRecord(projectId, recordId);
            dialog.dismiss();
            recreate();
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        alertDialog.show();
    }

    public void addNewRecord(View view) {
        Intent intent = new Intent(this, NewRecordActivity.class);
        intent.putExtra(NewRecordActivity.PROJECT_ID, projectId);
        startActivity(intent);
    }
}