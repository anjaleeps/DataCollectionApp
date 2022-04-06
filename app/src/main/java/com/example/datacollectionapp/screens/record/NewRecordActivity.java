package com.example.datacollectionapp.screens.record;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
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
import android.widget.Toast;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.FirebaseStorageManager;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.database.connectionmanagers.RecordFirestoreManager;
import com.example.datacollectionapp.models.Project;
import com.example.datacollectionapp.models.Record;
import com.example.datacollectionapp.models.RecordField;
import com.example.datacollectionapp.models.TemplateField;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NewRecordActivity extends AppCompatActivity {

    public static final String PROJECT_ID = "PROJECT_ID";
    public static final int CHOOSE_IMAGE_REQUEST = 32;
    public static final int CHOOSE_AUDIO_REQUEST = 64;

    public static final String TAG = "NewRecordActivity";

    private String projectId;
    private String projectName;
    private RecyclerView recordRecycleView;
    private RecordFieldAdapter recordFieldAdapter;
    private List<TemplateField> formTemplate = new ArrayList<>();
    private HashMap<TemplateField, Integer> fieldIds = new HashMap<>();
    private List<RecordField> recordFields = new ArrayList<>();
    private ProjectFirestoreManager projectFirestoreManager;
    private RecordFirestoreManager recordFirestoreManager;
    private FirebaseStorageManager firebaseStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

//        Intent intent = getIntent();
//        projectId = intent.getStringExtra(PROJECT_ID);
        projectId = "TmFPKp8At1v6SCarpxeR";
        projectFirestoreManager = ProjectFirestoreManager.getInstance();
        recordFirestoreManager = RecordFirestoreManager.getInstance();
        firebaseStorageManager = FirebaseStorageManager.getInstance();
        getFormTemplate();
    }

    private void getFormTemplate() {
        projectFirestoreManager.getProjectById(projectId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                Project project = documentSnapshot.toObject(Project.class);
                projectName = project.getProjectName();
                formTemplate = project.getFormTemplate();
                setupRecordRecycleView();
            } else {
                Toast.makeText(this, "Error retrieving the form template", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Error retrieving the form template", task.getException());
            }
        });
    }

    private void setupRecordRecycleView() {
        recordRecycleView = findViewById(R.id.recordRecyclerView);
        recordRecycleView.setLayoutManager(new LinearLayoutManager(this));
        recordFieldAdapter = new RecordFieldAdapter(this, recordFields);
        recordRecycleView.setAdapter(recordFieldAdapter);

        for (TemplateField templateField : formTemplate) {
            RecordField newRecordField = new RecordField(templateField.getFieldName(), templateField.getDatatype());
            recordFields.add(newRecordField);
        }

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
                    Log.w(NewRecordActivity.TAG, "ERROR: File not found in the provided address", e);
                } catch (IOException e) {
                    Log.w(NewRecordActivity.TAG, "ERROR: IO exception while choosing the provided file", e);
                }
            } else if ((CHOOSE_AUDIO_REQUEST & requestCode) != 0) {
                position = requestCode - CHOOSE_AUDIO_REQUEST;
                System.out.println(position);
                AudioRecordViewHolder audioRecordViewHolder =
                        (AudioRecordViewHolder) recordRecycleView.findViewHolderForAdapterPosition(position);
                assert audioRecordViewHolder != null;
                audioRecordViewHolder.setAudio(filePath, getFileName(filePath));

            }
        }
    }

    public void saveRecord(View view) {
        Record newRecord = new Record();

        newRecord.setProjectId(projectId);
        newRecord.setRecordFields(recordFields);
        String recordId = recordFirestoreManager.createRecord(newRecord);
        newRecord.setRecordId(recordId);

        for (int i = 0; i < recordFields.size(); i++) {
            Uri filePath;
            RecordField recordField = recordFields.get(i);
            switch (recordField.getDataType()) {
                case IMAGE:
                    filePath = ((ImageRecordViewHolder) Objects.requireNonNull(recordRecycleView.findViewHolderForAdapterPosition(i))).getFilePath();
                    if (filePath != null) {
                        uploadImage(newRecord, filePath, i);
                    }
                    break;
                case AUDIO:
                    filePath = ((AudioRecordViewHolder) Objects.requireNonNull(recordRecycleView.findViewHolderForAdapterPosition(i))).getFilePath();
                    System.out.println(filePath.toString());
                    if (filePath != null) {
                        uploadAudio(newRecord, filePath, i);
                    }
            }
        }
        //Intent intent = new Intent(this, ProjectRecordsActivity.class);
        //startActivity(intent);
    }

    private void uploadImage(Record record, Uri filePath, int position) {
        firebaseStorageManager.uploadImage(filePath, task -> {
            if (task.isSuccessful()) {
                Uri imageUri = task.getResult();
                recordFields.get(position).setValue(imageUri.toString());
                recordFirestoreManager.updateRecord(record);
            } else {
                Log.w(NewRecordActivity.TAG, "ERROR: Failed to upload image", task.getException());
            }
        });
    }

    private void uploadAudio(Record record, Uri filePath, int position) {
        firebaseStorageManager.uploadAudio(filePath, task -> {
            if (task.isSuccessful()) {
                Uri audioUri = task.getResult();
                recordFields.get(position).setValue(audioUri.toString());
                recordFirestoreManager.updateRecord(record);
            } else {
                Log.w(NewRecordActivity.TAG, "ERROR: Failed to upload audio file", task.getException());
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
