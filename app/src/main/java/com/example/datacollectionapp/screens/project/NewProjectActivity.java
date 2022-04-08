package com.example.datacollectionapp.screens.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.FirebaseAuthentication;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.models.Project;
import com.example.datacollectionapp.screens.formtemplate.NewFormTemplateActivity;
import com.example.datacollectionapp.screens.user.RegisterActivity;

public class NewProjectActivity extends AppCompatActivity {

    private ProjectFirestoreManager projectFirestoreManager;
    private FirebaseAuthentication firebaseAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        projectFirestoreManager = ProjectFirestoreManager.getInstance();
        firebaseAuthentication = FirebaseAuthentication.getInstance();
    }

    public void onStart() {
        super.onStart();
        if (!firebaseAuthentication.isUserSet()) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }


    public void createNewProject(View view) {
        EditText editProjectName = findViewById(R.id.editProjectName);
        String newProjectName = editProjectName.getText().toString();

        if (newProjectName.trim().isEmpty()) {
            Toast.makeText(NewProjectActivity.this, "Please enter a project name", Toast.LENGTH_SHORT).show();
            return;
        }

        final Project project = new Project();
        final String userId = firebaseAuthentication.getUserId();
        project.setUsername(userId);
        project.setProjectName(newProjectName);
        String projectId = projectFirestoreManager.createProject(project);
        project.setProjectId(projectId);

        Intent templateIntent = new Intent(NewProjectActivity.this, NewFormTemplateActivity.class);
        templateIntent.putExtra(NewFormTemplateActivity.PROJECT_DATA, project);
        startActivity(templateIntent);
    }
}