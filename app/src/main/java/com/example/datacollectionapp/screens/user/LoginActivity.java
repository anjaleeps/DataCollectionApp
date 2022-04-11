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

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuthentication firebaseAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        firebaseAuthentication = FirebaseAuthentication.getInstance();
    }

    public void loginUser(View view) {
        EditText editEmail = findViewById(R.id.editEmail);
        EditText editPassword = findViewById(R.id.editPassword);

        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        firebaseAuthentication.signInUser(email, password, task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(this, ProjectListActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User sign in failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}