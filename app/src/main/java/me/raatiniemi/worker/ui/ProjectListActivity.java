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
import me.raatiniemi.worker.adapter.ProjectListAdapter;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mapper.TimeMapper;
import me.raatiniemi.worker.ui.fragment.NewProjectFragment;

public class ProjectListActivity extends ActionBarActivity
    implements NewProjectFragment.OnCreateProjectListener, ProjectListAdapter.OnProjectActivityChangeListener
{
    private TimeMapper mTimeMapper;

    private ProjectMapper mProjectMapper;

    private ProjectListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView projectsView = (RecyclerView) findViewById(R.id.project_list);
        projectsView.setLayoutManager(manager);

        // Instantiate the data mapper for time and project.
        mTimeMapper = new TimeMapper();
        mProjectMapper = new ProjectMapper(mTimeMapper);

        // Retrieve the available projects from the project data mapper.
        ArrayList<Project> projects = mProjectMapper.getProjects();

        mAdapter = new ProjectListAdapter(this, projects);
        projectsView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_list_activity_actions, menu);
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
        mAdapter.addProject(project);
    }

    public void onProjectActivityToggle(Project project, int index)
    {
        Time time;

        // Depending on whether the project is active,
        // it either have to clock out or clock in.
        if (project.isActive()) {
            // Clock out project.
            time = project.clockOut();
            mTimeMapper.update(time);
        } else {
            // Initialize the Time domain object with the project id,
            // the constructor takes care of the start and stop.
            time = new Time(project.getId());
            mTimeMapper.insert(time);
        }

        // Retrieve the updated project and send it to the adapter.
        project = (Project) mProjectMapper.find(project.getId());
        mAdapter.updateProject(project, index);
    }
}
