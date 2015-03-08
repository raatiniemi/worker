package me.raatiniemi.worker.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import me.raatiniemi.worker.R;

public class ProjectActivity extends ActionBarActivity
{
    private static final String TAG = "ProjectActivity";

    private static final String FRAGMENT_TIME_LIST_TAG = "time list";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (savedInstanceState == null) {
            ExpandableTimeListFragment fragment = new ExpandableTimeListFragment();
            fragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                .replace(R.id.activity_project_time_list_fragment, fragment, FRAGMENT_TIME_LIST_TAG)
                .commit();
        }
    }
}
