/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.project.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import me.raatiniemi.worker.Preferences;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.features.project.timesheet.view.TimesheetFragment;
import me.raatiniemi.worker.features.shared.view.activity.BaseActivity;
import me.raatiniemi.worker.util.KeyValueStore;

import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.util.NullUtil.nonNull;

public class ProjectActivity extends BaseActivity {
    public static final String MESSAGE_PROJECT_ID = "project id";

    /**
     * Tag for the timesheet fragment.
     */
    private static final String FRAGMENT_TIMESHEET_TAG = "timesheet";

    private final Preferences preferences = new Preferences();
    private final KeyValueStore keyValueStore = preferences.getKeyValueStore();

    /**
     * Reference to the timesheet fragment.
     */
    private TimesheetFragment timesheetFragment;

    public static Intent newIntent(Context context, Long projectId) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(ProjectActivity.MESSAGE_PROJECT_ID, projectId);

        return intent;
    }

    /**
     * Get the timesheet fragment, handles construction if needed.
     *
     * @return Timesheet fragment.
     */
    private TimesheetFragment getTimesheetFragment() {
        if (isNull(timesheetFragment)) {
            timesheetFragment = TimesheetFragment.newInstance(getIntent().getExtras());
        }

        return timesheetFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (isNull(savedInstanceState)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            getTimesheetFragment(),
                            ProjectActivity.FRAGMENT_TIMESHEET_TAG
                    )
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions_project, menu);

        // Set the selected value for the option, otherwise the value will be set to default each
        // time the activity is created.
        MenuItem hideRegistered = menu.findItem(R.id.actions_project_hide_registered);
        if (nonNull(hideRegistered)) {
            hideRegistered.setChecked(keyValueStore.hideRegisteredTime());
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.actions_project_hide_registered == item.getItemId()) {
            handleHideRegisteredTimeChange(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleHideRegisteredTimeChange(MenuItem item) {
        item.setChecked(!item.isChecked());

        keyValueStore.setHideRegisteredTime(item.isChecked());
        getTimesheetFragment().refresh();
    }
}
