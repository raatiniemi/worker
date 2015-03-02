package me.raatiniemi.worker.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.Project;

public class ProjectListActivity extends ActionBarActivity
{
    private static final String FRAGMENT_PROJECT_LIST_TAG = "project list";

    private static final String FRAGMENT_NEW_PROJECT_TAG = "new project";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        if (null == savedInstanceState) {
            ProjectListFragment fragment = new ProjectListFragment();

            getFragmentManager().beginTransaction()
                .replace(R.id.activity_project_list_project_list_fragment, fragment, FRAGMENT_PROJECT_LIST_TAG)
                .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_project_list, menu);
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

    public void openCreateNewProject()
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
                    Log.e("ProjectListActivity", "Unable to find fragment with tag: "+ FRAGMENT_PROJECT_LIST_TAG);
                }
            }
        });
        newProject.show(getFragmentManager().beginTransaction(), FRAGMENT_NEW_PROJECT_TAG);
    }
}
