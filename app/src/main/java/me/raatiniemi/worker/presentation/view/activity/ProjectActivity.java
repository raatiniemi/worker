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

package me.raatiniemi.worker.presentation.view.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.BaseActivity;
import me.raatiniemi.worker.presentation.view.fragment.TimesheetFragment;
import me.raatiniemi.worker.util.Settings;

public class ProjectActivity extends BaseActivity {
    /**
     * Tag for the timesheet fragment.
     */
    public static final String FRAGMENT_TIMESHEET_TAG = "timesheet";

    /**
     * Reference to the timesheet fragment.
     */
    private TimesheetFragment mTimesheetFragment;

    /**
     * Get the timesheet fragment, handles construction if needed.
     *
     * @return Timesheet fragment.
     */
    private TimesheetFragment getTimesheetFragment() {
        if (null == mTimesheetFragment) {
            mTimesheetFragment = new TimesheetFragment();
            mTimesheetFragment.setArguments(getIntent().getExtras());
        }

        return mTimesheetFragment;
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
        switch (item.getItemId()) {
            case R.id.actions_project_hide_registered:
                item.setChecked(!item.isChecked());

                // Save the hide preference to the SharedPreferences.
                Settings.setHideRegisteredTime(this, item.isChecked());
                getTimesheetFragment().refresh();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
