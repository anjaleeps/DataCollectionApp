package com.example.datacollectionapp.screens.record;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.FirebaseAuthentication;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.database.connectionmanagers.RecordFirestoreManager;
import com.example.datacollectionapp.models.Project;
import com.example.datacollectionapp.models.Record;
import com.example.datacollectionapp.models.RecordField;
import com.example.datacollectionapp.models.TemplateField;
import com.example.datacollectionapp.screens.projectrecords.ProjectRecordsActivity;
import com.example.datacollectionapp.screens.user.RegisterActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewRecordActivity extends AppCompatActivity {

    private static final String timestampPattern = "dd-MM-yyyy HH:mm:ss";
    private String TAG = "View Record";
    String recordId;
    String projectId;
    Record record;
    private SimpleDateFormat dateFormat;
    private ProjectFirestoreManager projectFirestoreManager;
    private RecordFirestoreManager recordFirestoreManager;
    private FirebaseAuthentication firebaseAuthentication;
    private RecyclerView recordRecycleView;
    private RecordFieldAdapter recordFieldAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_record);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        projectFirestoreManager = ProjectFirestoreManager.getInstance();
        recordFirestoreManager = RecordFirestoreManager.getInstance();
        firebaseAuthentication = FirebaseAuthentication.getInstance();
        dateFormat = new SimpleDateFormat(timestampPattern);

        Intent intent = getIntent();
        recordId = intent.getStringExtra(ProjectRecordsActivity.RECORD_ID);
        projectId = intent.getStringExtra(ProjectRecordsActivity.PROJECT_ID);
        showRecord();
    }

    public void onStart() {
        super.onStart();
        if (!firebaseAuthentication.isUserSet()) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void showRecord() {
        recordFirestoreManager.getRecordById(projectId, recordId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                record = documentSnapshot.toObject(Record.class);
                setupRecordRecycleView();
            } else {
                Toast.makeText(this, "Error retrieving record data", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Error retrieving the record data", task.getException());
            }
        });
    }

    private void setupRecordRecycleView() {
        TextView textTimestamp = findViewById(R.id.textTimestamp);
        String recordTimestamp = dateFormat.format(record.getTimestamp().toDate());
        textTimestamp.setText(recordTimestamp);

        recordRecycleView = findViewById(R.id.recordRecyclerView);
        recordRecycleView.setLayoutManager(new LinearLayoutManager(this));
        recordFieldAdapter = new RecordFieldAdapter(this, record.getRecordFields(), RecordFieldAdapter.VIEW_RECORD);
        recordRecycleView.setAdapter(recordFieldAdapter);
        recordFieldAdapter.notifyDataSetChanged();
    }


}