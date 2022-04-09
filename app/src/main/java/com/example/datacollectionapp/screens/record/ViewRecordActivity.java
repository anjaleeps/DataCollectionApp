package com.example.datacollectionapp.screens.record;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.database.connectionmanagers.RecordFirestoreManager;
import com.example.datacollectionapp.models.Project;
import com.example.datacollectionapp.models.RecordField;
import com.example.datacollectionapp.models.TemplateField;
import com.example.datacollectionapp.screens.projectrecords.ProjectRecordsActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewRecordActivity extends AppCompatActivity {
    private String TAG = "View Record";
    String projectId, recordId, projectName;
    private ProjectFirestoreManager projectFirestoreManager;
    private RecordFirestoreManager recordFirestoreManager;
    private List<TemplateField> formTemplate = new ArrayList<>();
    private List<RecordField> recordFields = new ArrayList<>();
    private RecyclerView recordRecycleView;
    private RecordFieldAdapter recordFieldAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_record);
        Intent intent = getIntent();
        projectId = intent.getStringExtra(ProjectRecordsActivity.PROJECT_ID);
        recordId = intent.getStringExtra(ProjectRecordsActivity.RECORD_ID);
        projectFirestoreManager = ProjectFirestoreManager.getInstance();
        recordFirestoreManager = RecordFirestoreManager.getInstance();
        getFormTemplate();
    }

    private void getFormTemplate() {
        projectFirestoreManager.getProjectById(projectId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                Project project = documentSnapshot.toObject(Project.class);
                projectName = project.getProjectName();
                formTemplate = project.getFormTemplate();
                setupRecordRecycleView();
            } else {
                Toast.makeText(this, "Error retrieving the form template", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Error retrieving the form template", task.getException());
            }
        });
    }

    private void setupRecordRecycleView() {
        recordRecycleView = findViewById(R.id.viewRecordRecyclerView);
        recordRecycleView.setLayoutManager(new LinearLayoutManager(this));
        recordFieldAdapter = new RecordFieldAdapter(this, recordFields);
        recordRecycleView.setAdapter(recordFieldAdapter);

        for (TemplateField templateField : formTemplate) {
            RecordField newRecordField = new RecordField(templateField.getFieldName(), templateField.getDatatype());
            recordFields.add(newRecordField);
        }

        recordFieldAdapter.notifyDataSetChanged();

    }


}