package com.example.datacollectionapp.screens.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.FirebaseAuthentication;
import com.example.datacollectionapp.screens.projectlist.ProjectListActivity;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";
    private FirebaseAuthentication firebaseAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        firebaseAuthentication = FirebaseAuthentication.getInstance();
    }

    public void registerUser(View view) {
        EditText editEmail = findViewById(R.id.editEmail);
        EditText editPassword = findViewById(R.id.editPassword);

        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        firebaseAuthentication.createUser(email, password, task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(this, ProjectListActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToSignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}