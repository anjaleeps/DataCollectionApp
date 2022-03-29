package com.example.datacollectionapp.models;

public class RecordField {
    private String fieldName;
    private String value;
    private DataType dataType;

    public RecordField(String fieldName, String value, DataType dataType) {
        this.fieldName = fieldName;
        this.value = value;
        this.dataType = dataType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
