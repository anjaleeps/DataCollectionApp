package com.example.datacollectionapp.models;

public class TemplateField {

    private String fieldName;
    private DataType datatype;

    public TemplateField() {
    }

    public TemplateField(String fieldName, DataType dataType) {
        this.fieldName = fieldName;
        this.datatype = dataType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public DataType getDatatype() {
        return datatype;
    }

    public void setDatatype(DataType datatype) {
        this.datatype = datatype;
    }
}
