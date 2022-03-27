package com.example.datacollectionapp.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.HashMap;

public class Record {

    @DocumentId
    private String recordId;
    private String projectId;
    @ServerTimestamp
    private Timestamp timestamp;
    private HashMap<String, RecordField> recordFields = new HashMap<>();

    public String getProjectId() {
        return projectId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setRecordFields(HashMap<String, RecordField> recordFields) {
        this.recordFields = recordFields;
    }

    public HashMap<String, RecordField> getRecordFields() {
        return recordFields;
    }

    public void addFieldValue(RecordField field) {
        recordFields.put(field.getFieldName(), field);
    }

    public RecordField getFieldValue(String fieldName) {
        return recordFields.get(fieldName);
    }
}
