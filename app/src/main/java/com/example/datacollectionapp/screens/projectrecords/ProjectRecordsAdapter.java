package com.example.datacollectionapp.screens.projectrecords;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.models.Record;
import com.example.datacollectionapp.screens.record.UpdateRecordActivity;
import com.example.datacollectionapp.screens.record.ViewRecordActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProjectRecordsAdapter extends BaseAdapter {

    private static final String timestampPattern = "dd-MM-yyyy HH:mm:ss";
    private final SimpleDateFormat dateFormat;
    private List<Record> recordList;
    private Context context;

    public ProjectRecordsAdapter(Context context, List<Record> recordList) {
        this.context = context;
        this.recordList = recordList;
        dateFormat = new SimpleDateFormat(timestampPattern);
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
        Record record = recordList.get(position);
        TextView recordName = view.findViewById(R.id.textViewName);
        ImageView buttonEdit = view.findViewById(R.id.buttonEdit);
        ImageView buttonDelete = view.findViewById(R.id.buttonDelete);
        String recordTimestamp;
        if (record.getTimestamp() != null) {
             recordTimestamp = dateFormat.format(record.getTimestamp().toDate());
        } else {
            recordTimestamp = dateFormat.format(new Date());
        }

        recordName.setText(recordTimestamp);
        recordName.setTextSize(14);
        recordName.setOnClickListener(v -> {
            selectRecord(v, position);
        });
        buttonEdit.setOnClickListener(v -> {
            editRecord(v, position);
        });
        buttonDelete.setOnClickListener(v -> {
            deleteRecord(v, position);
        });
        return view;
    }

    private void deleteRecord(View view, int position) {
        String recordId = recordList.get(position).getRecordId();
        ((ProjectRecordsActivity) context).deleteRecord(recordId, position);
    }

    private void editRecord(View view, int position) {
        Record record = recordList.get(position);
        Intent intent = new Intent(context, UpdateRecordActivity.class);
        intent.putExtra(ProjectRecordsActivity.RECORD_ID, record.getRecordId());
        intent.putExtra(ProjectRecordsActivity.PROJECT_ID, record.getProjectId());
        context.startActivity(intent);
    }

    public void selectRecord(View view, int position) {
        Record record = recordList.get(position);
        Intent intent = new Intent(context, ViewRecordActivity.class);
        intent.putExtra(ProjectRecordsActivity.RECORD_ID, record.getRecordId());
        intent.putExtra(ProjectRecordsActivity.PROJECT_ID, record.getProjectId());
        context.startActivity(intent);
    }
}
