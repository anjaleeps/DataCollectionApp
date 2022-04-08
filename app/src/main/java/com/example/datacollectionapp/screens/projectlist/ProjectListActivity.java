package com.example.datacollectionapp.screens.projectlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.FirebaseAuthentication;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.screens.project.NewProjectActivity;
import com.example.datacollectionapp.screens.projectrecords.ProjectRecordsActivity;
import com.example.datacollectionapp.screens.user.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class ProjectListActivity extends AppCompatActivity {

    private ProjectFirestoreManager projectFirestoreManager;
    private FirebaseAuthentication firebaseAuthentication;
    private String TAG = "Project List";
    private ListView projectsListView;
    private ProjectListAdapter projectListAdapter;
    ArrayList<String> projectNames;
    ArrayList<String> projects;
    public static final String EXTRA_MESSAGE = "com.example.datacollectionapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        projectFirestoreManager = ProjectFirestoreManager.getInstance();
        firebaseAuthentication = FirebaseAuthentication.getInstance();
        getProjectList();
    }

    public void onStart() {
        super.onStart();
        if (!firebaseAuthentication.isUserSet()) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    public void getProjectList(){
        final String userId = firebaseAuthentication.getUserId();
        projectFirestoreManager.getAllProjectsByUser(userId, onCompleteListener);
        projectNames = new ArrayList();
        projects = new ArrayList();
    }

    private OnCompleteListener onCompleteListener = new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    projectNames.add(String.valueOf(document.get("projectName")));
                    projects.add(document.getId());
                }
            listView();
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        }
    };

    private void listView(){
        projectsListView = (ListView) findViewById(R.id.projectList);
        projectListAdapter = new ProjectListAdapter(this, projectNames, projects);
        projectsListView.setAdapter(projectListAdapter);
    }

    public void createNewProject(View view) {
        Intent intent = new Intent(this, NewProjectActivity.class);
        startActivity(intent);
    }

    public void deleteProject(String projectId, int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Delete " + projectNames.get(position));
        alertDialog.setMessage("Are you sure you want to delete " + projectNames.get(position) + "?");

        alertDialog.setPositiveButton("Delete", (dialog, which) -> {
            projectFirestoreManager.deleteProject(projectId);
            dialog.dismiss();
            recreate();
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
           dialog.cancel();
        });

        alertDialog.show();
    }
}