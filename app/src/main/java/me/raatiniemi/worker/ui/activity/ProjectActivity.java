package me.raatiniemi.worker.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.ui.project.TimeListViewFragment;

public class ProjectActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (savedInstanceState == null) {
            TimeListViewFragment fragment = new TimeListViewFragment();
            fragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                .replace(R.id.project_time_list_view_fragment, fragment)
                .commit();
        }
    }
}
