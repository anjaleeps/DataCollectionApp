package com.example.datacollectionapp.database.connectionmanagers;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class FirebaseStorageManager {

    private final long TEN_MEGABYTE = 1024 * 1024 * 10;
    private final FirebaseStorage firebaseStorage;
    private final StorageReference storageReference;
    private static FirebaseStorageManager instance;

    private FirebaseStorageManager() {
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = firebaseStorage.getReference();
    }

    public static FirebaseStorageManager getInstance() {
        if (instance == null) {
            instance = new FirebaseStorageManager();
        }
        return instance;
    }

    public void uploadImage(Uri filePath, OnCompleteListener<Uri> onCompleteListener) {
        StorageReference childReference = storageReference.child("images/" + UUID.randomUUID().toString());
        childReference.putFile(filePath)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return childReference.getDownloadUrl();
                })
                .addOnCompleteListener(onCompleteListener);
    }

    public void uploadAudio(Uri filePath, OnCompleteListener<Uri> onCompleteListener) {
        StorageReference childReference = storageReference.child("audio/" + UUID.randomUUID().toString());
        childReference.putFile(filePath)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return childReference.getDownloadUrl();
                })
                .addOnCompleteListener(onCompleteListener);
    }

    public void downloadImage(String downloadUri, OnCompleteListener<byte[]> onCompleteListener) {
        String fileName = getFileName(downloadUri);
        StorageReference childReference = storageReference.child("images/" + fileName);
        childReference
                .getBytes(TEN_MEGABYTE)
                .addOnCompleteListener(onCompleteListener);
    }

    public void downloadAudio(String downloadUri, OnCompleteListener<byte[]> onCompleteListener) {
        String fileName = getFileName(downloadUri);
        StorageReference childReference = storageReference.child("audio/" + fileName);
        childReference
                .getBytes(TEN_MEGABYTE)
                .addOnCompleteListener(onCompleteListener);
    }

    public String getFileName(String downloadUri) {
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(downloadUri);
        return storageReference.getName();
    }
}
