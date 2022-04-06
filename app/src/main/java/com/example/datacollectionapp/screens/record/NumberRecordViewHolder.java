package com.example.datacollectionapp.screens.record;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.models.RecordField;

import java.util.List;

public class NumberRecordViewHolder extends RecordViewHolder {

    private EditText numberValue;

    public NumberRecordViewHolder(@NonNull View itemView, Context context, List<RecordField> recordFields) {
        super(itemView, itemView.findViewById(R.id.textFieldName), context, recordFields);
        numberValue = itemView.findViewById(R.id.editNumberValue);

        numberValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                RecordField recordField = recordFields.get(getAdapterPosition());
                recordField.setValue(numberValue.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public EditText getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(EditText numberValue) {
        this.numberValue = numberValue;
    }
}
