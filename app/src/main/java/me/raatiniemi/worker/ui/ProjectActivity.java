package me.raatiniemi.worker.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import me.raatiniemi.worker.R;

public class ProjectActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Attempt to get the project id from the activity intent.
        Intent intent = getIntent();
        Long projectId = intent.getLongExtra(ProjectListActivity.MESSAGE_PROJECT_ID, 0);

        if (projectId == 0) {
            // TODO: Send error message to the user, unable to find project id.
            finish();
            return;
        }

        // TODO: Retrieve the project data.
    }
}
