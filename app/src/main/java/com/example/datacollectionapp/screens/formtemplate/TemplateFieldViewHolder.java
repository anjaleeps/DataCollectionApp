package com.example.datacollectionapp.screens.formtemplate;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.models.DataType;
import com.example.datacollectionapp.models.TemplateField;

public class TemplateFieldViewHolder extends RecyclerView.ViewHolder {

    private final TemplateFieldAdapter templateFieldAdapter;
    private EditText fieldName;
    private Spinner dataType;

    public TemplateFieldViewHolder(TemplateFieldAdapter templateFieldAdapter, @NonNull View itemView) {
        super(itemView);
        this.templateFieldAdapter = templateFieldAdapter;
        fieldName = itemView.findViewById(R.id.editFieldName);
        dataType = itemView.findViewById(R.id.spinnerDataType);

        fieldName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TemplateField templateField = templateFieldAdapter.templateFields.get(getAdapterPosition());
                templateField.setFieldName(fieldName.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dataType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDataType = (String) parent.getItemAtPosition(position);
                TemplateField templateField = templateFieldAdapter.templateFields.get(getAdapterPosition());
                templateField.setDatatype(DataType.valueOf(selectedDataType.toUpperCase()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TemplateField templateField = templateFieldAdapter.templateFields.get(getAdapterPosition());
                templateField.setDatatype(null);
            }
        });
    }

    public EditText getFieldName() {
        return fieldName;
    }

    public void setFieldName(EditText fieldName) {
        this.fieldName = fieldName;
    }

    public Spinner getDataType() {
        return dataType;
    }

    public void setDataType(Spinner dataType) {
        this.dataType = dataType;
    }
}
