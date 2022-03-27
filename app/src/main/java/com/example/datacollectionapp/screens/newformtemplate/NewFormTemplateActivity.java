package com.example.datacollectionapp.screens.newformtemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.adapters.TemplateFieldAdapter;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.models.Project;
import com.example.datacollectionapp.models.TemplateField;
import com.example.datacollectionapp.screens.projectlist.ProjectListActivity;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;
import java.util.List;

public class NewFormTemplateActivity extends AppCompatActivity {

    public static final String PROJECT_DATA = "PROJECT_DATA";

    private Project project;
    private ProjectFirestoreManager projectFirestoreManager;
    private TemplateFieldAdapter fieldAdapter;
    private final List<TemplateField> templateFields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_form_template);

        setProjectName();
        templateFields.addAll(project.getFormTemplate());
        projectFirestoreManager = ProjectFirestoreManager.getInstance();
        setupFieldRecycleView();
    }

    public void setProjectName() {
        Intent intent = getIntent();
        TextView textProjectName = findViewById(R.id.textProjectName);
        project = (Project) intent.getSerializableExtra(PROJECT_DATA);
        textProjectName.setText(project.getProjectName());
    }

    public void setupFieldRecycleView() {
        RecyclerView fieldsRecycleView = findViewById(R.id.fieldsRecycleView);
        fieldsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        fieldAdapter = new TemplateFieldAdapter(this, templateFields);
        fieldsRecycleView.setAdapter(fieldAdapter);
        if (templateFields.size() == 0) {
            templateFields.add(new TemplateField());
        }
        fieldAdapter.notifyDataSetChanged();
    }

    public void addNewField(View view) {
        final TemplateField newTemplateField = new TemplateField();
        templateFields.add(newTemplateField);
        fieldAdapter.notifyDataSetChanged();
    }

    public void saveTemplate(View view) {
        List<TemplateField> newTemplateFields = new ArrayList<>();
        for (int i = 0; i < templateFields.size(); i++) {
            if (templateFields.get(i).getFieldName().trim() != null && templateFields.get(i).getDatatype() != null) {
                newTemplateFields.add(templateFields.get(i));
            }
        }
        project.setFormTemplate(newTemplateFields);
        projectFirestoreManager.updateProject(project, task -> {
            if (task.isSuccessful()) {
                Intent templateIntent = new Intent(NewFormTemplateActivity.this, ProjectListActivity.class);
                startActivity(templateIntent);
            } else {
                Toast.makeText(NewFormTemplateActivity.this, "Couldn't save the template! Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}