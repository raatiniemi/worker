package me.raatiniemi.worker.ui;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
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
import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.domain.Time;
import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.mapper.MapperRegistry;
import me.raatiniemi.worker.mapper.ProjectMapper;
import me.raatiniemi.worker.mapper.TimeMapper;
import me.raatiniemi.worker.ui.fragment.ClockActivityAtFragment;
import me.raatiniemi.worker.ui.fragment.NewProjectFragment;

public class ProjectListActivity extends ActionBarActivity
    implements NewProjectFragment.OnCreateProjectListener, ProjectListAdapter.OnProjectListListener, ClockActivityAtFragment.OnClockActivityAtListener
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

        RecyclerView projectsView = (RecyclerView) findViewById(R.id.fragment_project_list);
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
        update(project, new Date(), index);
    }

    public void onProjectClockActivityAt(Project project, int index)
    {
        // Instantiate the "Clock [in|out] at..."-fragment.
        ClockActivityAtFragment fragment = ClockActivityAtFragment.newInstance(project, index);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(fragment, "fragment_clock_activity_at");
        fragmentTransaction.commit();
    }

    public void onProjectOpen(Project project)
    {
        // Open the ProjectActivity.
        Intent intent = new Intent(this, ProjectActivity.class);
        intent.putExtra(ProjectListActivity.MESSAGE_PROJECT_ID, project.getId());
        startActivity(intent);
    }

    public void onClockActivityAt(Project project, Calendar calendar, int index)
    {
        update(project, calendar.getTime(), index);
    }

    /**
     * Update clock activity for the project.
     * @param project Project to update clock activity.
     * @param date Date to use for the clock activity.
     * @param index Row index to update the adapter.
     */
    private void update(Project project, Date date, int index)
    {
        try {
            // Retrieve the project and time data mappers.
            ProjectMapper projectMapper = MapperRegistry.getProjectMapper();
            TimeMapper timeMapper = MapperRegistry.getTimeMapper();

            Time time;

            // Depending on whether the project is active,
            // we're going to clock out or clock in.
            if (project.isActive()) {
                time = project.clockOutAt(date);
                timeMapper.update(time);
            } else {
                time = project.clockInAt(date);
                timeMapper.insert(time);
            }

            // Retrieve the updated project from the data mapper.
            project = projectMapper.find(project.getId());
            mAdapter.updateProject(project, index);
        } catch (DomainException e) {
            new AlertDialog.Builder(this)
                .setTitle(getString(R.string.project_list_item_project_clock_out_before_clock_in_title))
                .setMessage(getString(R.string.project_list_item_project_clock_out_before_clock_in_description))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing...
                    }
                })
                .show();
        }
    }
}
