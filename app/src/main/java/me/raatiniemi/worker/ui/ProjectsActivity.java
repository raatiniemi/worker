package me.raatiniemi.worker.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import java.util.ArrayList;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.adapter.ProjectsAdapter;
import me.raatiniemi.worker.data.Project;

public class ProjectsActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        // TODO: Retrieve saved projects.
        ArrayList<Project> projects = new ArrayList<>();

        ProjectsAdapter adapter = new ProjectsAdapter(this, projects);
        ListView listView = (ListView) findViewById(R.id.projects_listview);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.projects_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
