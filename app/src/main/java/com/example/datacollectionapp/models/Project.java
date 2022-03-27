package com.example.datacollectionapp.models;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {

    @DocumentId
    private String projectId;
    private String username;
    private String projectName;
    private List<TemplateField> formTemplate = new ArrayList<>();

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<TemplateField> getFormTemplate() {
        return formTemplate;
    }

    public void setFormTemplate(List<TemplateField> formTemplate) {
        this.formTemplate = formTemplate;
    }

    public void addTemplateField(TemplateField templateField) {
        formTemplate.add(templateField);
    }
}
