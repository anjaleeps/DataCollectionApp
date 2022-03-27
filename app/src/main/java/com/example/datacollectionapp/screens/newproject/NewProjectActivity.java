package com.example.datacollectionapp.screens.newproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.models.Project;
import com.example.datacollectionapp.screens.newformtemplate.NewFormTemplateActivity;
import com.google.firebase.firestore.DocumentReference;

public class NewProjectActivity extends AppCompatActivity {

    private ProjectFirestoreManager projectFirestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        projectFirestoreManager = ProjectFirestoreManager.getInstance();
    }

    public void createNewProject(View view) {
        EditText editProjectName = findViewById(R.id.editProjectName);
        String newProjectName = editProjectName.getText().toString();

        if (newProjectName.trim().isEmpty()) {
            Toast.makeText(NewProjectActivity.this, "Please enter a project name", Toast.LENGTH_SHORT).show();
            return;
        }

        final Project project = new Project();
        project.setUsername("user1");
        project.setProjectName(newProjectName);
        projectFirestoreManager.createProject(project, task -> {

            if (task.isSuccessful() && task.getResult() != null) {
                DocumentReference documentReference = task.getResult();
                project.setTaskId(documentReference.getId());
                Intent templateIntent = new Intent(NewProjectActivity.this, NewFormTemplateActivity.class);
                templateIntent.putExtra(NewFormTemplateActivity.PROJECT_DATA, project);
                startActivity(templateIntent);
            } else {
                Toast.makeText(NewProjectActivity.this, "Couldn't save the project! Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}