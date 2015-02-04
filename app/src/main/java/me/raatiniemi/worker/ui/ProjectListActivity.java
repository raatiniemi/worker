package me.raatiniemi.worker.ui;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.adapter.ProjectListAdapter;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mapper.TimeMapper;
import me.raatiniemi.worker.ui.fragment.ClockOutAtFragment;
import me.raatiniemi.worker.ui.fragment.NewProjectFragment;

public class ProjectListActivity extends ActionBarActivity
    implements NewProjectFragment.OnCreateProjectListener, ProjectListAdapter.OnProjectListListener, ClockOutAtFragment.OnClockOutAtListener
{
    public static final String MESSAGE_PROJECT_ID = "me.raatiniemi.activity.project.id";

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
        ProjectMapper projectMapper = MapperRegistry.getProjectMapper();

        // Retrieve the available projects from the project data mapper.
        ArrayList<Project> projects = projectMapper.getProjects();

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
        ProjectMapper projectMapper = MapperRegistry.getProjectMapper();
        TimeMapper timeMapper = MapperRegistry.getTimeMapper();

        Time time;

        // Depending on whether the project is active,
        // it either have to clock out or clock in.
        if (project.isActive()) {
            // Clock out project.
            time = project.clockOut();
            timeMapper.update(time);
        } else {
            // Initialize the Time domain object with the project id,
            // the constructor takes care of the start and stop.
            time = new Time(project.getId());
            timeMapper.insert(time);
        }

        // Retrieve the updated project and send it to the adapter.
        project = (Project) projectMapper.find(project.getId());
        mAdapter.updateProject(project, index);
    }

    public void onProjectClockOutAt(Project project, int index)
    {
        // Instantiate the "Clock out at"-fragment.
        ClockOutAtFragment clockOutAtFragment = new ClockOutAtFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(clockOutAtFragment, "fragment_clock_out_at");
        fragmentTransaction.commit();
    }

    public void onProjectOpen(Project project)
    {
        // Open the ProjectActivity.
        Intent intent = new Intent(this, ProjectActivity.class);
        intent.putExtra(ProjectListActivity.MESSAGE_PROJECT_ID, project.getId());
        startActivity(intent);
    }

    public void onClockOutAt(Calendar calendar)
    {
        // TODO: Clock out the project.
    }
}
