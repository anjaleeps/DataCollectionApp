package com.example.datacollectionapp.screens.record;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datacollectionapp.models.RecordField;

import java.util.List;

public abstract class RecordViewHolder extends RecyclerView.ViewHolder {
    private TextView fieldName;
    private List<RecordField> recordFields;
    private Context context;

    public RecordViewHolder(@NonNull View itemView, TextView fieldName, Context context, List<RecordField> recordFields) {
        super(itemView);
        this.fieldName = fieldName;
        this.context = context;
        this.recordFields = recordFields;
    }

    public TextView getFieldName() {
        return fieldName;
    }

    public void setFieldName(TextView fieldName) {
        this.fieldName = fieldName;
    }

    public RecordField getFieldAtPosition(int position) {
        return recordFields.get(position);
    }

    public Context getContext() {
        return context;
    }
}
