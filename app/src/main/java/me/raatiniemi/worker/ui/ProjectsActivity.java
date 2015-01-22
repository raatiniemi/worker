package me.raatiniemi.worker.ui;

import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.adapter.ProjectsAdapter;
import me.raatiniemi.worker.data.Project;
import me.raatiniemi.worker.database.ProjectDataSource;
import me.raatiniemi.worker.ui.fragment.NewProjectFragment;

public class ProjectsActivity extends ActionBarActivity
    implements NewProjectFragment.OnCreateProjectListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        // Retrieve the saved projects.
        ProjectDataSource dataSource = new ProjectDataSource(this);
        ArrayList<Project> projects = dataSource.getProjects();

        ProjectsAdapter adapter = new ProjectsAdapter(this, projects);
        ListView listView = (ListView) findViewById(R.id.projects_listview);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.projects_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case R.id.action_create_new_project:
                openCreateNewProject();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void openCreateNewProject()
    {
        DialogFragment newProject = new NewProjectFragment();
        newProject.show(getFragmentManager().beginTransaction(), "fragment_new_project");
    }

    public void onCreateProject(Project project)
    {
        // TODO: Add project to ArrayList and notify the adapter.
    }
}
