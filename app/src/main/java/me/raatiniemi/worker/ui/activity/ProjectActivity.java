package me.raatiniemi.worker.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.adapter.ProjectTimeListAdapter;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;

public class ProjectActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Attempt to get the project id from the activity intent.
        Intent intent = getIntent();
        Long projectId = intent.getLongExtra(ProjectListActivity.MESSAGE_PROJECT_ID, 0);

        if (projectId == 0) {
            // TODO: Send error message to the user, unable to find project id.
            finish();
            return;
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView timeView = (RecyclerView) findViewById(R.id.project_time_list);
        timeView.setLayoutManager(manager);

        // Retrieve the project data from the mapper.
        ProjectMapper projectMapper = MapperRegistry.getProjectMapper();
        Project project = (Project) projectMapper.find(projectId);

        // Set the activity title to the project name.
        setTitle(project.getName());

        ProjectTimeListAdapter adapter = new ProjectTimeListAdapter(this, project.getTime());
        timeView.setAdapter(adapter);
    }
}
