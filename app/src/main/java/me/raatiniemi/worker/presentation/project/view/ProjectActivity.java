/*
 * Copyright (C) 2015-2016 Worker Project
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

package me.raatiniemi.worker.presentation.project.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.presentation.util.Settings;
import me.raatiniemi.worker.presentation.view.activity.BaseActivity;

public class ProjectActivity extends BaseActivity {
    public static final String MESSAGE_PROJECT_ID = "me.raatiniemi.activity.project.id";

    /**
     * Tag for the timesheet fragment.
     */
    private static final String FRAGMENT_TIMESHEET_TAG = "timesheet";

    /**
     * Reference to the timesheet fragment.
     */
    private TimesheetFragment timesheetFragment;

    /**
     * Get the timesheet fragment, handles construction if needed.
     *
     * @return Timesheet fragment.
     */
    private TimesheetFragment getTimesheetFragment() {
        if (null == timesheetFragment) {
            timesheetFragment = new TimesheetFragment();
            timesheetFragment.setArguments(getIntent().getExtras());
        }

        return timesheetFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
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
        if (null != hideRegistered) {
            hideRegistered.setChecked(Settings.shouldHideRegisteredTime(this));
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

        Settings.setHideRegisteredTime(this, item.isChecked());
        getTimesheetFragment().refresh();
    }
}
