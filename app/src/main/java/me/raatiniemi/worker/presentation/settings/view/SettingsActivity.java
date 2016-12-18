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

package me.raatiniemi.worker.presentation.settings.view;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.presentation.view.activity.BaseActivity;

import static me.raatiniemi.util.NullUtil.isNull;

public class SettingsActivity extends BaseActivity {
    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (isNull(savedInstanceState)) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Override the toolbar back button to behave as the back button.
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (shouldPopBackStack()) {
            getFragmentManager().popBackStack();
            return;
        }

        super.onBackPressed();
    }

    private boolean shouldPopBackStack() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);

        Class<SettingsFragment> settings = SettingsFragment.class;
        return !settings.equals(fragment.getClass());
    }
}
