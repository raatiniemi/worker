package me.raatiniemi.worker.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.ui.project.TimeListFragment;

public class ProjectActivity extends ActionBarActivity
{
    private static final String FRAGMENT_LIST_VIEW = "list view";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (savedInstanceState == null) {
            TimeListFragment fragment = new TimeListFragment();
            fragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                .replace(R.id.project_time_list_view_fragment, fragment, FRAGMENT_LIST_VIEW)
                .commit();
        }
    }
}
