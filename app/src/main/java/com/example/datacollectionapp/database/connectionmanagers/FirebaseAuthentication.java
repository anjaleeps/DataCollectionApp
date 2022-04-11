package com.example.datacollectionapp.database.connectionmanagers;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthentication {

    private FirebaseAuth firebaseAuth;
    private static FirebaseAuthentication firebaseAuthentication;

    private FirebaseAuthentication() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseAuthentication getInstance() {
        if (firebaseAuthentication == null) {
            firebaseAuthentication = new FirebaseAuthentication();
        }
        return firebaseAuthentication;
    }

    public void createUser(String email, String password, OnCompleteListener<AuthResult> onCompleteListener) {
        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(onCompleteListener);
    }

    public void signInUser(String email, String password, OnCompleteListener<AuthResult> onCompleteListener) {
        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(onCompleteListener);

    }

    public FirebaseUser getUser() {
        return firebaseAuth.getCurrentUser();
    }

    public String getUserId() {
        if (getUser() != null) {
            return getUser().getUid();
        }
        return null;
    }

    public boolean isUserSet() {
        if (getUser() == null) {
            return false;
        }
        return true;
    }
}
