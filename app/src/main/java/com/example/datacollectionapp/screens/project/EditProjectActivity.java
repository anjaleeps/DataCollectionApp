package com.example.datacollectionapp.screens.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.models.Project;
import com.example.datacollectionapp.screens.projectlist.ProjectListActivity;
import com.example.datacollectionapp.screens.projectlist.ProjectListAdapter;

public class EditProjectActivity extends AppCompatActivity {

    public static final String TAG = "EditProjectActivity";
    private ProjectFirestoreManager projectFirestoreManager;
    private String projectId;
    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        projectFirestoreManager = ProjectFirestoreManager.getInstance();

        Intent intent = getIntent();
        projectId = intent.getStringExtra(ProjectListActivity.EXTRA_MESSAGE);
        fillProjectDetails();
    }

    private void fillProjectDetails() {
        EditText editProjectName = findViewById(R.id.updateProjectName);

        projectFirestoreManager.getProjectById(projectId, task -> {
            if (task.isSuccessful()) {
                project = task.getResult().toObject(Project.class);
                editProjectName.setText(project.getProjectName());
            }
            else {
                Log.e(TAG, "Failed to retrieve project details");
                Toast.makeText(this,"Failed to retrieve project details! Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateProject(View view) {
        EditText editProjectName = findViewById(R.id.updateProjectName);
        String newProjectName = editProjectName.getText().toString().trim();
        if (project != null && !newProjectName.isEmpty()) {
            project.setProjectName(newProjectName);
            projectFirestoreManager.updateProject(project);

            Intent intent = new Intent(this, ProjectListActivity.class);
            startActivity(intent);
        } else if (project == null) {
            Toast.makeText(this, "Project not loaded yet! Please try again", Toast.LENGTH_SHORT).show();
        } else if (newProjectName.isEmpty()) {
            Toast.makeText(this, "Please enter a project name", Toast.LENGTH_SHORT).show();
        }
    }
}