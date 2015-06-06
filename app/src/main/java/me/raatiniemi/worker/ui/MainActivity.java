package me.raatiniemi.worker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.BaseActivity;
import me.raatiniemi.worker.projects.ProjectsFragment;
import me.raatiniemi.worker.projects.ProjectsView;

public class MainActivity extends BaseActivity {
    /**
     * Tag for the project list fragment.
     */
    public static final String FRAGMENT_PROJECT_LIST_TAG = "project list";

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null == savedInstanceState) {
            ProjectsFragment fragment = new ProjectsFragment();

            getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, FRAGMENT_PROJECT_LIST_TAG)
                .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.actions_projects_new:
                openCreateNewProject();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Open the fragment for creating a new project.
     */
    private void openCreateNewProject() {
        try {
            // Attempt to retrieve the projects fragment.
            ProjectsView fragment = (ProjectsView) getFragmentManager()
                    .findFragmentByTag(FRAGMENT_PROJECT_LIST_TAG);

            // Dispatch the create new project to the fragment.
            fragment.createNewProject();
        } catch (ClassCastException e) {
            // Something has gone wrong with the fragment manager,
            // just print the exception and continue.
            Log.e(TAG, "Unable to cast projects fragment: " + e.getMessage());
        }
    }
}
