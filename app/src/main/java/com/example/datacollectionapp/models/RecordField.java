package com.example.datacollectionapp.models;

public class RecordField {
    private String fieldName;
    private DataType dataType;
    private String value;

    public RecordField(String fieldName, DataType dataType) {
        this.fieldName = fieldName;
        this.dataType = dataType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
