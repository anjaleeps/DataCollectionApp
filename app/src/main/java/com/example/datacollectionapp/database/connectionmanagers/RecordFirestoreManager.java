package com.example.datacollectionapp.database.connectionmanagers;

import androidx.annotation.NonNull;

import com.example.datacollectionapp.database.contracts.RecordFirestoreContract;
import com.example.datacollectionapp.models.Record;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RecordFirestoreManager {

    private static RecordFirestoreManager recordFirestoreManager;
    private final FirebaseFirestore firebaseFirestore;
    private final CollectionReference collectionReference;

    private RecordFirestoreManager() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.collectionReference = firebaseFirestore.collection(RecordFirestoreContract.COLLECTION_NAME);
    }

    public static RecordFirestoreManager getInstance() {
        if (recordFirestoreManager == null) {
            recordFirestoreManager = new RecordFirestoreManager();
        }
        return recordFirestoreManager;
    }

    public String createRecord(Record record) {
        DocumentReference documentReference = collectionReference
                .document(record.getProjectId())
                .collection(RecordFirestoreContract.SUB_COLLECTION_NAME)
                .document();

        documentReference.set(record);
        return documentReference.getId();
    }

    public void getRecordById(String projectId, String recordId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        collectionReference
                .document(projectId)
                .collection(RecordFirestoreContract.SUB_COLLECTION_NAME)
                .document(recordId)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void getRecordsByProject(String projectId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        collectionReference
                .document(projectId)
                .collection(RecordFirestoreContract.SUB_COLLECTION_NAME)
                .whereEqualTo(RecordFirestoreContract.PROJECT_ID, projectId)
                .orderBy(RecordFirestoreContract.TIMESTAMP)
                .get()
                .addOnCompleteListener(onCompleteListener);

    }

    public void updateRecord(Record record) {
        collectionReference
                .document(record.getProjectId())
                .collection(RecordFirestoreContract.SUB_COLLECTION_NAME)
                .document(record.getRecordId())
                .set(record);
    }

    public void deleteRecord(String projectId, String recordId) {
        collectionReference
                .document(projectId)
                .collection(RecordFirestoreContract.SUB_COLLECTION_NAME)
                .document(recordId)
                .delete();
    }
}
