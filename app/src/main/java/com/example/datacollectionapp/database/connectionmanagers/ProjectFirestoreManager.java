package com.example.datacollectionapp.database.connectionmanagers;

import android.media.MediaPlayer;

import com.example.datacollectionapp.database.contracts.ProjectFirestoreContract;
import com.example.datacollectionapp.models.Project;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProjectFirestoreManager {

    private static ProjectFirestoreManager projectFirestoreManager;
    private final FirebaseFirestore firestore;
    private final CollectionReference collectionReference;

    private ProjectFirestoreManager() {
        this.firestore = FirebaseFirestore.getInstance();
        this.collectionReference = firestore.collection(ProjectFirestoreContract.COLLECTION_NAME);
    }

    public static ProjectFirestoreManager getInstance() {
        if (projectFirestoreManager == null) {
            projectFirestoreManager = new ProjectFirestoreManager();
        }
        return projectFirestoreManager;
    }

    public void createProject(Project project, OnCompleteListener<DocumentReference> onCompleteListener) {
        collectionReference.add(project).addOnCompleteListener(onCompleteListener);
    }

    public void getAllProjectsByUser(OnCompleteListener<QuerySnapshot> onCompleteListener, String username) {
        collectionReference.whereEqualTo(ProjectFirestoreContract.USERNAME, username)
                .orderBy(ProjectFirestoreContract.PROJECT_NAME)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void updateProject(Project project, OnCompleteListener<Void> onCompleteListener) {
        String projectId = project.getProjectId();
        DocumentReference documentReference = collectionReference.document(projectId);
        documentReference.set(project).addOnCompleteListener(onCompleteListener);
    }

    public void deleteProject(String projectId) {
        DocumentReference documentReference = collectionReference.document(projectId);
        documentReference.delete();
    }
}
