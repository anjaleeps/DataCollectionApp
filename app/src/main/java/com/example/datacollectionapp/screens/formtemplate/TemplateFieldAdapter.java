package com.example.datacollectionapp.screens.formtemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.models.DataType;
import com.example.datacollectionapp.models.TemplateField;

import java.util.List;

public class TemplateFieldAdapter extends RecyclerView.Adapter<TemplateFieldViewHolder> {

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
        return new TemplateFieldViewHolder(this, view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateFieldViewHolder holder, int position) {
        TemplateField templateField = templateFields.get(position);
        String fieldName = templateField.getFieldName();
        DataType dataType = templateField.getDatatype();

        EditText editFieldName = holder.getFieldName();
        Spinner spinnerDataType = holder.getDataType();

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

}
