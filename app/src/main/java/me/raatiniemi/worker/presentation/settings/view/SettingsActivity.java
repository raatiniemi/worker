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
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.presentation.util.PermissionUtil;
import me.raatiniemi.worker.presentation.view.activity.BaseActivity;
import timber.log.Timber;

import static me.raatiniemi.util.NullUtil.isNull;

public class SettingsActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * Key for the project preference.
     */
    static final String SETTINGS_PROJECT_KEY = "settings_project";

    /**
     * Key for the data preference.
     */
    static final String SETTINGS_DATA_KEY = "settings_data";

    /**
     * Code for requesting permission for reading external storage.
     */
    static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    /**
     * Code for requesting permission for writing to external storage.
     */
    static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;

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
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        // Both of the permission requests require that the `DataFragment` is
        // available, no ned to go any further if the fragment is not available.
        DataFragment fragment = getDataFragment();
        if (isNull(fragment)) {
            super.onRequestPermissionsResult(
                    requestCode,
                    permissions,
                    grantResults
            );
            return;
        }

        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE:
                // Whether we've been granted read permission or not, a call to
                // the `checkLatestBackup` will handle both scenarios.
                fragment.checkLatestBackup();
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                // Only if we've been granted write permission should we backup.
                // We should not display the permission message again unless the
                // user attempt to backup.
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    fragment.runBackup();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
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
        FragmentManager fragmentManager = getFragmentManager();

        // Depending on which fragment is contained within the
        // container, the back button will behave differently.
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        Class<SettingsFragment> settings = SettingsFragment.class;
        if (!settings.equals(fragment.getClass())) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Get preference fragment by tag.
     *
     * @param tag Tag for the fragment.
     * @param <T> Type of the fragment.
     * @return Preference fragment, or null if unable to retrieve fragment.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private <T extends BasePreferenceFragment> T getPreferenceFragment(String tag) {
        T fragment = null;

        try {
            fragment = (T) getFragmentManager().findFragmentByTag(tag);
            if (isNull(fragment)) {
                // Should only be an informational log message since
                // the activity is working with multiple fragments
                // and the user can navigate up or down before the
                // background operations are finished.
                Timber.i("Unable to find fragment with tag: %s", tag);
            }
        } catch (ClassCastException e) {
            Timber.w(e, "Unable to cast preference fragment");
        }

        return fragment;
    }

    /**
     * Get the data fragment.
     *
     * @return Data fragment, or null if unable to get fragment.
     */
    @Nullable
    private DataFragment getDataFragment() {
        return getPreferenceFragment(SETTINGS_DATA_KEY);
    }
}
