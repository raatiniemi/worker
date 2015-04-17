package me.raatiniemi.worker.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import me.raatiniemi.worker.R;

public class ProjectActivity extends ActionBarActivity
{
    private static final String TAG = "ProjectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (savedInstanceState == null) {
            TimesheetFragment fragment = new TimesheetFragment();
            fragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                .replace(R.id.activity_project_time_list_fragment, fragment, MainActivity.FRAGMENT_TIMESHEET_TAG)
                .commit();
        }
    }
}
