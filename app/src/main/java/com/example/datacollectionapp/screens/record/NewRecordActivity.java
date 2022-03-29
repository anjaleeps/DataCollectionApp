package com.example.datacollectionapp.screens.record;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.database.connectionmanagers.RecordFirestoreManager;
import com.example.datacollectionapp.models.DataType;
import com.example.datacollectionapp.models.Project;
import com.example.datacollectionapp.models.Record;
import com.example.datacollectionapp.models.RecordField;
import com.example.datacollectionapp.models.TemplateField;
import com.example.datacollectionapp.screens.projectrecords.ProjectRecordsActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRecordActivity extends AppCompatActivity {

    public static final String PROJECT_ID = "PROJECT_ID";
    private static final String TAG = "NewRecordActivity";

    private String projectId;
    private String projectName;
    private List<TemplateField> formTemplate = new ArrayList<>();
    private HashMap<TemplateField, Integer> fieldIds = new HashMap<>();
    private ProjectFirestoreManager projectFirestoreManager;
    private RecordFirestoreManager recordFirestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

//        Intent intent = getIntent();
//        projectId = intent.getStringExtra(PROJECT_ID);
        projectId = "TmFPKp8At1v6SCarpxeR";
        projectFirestoreManager = ProjectFirestoreManager.getInstance();
        recordFirestoreManager = RecordFirestoreManager.getInstance();
        getFormTemplate();
        createFormFields();
    }

    public void getFormTemplate() {
        projectFirestoreManager.getProjectById(projectId, task -> {
           if (task.isSuccessful() && task.getResult() != null) {
               DocumentSnapshot documentSnapshot = task.getResult();
               Project project = documentSnapshot.toObject(Project.class);
               projectName = project.getProjectName();
               formTemplate = project.getFormTemplate();
               System.out.println(formTemplate.size());
               createFormFields();
           } else {
               Toast.makeText(this, "Error retrieving the form template", Toast.LENGTH_SHORT).show();
               Log.w(TAG, "Error retrieving the form template", task.getException());
           }
        });
    }

    public void createFormFields() {
        LinearLayout linearLayoutContainer = findViewById(R.id.linearLayoutContainer);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        LinearLayout.LayoutParams editLayoutParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f);

        for (TemplateField templateField : formTemplate) {
            DataType dataType = templateField.getDatatype();

            LinearLayout linearLayout = new LinearLayout(this);

            linearLayout.setLayoutParams(linearLayoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setWeightSum(3f);

            TextView fieldName = addFieldName(templateField.getFieldName());
            fieldName.setLayoutParams(textLayoutParams);
            linearLayout.addView(fieldName);

            switch (dataType) {
                case TEXT:
                    EditText editText = addEditText(templateField);
                    editText.setLayoutParams(editLayoutParams);
                    linearLayout.addView(editText);
                    break;

                case NUMBER:
                    EditText editNumber = addEditNumber(templateField);
                    editNumber.setLayoutParams(editLayoutParams);
                    linearLayout.addView(editNumber);
                    break;

                case IMAGE:
                    //TODO
                    break;

                case LOCATION:
                    //TODO
                    break;

                case AUDIO:
                    //TODO
                    break;
            }

            linearLayoutContainer.addView(linearLayout);
        }

    }

    public void saveRecord(View view) {
        Record newRecord = new Record();

        for (Map.Entry<TemplateField, Integer> fieldId : fieldIds.entrySet()) {
            String fieldName = fieldId.getKey().getFieldName();
            DataType dataType = fieldId.getKey().getDatatype();

            switch(dataType) {
                case TEXT:
                case NUMBER:
                    EditText editText = findViewById(fieldId.getValue());
                    String value = editText.getText().toString().trim();
                    RecordField recordField = new RecordField(fieldName, value, dataType);
                    newRecord.addFieldValue(recordField);
                    break;

                case IMAGE:
                    //TODO
                    break;

                case LOCATION:
                    //TODO
                    break;

                case AUDIO:
                    //TODO
                    break;
            }
        }

        newRecord.setProjectId(projectId);
        recordFirestoreManager.createRecord(newRecord);

        Intent intent = new Intent(this, ProjectRecordsActivity.class);
        startActivity(intent);
    }

    private TextView addFieldName(String fieldName) {
        TextView textView = new TextView(this);
        textView.setText(fieldName.toLowerCase());
        return  textView;
    }

    private EditText addEditText(TemplateField field) {
        int id = View.generateViewId();
        EditText editText = new EditText(this);
        editText.setHint("Enter text");
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setId(id);
        fieldIds.put(field, id);
        return editText;
    }

    private EditText addEditNumber(TemplateField field) {
        int id = View.generateViewId();
        EditText editNumber = new EditText(this);
        editNumber.setHint("Enter number");
        editNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        editNumber.setId(id);
        fieldIds.put(field, id);
        return editNumber;
    }
}