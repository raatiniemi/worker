package me.raatiniemi.worker.ui;

import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.adapter.ProjectAdapter;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.ui.fragment.NewProjectFragment;

public class ProjectsActivity extends ActionBarActivity
    implements NewProjectFragment.OnCreateProjectListener
{
    private ArrayList<Project> projects;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView projectsView = (RecyclerView) findViewById(R.id.projects);
        projectsView.setLayoutManager(manager);

        ProjectMapper projectMapper = new ProjectMapper(this, null);
        projects = projectMapper.getProjects();

        ProjectAdapter projectAdapter = new ProjectAdapter(projects);
        projectsView.setAdapter(projectAdapter);
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
        // Add the project to the list of available projects
        // and notify the adapter that the data has changed.
        projects.add(project);
        // TODO: Update the recycler view.
    }
}
