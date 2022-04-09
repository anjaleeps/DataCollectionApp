package com.example.datacollectionapp.screens.projectlist;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.datacollectionapp.R;
import com.example.datacollectionapp.screens.project.EditProjectActivity;
import com.example.datacollectionapp.screens.projectrecords.ProjectRecordsActivity;

import java.util.List;

public class ProjectListAdapter extends BaseAdapter {
    private List<String> projectNames;
    private List<String> projectIds;
    private Context context;

    public ProjectListAdapter(Context context, List<String> projectNames, List<String> projectIds) {
        this.context = context;
        this.projectNames = projectNames;
        this.projectIds = projectIds;
    }
    @Override
    public int getCount() {
        return projectNames.size();
    }

    @Override
    public Object getItem(int position) {
        return projectNames.get(position);
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
        TextView projectName = view.findViewById(R.id.textViewProjectName);
        ImageView buttonEdit = view.findViewById(R.id.buttonEdit);
        ImageView buttonDelete = view.findViewById(R.id.buttonDelete);

        projectName.setText(projectNames.get(position));
        projectName.setOnClickListener(v -> {
            selectProject(v, position);
        });
        buttonEdit.setOnClickListener(v -> {
            editProject(v, position);
        });
        buttonDelete.setOnClickListener(v -> {
            deleteProject(v, position);
        });
        return view;
    }

    public void selectProject(View view, int position) {
        String projectId = projectIds.get(position);
        Intent intent = new Intent(context, ProjectRecordsActivity.class);
        intent.putExtra(ProjectListActivity.PROJECT_ID, projectId);
        intent.putExtra(ProjectListActivity.PROJECT_NAME, projectNames.get(position));
        context.startActivity(intent);
    }

    public void editProject(View view, int position) {
        String projectId = projectIds.get(position);
        Intent intent = new Intent(context, EditProjectActivity.class);
        intent.putExtra(ProjectListActivity.EXTRA_MESSAGE, projectId);
        context.startActivity(intent);
    }

    public void deleteProject(View view, int position) {
        String projectId = projectIds.get(position);
        ((ProjectListActivity) context).deleteProject(projectId, position);
    }
}
