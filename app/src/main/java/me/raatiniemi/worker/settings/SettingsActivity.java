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

package me.raatiniemi.worker.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.MvpActivity;
import me.raatiniemi.worker.model.backup.Backup;
import me.raatiniemi.worker.service.DataIntentService;
import me.raatiniemi.worker.util.PermissionUtil;
import me.raatiniemi.worker.util.Settings;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SettingsActivity extends MvpActivity<SettingsPresenter>
        implements SettingsView, ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * Tag for logging.
     */
    private static final String TAG = "SettingsActivity";

    /**
     * Key for confirm clock out preference.
     */
    private static final String SETTINGS_CONFIRM_CLOCK_OUT_KEY = "settings_confirm_clock_out";

    /**
     * Key for the data preference.
     */
    private static final String SETTINGS_DATA_KEY = "settings_data";

    /**
     * Key for the data backup preference.
     */
    private static final String SETTINGS_DATA_BACKUP_KEY = "settings_data_backup";

    /**
     * Key for the data restore preference.
     */
    private static final String SETTINGS_DATA_RESTORE_KEY = "settings_data_restore";

    /**
     * Code for requesting permission for reading external storage.
     */
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    /**
     * Code for requesting permission for writing to external storage.
     */
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    /**
     * Instance for the SettingsActivity.
     */
    private static SettingsActivity sInstance;

    /**
     * Retrieve the instance for the SettingsActivity.
     *
     * @return Instance for the SettingsActivity.
     */
    static SettingsActivity getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Store the instance in a static variable,
        // so it's accessible from the SettingsFragment.
        sInstance = this;

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .commit();
        }

        getPresenter().attachView(this);
    }

    @Override
    protected SettingsPresenter createPresenter() {
        return new SettingsPresenter(this, EventBus.getDefault());
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
        if (null == fragment) {
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

    /**
     * Switch the currently displayed preference screen.
     *
     * @param key Key for the new preference screen.
     */
    void switchPreferenceScreen(String key) {
        Fragment fragment = null;
        switch (key) {
            case SETTINGS_DATA_KEY:
                fragment = new DataFragment();
                break;
        }

        if (null == fragment) {
            Log.w(TAG, "Switch to preference screen '" + key + "' is not implemented");
            Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.error_message_preference_screen_not_implemented,
                    Snackbar.LENGTH_SHORT
            ).show();

            return;
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, key)
                .addToBackStack(key)
                .commit();
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
            if (null == fragment) {
                // Should only be an informational log message since
                // the activity is working with multiple fragments
                // and the user can navigate up or down before the
                // background operations are finished.
                Log.i(TAG, "Unable to find fragment with tag: " + tag);
            }
        } catch (ClassCastException e) {
            Log.w(TAG, "Unable to cast preference fragment: " + e.getMessage());
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

    @Override
    public void setLatestBackup(@Nullable Backup backup) {
        DataFragment fragment = getDataFragment();
        if (null == fragment) {
            Log.d(TAG, "DataFragment is not available");
            return;
        }

        fragment.setBackupSummary(backup);
        fragment.setRestoreSummary(backup);
    }

    public abstract static class BasePreferenceFragment extends PreferenceFragment {
        @Override
        public void onResume() {
            super.onResume();

            // Set the title for the preference fragment.
            getActivity().setTitle(getTitle());
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
            super.onPreferenceTreeClick(preferenceScreen, preference);
            if (preference instanceof PreferenceScreen) {
                getInstance().switchPreferenceScreen(preference.getKey());
            } else {
                Log.d(TAG, "Preference '" + preference.getTitle() + "' is not implemented");
                Snackbar.make(
                        getActivity().findViewById(android.R.id.content),
                        R.string.error_message_preference_not_implemented,
                        Snackbar.LENGTH_SHORT
                ).show();
            }
            return false;
        }

        /**
         * Get the resource id for the preference fragment title.
         *
         * @return Resource id for the preference fragment title.
         */
        abstract public int getTitle();
    }

    public static class SettingsFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings);

            try {
                // Set the preference value for the clock out confirmation.
                CheckBoxPreference confirmClockOut =
                        (CheckBoxPreference) findPreference(SETTINGS_CONFIRM_CLOCK_OUT_KEY);
                confirmClockOut.setChecked(Settings.shouldConfirmClockOut(getActivity()));
            } catch (ClassCastException e) {
                Log.w(TAG, "Unable to get value for 'confirm_clock_out'");
            }
        }

        @Override
        public int getTitle() {
            return R.string.activity_settings_preferences;
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
            if (SETTINGS_CONFIRM_CLOCK_OUT_KEY.equals(preference.getKey())) {
                try {
                    // Set the clock out confirmation preference.
                    boolean checked = ((CheckBoxPreference) preference).isChecked();
                    Settings.setConfirmClockOut(getActivity(), checked);
                    return true;
                } catch (ClassCastException e) {
                    Log.w(TAG, "Unable to set value for 'confirm_clock_out'");
                }
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    public static class DataFragment extends BasePreferenceFragment {
        private static final SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_data);

            // Check for the latest backup.
            checkLatestBackup();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
            // Check if we support the user action, if not, send it to the
            // parent which will handle it.
            switch (preference.getKey()) {
                case SETTINGS_DATA_BACKUP_KEY:
                    runBackup();
                    break;
                case SETTINGS_DATA_RESTORE_KEY:
                    runRestore();
                    break;
                default:
                    return super.onPreferenceTreeClick(preferenceScreen, preference);
            }

            return false;
        }

        @Override
        public int getTitle() {
            return R.string.activity_settings_data;
        }

        /**
         * Check if a backup/restore action is already running.
         *
         * @return true if action is running, otherwise false.
         */
        private boolean checkRunningAction() {
            // Check that no other data operation is already running, we
            // don't want two actions to run simultaneously.
            boolean isRunning = DataIntentService.RUNNING.NONE != DataIntentService.getRunning();
            if (isRunning) {
                Snackbar.make(
                        getActivity().findViewById(android.R.id.content),
                        R.string.error_message_data_operation_already_running,
                        Snackbar.LENGTH_LONG
                ).show();
            }

            return isRunning;
        }

        /**
         * Initiate the backup action.
         */
        private void runBackup() {
            // Check if a action is already running, we don't want two actions
            // to run simultaneously.
            if (checkRunningAction()) {
                // Another action is running, no need to go any further.
                return;
            }

            // We should only attempt to backup if permission to write
            // to the external storage have been granted.
            if (PermissionUtil.havePermission(getActivity(), WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG, "Permission for writing to external storage is granted");
                Snackbar.make(
                        getActivity().findViewById(android.R.id.content),
                        R.string.message_backing_up_data,
                        Snackbar.LENGTH_SHORT
                ).show();

                // Start the backup action.
                Intent intent = new Intent(getActivity(), DataIntentService.class);
                intent.setAction(DataIntentService.INTENT_ACTION_BACKUP);
                getActivity().startService(intent);

                // No need to go any further.
                return;
            }

            // We have not been granted permission to write to the external storage. Display
            // the permission message and allow the user to initiate the permission request.
            Log.d(TAG, "Permission for writing to external storage is not granted");
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.message_permission_write_backup,
                    Snackbar.LENGTH_INDEFINITE
            ).setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_EXTERNAL_STORAGE
                    );
                }
            }).show();
        }

        /**
         * Initiate the restore action.
         */
        private void runRestore() {
            // Check if a action is already running, we don't want two actions
            // to run simultaneously.
            if (checkRunningAction()) {
                // Another action is running, no need to go any further.
                return;
            }

            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.message_restoring_data,
                    Snackbar.LENGTH_SHORT
            ).show();

            // Start the restore action.
            Intent intent = new Intent(getActivity(), DataIntentService.class);
            intent.setAction(DataIntentService.INTENT_ACTION_RESTORE);
            getActivity().startService(intent);
        }

        /**
         * Get the latest backup, if permission have been granted.
         */
        private void checkLatestBackup() {
            // We should only attempt to check the latest backup if permission
            // to read the external storage have been granted.
            if (PermissionUtil.havePermission(getActivity(), READ_EXTERNAL_STORAGE)) {
                // Tell the SettingsActivity to fetch the latest backup.
                Log.d(TAG, "Permission for reading external storage is granted");
                getInstance().getPresenter()
                        .getLatestBackup();

                // No need to go any further.
                return;
            }

            // We have not been granted permission to read the external storage. Display the
            // permission message and allow the user to initiate the permission request.
            Log.d(TAG, "Permission for reading external storage is not granted");
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.message_permission_read_backup,
                    Snackbar.LENGTH_INDEFINITE
            ).setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{READ_EXTERNAL_STORAGE},
                            REQUEST_READ_EXTERNAL_STORAGE
                    );
                }
            }).show();
        }

        /**
         * Set the backup summary based on the latest backup.
         *
         * @param backup Latest available backup.
         */
        void setBackupSummary(@Nullable Backup backup) {
            Preference preference = findPreference(SETTINGS_DATA_BACKUP_KEY);
            if (null == preference) {
                Log.w(TAG, "Unable to find preference with key: " + SETTINGS_DATA_BACKUP_KEY);
                return;
            }

            String text = getString(R.string.activity_settings_backup_unable_to_find);
            if (null != backup) {
                text = getString(R.string.activity_settings_backup_none_available);

                Date date = backup.getDate();
                if (null != date) {
                    text = getString(
                            R.string.activity_settings_backup_performed_at,
                            mFormat.format(date)
                    );
                }
            }

            preference.setSummary(text);
        }

        /**
         * Set the restore summary based on the latest backup.
         *
         * @param backup Latest available backup.
         */
        void setRestoreSummary(@Nullable Backup backup) {
            Preference preference = findPreference(SETTINGS_DATA_RESTORE_KEY);
            if (null == preference) {
                Log.w(TAG, "Unable to find preference with key: " + SETTINGS_DATA_RESTORE_KEY);
                return;
            }

            String text = getString(R.string.activity_settings_restore_unable_to_find);
            boolean enable = false;
            if (null != backup) {
                text = getString(R.string.activity_settings_restore_none_available);

                Date date = backup.getDate();
                if (null != date) {
                    text = getString(
                            R.string.activity_settings_restore_from,
                            mFormat.format(date)
                    );
                    enable = true;
                }
            }

            preference.setSummary(text);
            preference.setEnabled(enable);
        }
    }
}
