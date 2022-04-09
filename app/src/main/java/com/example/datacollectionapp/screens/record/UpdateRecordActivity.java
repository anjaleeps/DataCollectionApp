package com.example.datacollectionapp.screens.record;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.FirebaseAuthentication;
import com.example.datacollectionapp.database.connectionmanagers.FirebaseStorageManager;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.database.connectionmanagers.RecordFirestoreManager;
import com.example.datacollectionapp.models.Project;
import com.example.datacollectionapp.models.Record;
import com.example.datacollectionapp.models.RecordField;
import com.example.datacollectionapp.models.TemplateField;
import com.example.datacollectionapp.screens.projectrecords.ProjectRecordsActivity;
import com.example.datacollectionapp.screens.record.viewholder.AudioRecordViewHolder;
import com.example.datacollectionapp.screens.record.viewholder.ImageRecordViewHolder;
import com.example.datacollectionapp.screens.record.viewholder.LocationRecordViewHolder;
import com.example.datacollectionapp.screens.user.RegisterActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdateRecordActivity extends AppCompatActivity {

    public static final int CHOOSE_IMAGE_REQUEST = 32;
    public static final int CHOOSE_AUDIO_REQUEST = 64;
    private static final String timestampPattern = "dd-MM-yyyy HH:mm:ss";
    private String TAG = "UpdateRecord";
    String recordId;
    String projectId;
    Record record;
    private SimpleDateFormat dateFormat;
    private ProjectFirestoreManager projectFirestoreManager;
    private RecordFirestoreManager recordFirestoreManager;
    private FirebaseAuthentication firebaseAuthentication;
    private FirebaseStorageManager firebaseStorageManager;
    private RecyclerView recordRecycleView;
    private RecordFieldAdapter recordFieldAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_record);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        projectFirestoreManager = ProjectFirestoreManager.getInstance();
        recordFirestoreManager = RecordFirestoreManager.getInstance();
        firebaseAuthentication = FirebaseAuthentication.getInstance();
        firebaseStorageManager = FirebaseStorageManager.getInstance();
        dateFormat = new SimpleDateFormat(timestampPattern);

        Intent intent = getIntent();
        recordId = intent.getStringExtra(ProjectRecordsActivity.RECORD_ID);
        projectId = intent.getStringExtra(ProjectRecordsActivity.PROJECT_ID);
        showRecord();
    }

    public void onStart() {
        super.onStart();
        if (!firebaseAuthentication.isUserSet()) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void showRecord() {
        recordFirestoreManager.getRecordById(projectId, recordId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                record = documentSnapshot.toObject(Record.class);
                setupRecordRecycleView();
            } else {
                Toast.makeText(this, "Error retrieving record data", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Error retrieving the record data", task.getException());
            }
        });
    }

    private void setupRecordRecycleView() {
        TextView textTimestamp = findViewById(R.id.textTimestamp);
        String recordTimestamp = dateFormat.format(record.getTimestamp().toDate());
        textTimestamp.setText(recordTimestamp);

        recordRecycleView = findViewById(R.id.recordRecyclerView);
        recordRecycleView.setLayoutManager(new LinearLayoutManager(this));
        recordFieldAdapter = new RecordFieldAdapter(this, record.getRecordFields(), RecordFieldAdapter.UPDATE_RECORD);
        recordRecycleView.setAdapter(recordFieldAdapter);
        recordFieldAdapter.notifyDataSetChanged();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        int position;

        if (resultCode == RESULT_OK && intent.getData() != null) {
            Uri filePath = intent.getData();
            if ((CHOOSE_IMAGE_REQUEST & requestCode) != 0) {
                position = requestCode - CHOOSE_IMAGE_REQUEST;
                ImageRecordViewHolder imageRecordViewHolder =
                        (ImageRecordViewHolder) recordRecycleView.findViewHolderForAdapterPosition(position);
                assert imageRecordViewHolder != null;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imageRecordViewHolder.setImage(bitmap, filePath);
                } catch (FileNotFoundException e) {
                    Log.w(TAG, "ERROR: File not found in the provided address", e);
                } catch (IOException e) {
                    Log.w(TAG, "ERROR: IO exception while choosing the provided file", e);
                }
            } else if ((CHOOSE_AUDIO_REQUEST & requestCode) != 0) {
                position = requestCode - CHOOSE_AUDIO_REQUEST;
                AudioRecordViewHolder audioRecordViewHolder =
                        (AudioRecordViewHolder) recordRecycleView.findViewHolderForAdapterPosition(position);
                assert audioRecordViewHolder != null;
                audioRecordViewHolder.setAudio(filePath, getFileName(filePath));
            }
        }
    }

    public void updateRecord(View view) {
        recordFirestoreManager.updateRecord(record);
        for (int i = 0; i < record.getRecordFields().size(); i++) {
            Uri filePath;
            RecordField recordField = record.getRecordFields().get(i);
            switch (recordField.getDataType()) {
                case IMAGE:
                    filePath = ((ImageRecordViewHolder) Objects.requireNonNull(recordRecycleView.findViewHolderForAdapterPosition(i))).getFilePath();
                    if (filePath != null) {
                        uploadImage(record, filePath, i);
                    }
                    break;
                case AUDIO:
                    filePath = ((AudioRecordViewHolder) Objects.requireNonNull(recordRecycleView.findViewHolderForAdapterPosition(i))).getFilePath();
                    System.out.println(filePath.toString());
                    if (filePath != null) {
                        uploadAudio(record, filePath, i);
                    }
                case LOCATION:
                    ((LocationRecordViewHolder) Objects.requireNonNull(recordRecycleView.findViewHolderForAdapterPosition(i))).stopLocationUpdates();
            }
        }
        Intent intent = new Intent(this, ProjectRecordsActivity.class);
        intent.putExtra(ProjectRecordsActivity.PROJECT_ID, projectId);
        startActivity(intent);
    }

    private void uploadImage(Record record, Uri filePath, int position) {
        firebaseStorageManager.uploadImage(filePath, task -> {
            if (task.isSuccessful()) {
                Uri imageUri = task.getResult();
                record.getRecordFields().get(position).setValue(imageUri.toString());
                recordFirestoreManager.updateRecord(record);
            } else {
                Log.w(TAG, "ERROR: Failed to upload image", task.getException());
            }
        });
    }

    private void uploadAudio(Record record, Uri filePath, int position) {
        firebaseStorageManager.uploadAudio(filePath, task -> {
            if (task.isSuccessful()) {
                Uri audioUri = task.getResult();
                record.getRecordFields().get(position).setValue(audioUri.toString());
                recordFirestoreManager.updateRecord(record);
            } else {
                Log.w(TAG, "ERROR: Failed to upload audio file", task.getException());
            }
        });
    }

    private String getFileName(Uri filePath) {
        Cursor cursor = getContentResolver().query(filePath, null, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String fileName = cursor.getString(nameIndex);
        cursor.close();
        System.out.println(fileName);
        return fileName;
    }
}