package me.raatiniemi.worker.project;

import android.os.Bundle;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.BaseActivity;
import me.raatiniemi.worker.project.timesheet.TimesheetFragment;

public class ProjectActivity extends BaseActivity {
    /**
     * Tag for the timesheet fragment.
     */
    public static final String FRAGMENT_TIMESHEET_TAG = "timesheet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (null == savedInstanceState) {
            TimesheetFragment fragment = new TimesheetFragment();
            fragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                .replace(R.id.fragment_timesheet, fragment, ProjectActivity.FRAGMENT_TIMESHEET_TAG)
                .commit();
        }
    }
}
