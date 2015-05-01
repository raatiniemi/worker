package me.raatiniemi.worker.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Project;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    /**
     * Tag for the project list fragment.
     */
    static final String FRAGMENT_PROJECT_LIST_TAG = "project list";

    /**
     * Tag for the timesheet fragment.
     */
    static final String FRAGMENT_TIMESHEET_TAG = "timesheet";

    /**
     * Tag for the new project fragment.
     */
    static final String FRAGMENT_NEW_PROJECT_TAG = "new project";

    /**
     * Instance for the main activity.
     */
    private static MainActivity sInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Store the instance for the main activity as a static variable.
        // This will allow fragments to communicate using the main activity.
        sInstance = this;

        if (null == savedInstanceState) {
            ProjectListFragment fragment = new ProjectListFragment();

            getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, FRAGMENT_PROJECT_LIST_TAG)
                .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case R.id.actions_project_list_new:
                openCreateNewProject();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Open the fragment for creating a new project.
     */
    protected void openCreateNewProject()
    {
        NewProjectFragment newProject = new NewProjectFragment();
        newProject.setOnCreateProjectListener(new NewProjectFragment.OnCreateProjectListener() {
            @Override
            public void onCreateProject(Project project)
            {
                // Attempt to find the fragment by the used fragment tag.
                ProjectListFragment fragment = (ProjectListFragment)
                    getFragmentManager().findFragmentByTag(FRAGMENT_PROJECT_LIST_TAG);

                // If we have found the fragment, add the new project.
                if (null != fragment) {
                    fragment.addProject(project);
                } else {
                    Log.e(TAG, "Unable to find fragment with tag: "+ FRAGMENT_PROJECT_LIST_TAG);
                }
            }
        });
        newProject.show(getFragmentManager().beginTransaction(), FRAGMENT_NEW_PROJECT_TAG);
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fragmentManager = getFragmentManager();

        // Depending on which fragment is contained within the
        // container the back button will behave differently.
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        Class<TimesheetFragment> instance = TimesheetFragment.class;
        if (instance.equals(fragment.getClass())) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Retrieve the instance for the main activity.
     * @return Instance for the main activity or null if none is available.
     */
    static MainActivity getInstance()
    {
        return sInstance;
    }
}
