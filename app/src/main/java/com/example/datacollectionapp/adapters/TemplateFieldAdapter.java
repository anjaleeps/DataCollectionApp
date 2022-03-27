package com.example.datacollectionapp.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.database.connectionmanagers.ProjectFirestoreManager;
import com.example.datacollectionapp.models.DataType;
import com.example.datacollectionapp.models.TemplateField;
import com.example.datacollectionapp.screens.newformtemplate.NewFormTemplateActivity;

import java.util.ArrayList;
import java.util.List;

public class TemplateFieldAdapter extends RecyclerView.Adapter<TemplateFieldAdapter.TemplateFieldViewHolder> {

    Context context;
    List<TemplateField> templateFields;

    public TemplateFieldAdapter(Context context, List<TemplateField> templateFields) {
        this.context = context;
        this.templateFields = templateFields;
    }

    @NonNull
    @Override
    public TemplateFieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.form_field, parent, false);
        return new TemplateFieldViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateFieldAdapter.TemplateFieldViewHolder holder, int position) {
        TemplateField templateField = templateFields.get(position);
        String fieldName = templateField.getFieldName();
        DataType dataType = templateField.getDatatype();

        EditText editFieldName = holder.fieldName;
        Spinner spinnerDataType = holder.dataType;

        if (fieldName != null) {
            editFieldName.setText(fieldName);
        } else {
            editFieldName.setText("");
        }

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context, R.array.data_types, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerDataType.setPrompt("Choose data type");
        spinnerDataType.setAdapter(arrayAdapter);

        if (dataType != null) {
            int dataTypePosition = arrayAdapter.getPosition(dataType.toString().toLowerCase());
            spinnerDataType.setSelection(dataTypePosition);
        }
    }

    @Override
    public int getItemCount() {
        return templateFields.size();
    }

    public class TemplateFieldViewHolder extends RecyclerView.ViewHolder {

        EditText fieldName;
        Spinner dataType;

        public TemplateFieldViewHolder(@NonNull View itemView) {
            super(itemView);
            fieldName = itemView.findViewById(R.id.editFieldName);
            dataType = itemView.findViewById(R.id.spinnerDataType);

            fieldName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TemplateField templateField = templateFields.get(getAdapterPosition());
                    templateField.setFieldName(fieldName.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            dataType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedDataType = (String) parent.getItemAtPosition(position);
                    TemplateField templateField = templateFields.get(getAdapterPosition());
                    templateField.setDatatype(DataType.valueOf(selectedDataType.toUpperCase()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    TemplateField templateField = templateFields.get(getAdapterPosition());
                    templateField.setDatatype(null);
                }
            });
        }
    }
}
