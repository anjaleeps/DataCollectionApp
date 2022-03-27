package com.example.datacollectionapp.database.connectionmanagers;

import com.example.datacollectionapp.database.contracts.ProjectFirestoreContract;
import com.example.datacollectionapp.models.Project;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

    public String createProject(Project project) {
        DocumentReference documentReference = collectionReference.document();
        documentReference.set(project);
        return documentReference.getId();
    }

    public void getProjectById(String projectId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        collectionReference
                .document(projectId)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void getAllProjectsByUser(OnCompleteListener<QuerySnapshot> onCompleteListener, String username) {
        collectionReference.whereEqualTo(ProjectFirestoreContract.USERNAME, username)
                .orderBy(ProjectFirestoreContract.PROJECT_NAME)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void updateProject(Project project) {
        String projectId = project.getProjectId();
        DocumentReference documentReference = collectionReference.document(projectId);
        documentReference.set(project);
    }

    public void deleteProject(String projectId) {
        DocumentReference documentReference = collectionReference.document(projectId);
        documentReference.delete();
    }
}
