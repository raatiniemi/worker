/*
 * Copyright (C) 2015 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.presentation.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.BaseActivity;
import me.raatiniemi.worker.presentation.view.fragment.ProjectsFragment;
import me.raatiniemi.worker.presentation.view.ProjectsView;
import me.raatiniemi.worker.util.Worker;

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

        // Check if we have received the restart intent.
        Intent intent = getIntent();
        if (null != intent && null != intent.getAction()) {
            if (Worker.INTENT_ACTION_RESTART.equals(intent.getAction())) {
                restart();
            }
        }

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
            case R.id.actions_main_create_new_project:
                openCreateNewProject();
                return true;
            case R.id.actions_main_settings:
                openSettings();
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

    /**
     * Open the settings.
     */
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Restart the application.
     */
    private void restart() {
        try {
            // The AlarmManager will allow us to send the start intent after
            // we have stopped the application, i.e. it will restart.
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT
            );

            manager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
        } catch (ClassCastException e) {
            Log.w(TAG, "Unable to cast the AlarmManager: " + e.getMessage());
        }

        finish();
        System.exit(0);
    }
}
