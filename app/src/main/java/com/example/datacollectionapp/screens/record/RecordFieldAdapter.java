package com.example.datacollectionapp.screens.record;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.models.RecordField;

import java.util.List;

public class RecordFieldAdapter extends RecyclerView.Adapter<RecordViewHolder> {

    private static final int TEXT_VIEW_TYPE = 0;
    private static final int NUMBER_VIEW_TYPE = 1;
    private static final int AUDIO_VIEW_TYPE = 2;
    private static final int LOCATION_VIEW_TYPE = 3;
    private static final int IMAGE_VIEW_TYPE = 4;


    private Context context;
    private List<RecordField> recordFields;

    public RecordFieldAdapter(Context context, List<RecordField> recordFields) {
        this.context = context;
        this.recordFields = recordFields;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        switch (viewType) {
            case TEXT_VIEW_TYPE:
                view = layoutInflater.inflate(R.layout.text_field, parent, false);
                return new TextRecordViewHolder(view, context, recordFields);
            case NUMBER_VIEW_TYPE:
                view = layoutInflater.inflate(R.layout.number_field, parent, false);
                return new NumberRecordViewHolder(view, context, recordFields);
            case AUDIO_VIEW_TYPE:
                view = layoutInflater.inflate(R.layout.audio_upload, parent, false);
                return new AudioRecordViewHolder(view, context, recordFields);
            case IMAGE_VIEW_TYPE:
                view = layoutInflater.inflate(R.layout.image_upload, parent, false);
                return new ImageRecordViewHolder(view, context, recordFields);
            default:
                view = layoutInflater.inflate(R.layout.text_field, parent, false);
                return new TextRecordViewHolder(view, context, recordFields);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        RecordField recordField = recordFields.get(position);

        TextView textFieldName = holder.getFieldName();
        textFieldName.setText(recordField.getFieldName());

        switch (holder.getItemViewType()) {
            case TEXT_VIEW_TYPE:
                EditText editTextValue = ((TextRecordViewHolder) holder).getTextValue();
                editTextValue.setText(recordField.getValue());
                break;
            case NUMBER_VIEW_TYPE:
                EditText editNumberValue = ((NumberRecordViewHolder) holder).getNumberValue();
                editNumberValue.setText(recordField.getValue());
                break;
            case IMAGE_VIEW_TYPE:
            case AUDIO_VIEW_TYPE:
                //TODO
            default:
                //TODO
        }
    }

    @Override
    public int getItemCount() {
        return recordFields.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (recordFields.get(position).getDataType()) {
            case TEXT:
                return TEXT_VIEW_TYPE;
            case NUMBER:
                return NUMBER_VIEW_TYPE;
            case AUDIO:
                return AUDIO_VIEW_TYPE;
            case LOCATION:
                return LOCATION_VIEW_TYPE;
            case IMAGE:
                return IMAGE_VIEW_TYPE;
            default:
                return -1;
        }
    }

}
