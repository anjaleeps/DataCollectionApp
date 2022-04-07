package com.example.datacollectionapp.screens.projectlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.screens.projectrecords.ProjectRecordsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class ProjectListActivity extends AppCompatActivity {

    private ProjectFirestoreManager projectFirestoreManager;
    private String TAG = "Project List";
    private ListView projectsListView;
    private ArrayAdapter projectNamesAdapter;
    ArrayList projectNames;
    ArrayList projects;
    String selectedProject;
    public static final String EXTRA_MESSAGE = "com.example.datacollectionapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        projectFirestoreManager = ProjectFirestoreManager.getInstance();
        getProjectList();
    }

    public void getProjectList(){
        projectFirestoreManager.getAllProjectsByUser("user1", onCompleteListener);
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
        projectNamesAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, projectNames);
        projectsListView.setAdapter(projectNamesAdapter);
        projectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedProject = (String) projects.get(i);
                Log.i(TAG,selectedProject);
                goToProjetRecords(selectedProject);
            }
        });
    }

    private void goToProjetRecords(String projectId){
        Intent intent = new Intent(this, ProjectRecordsActivity.class);
        intent.putExtra(EXTRA_MESSAGE, projectId);
        startActivity(intent);
    }

}