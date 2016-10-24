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

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.data.service.data.BackupService;
import me.raatiniemi.worker.data.service.data.RestoreService;
import me.raatiniemi.worker.presentation.settings.model.Backup;
import me.raatiniemi.worker.presentation.settings.presenter.SettingsPresenter;
import me.raatiniemi.worker.presentation.util.PermissionUtil;
import me.raatiniemi.worker.presentation.util.Settings;
import me.raatiniemi.worker.presentation.view.activity.MvpActivity;
import timber.log.Timber;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static me.raatiniemi.util.NullUtil.isNull;
import static me.raatiniemi.util.NullUtil.nonNull;

public class SettingsActivity extends MvpActivity<SettingsPresenter>
        implements SettingsView, ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * Key for the project preference.
     */
    private static final String SETTINGS_PROJECT_KEY = "settings_project";

    /**
     * Key for confirm clock out preference.
     */
    private static final String SETTINGS_PROJECT_CONFIRM_CLOCK_OUT_KEY = "settings_project_confirm_clock_out";

    /**
     * Key for time summary preference.
     */
    private static final String SETTINGS_PROJECT_TIME_SUMMARY_KEY = "settings_project_time_summary";

    /**
     * Key for ongoing notification preference.
     */
    private static final String SETTINGS_PROJECT_ONGOING_NOTIFICATION_ENABLE_KEY = "settings_project_ongoing_notification_enable";

    /**
     * Key for the ongoing notification chronometer preference.
     */
    private static final String SETTINGS_PROJECT_ONGOING_NOTIFICATION_CHRONOMETER_KEY
            = "settings_project_ongoing_notification_chronometer";

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

    @Inject
    SettingsPresenter presenter;

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

        ((Worker) getApplication()).getSettingsComponent()
                .inject(this);

        getPresenter().attachView(this);
    }

    @Override
    protected SettingsPresenter getPresenter() {
        return presenter;
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

    /**
     * Switch the currently displayed preference screen.
     *
     * @param key Key for the new preference screen.
     */
    private void switchPreferenceScreen(String key) {
        Fragment fragment;
        switch (key) {
            case SETTINGS_PROJECT_KEY:
                fragment = new ProjectFragment();
                break;
            case SETTINGS_DATA_KEY:
                fragment = new DataFragment();
                break;
            default:
                Timber.w("Switch to preference screen '%s' is not implemented", key);
                displayPreferenceScreenNotImplementedMessage();
                return;
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, key)
                .addToBackStack(key)
                .commit();
    }

    private void displayPreferenceScreenNotImplementedMessage() {
        View contentView = findViewById(android.R.id.content);
        if (isNull(contentView)) {
            return;
        }

        Snackbar.make(
                contentView,
                R.string.error_message_preference_screen_not_implemented,
                Snackbar.LENGTH_SHORT
        ).show();
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

    @Override
    public void setLatestBackup(@Nullable Backup backup) {
        DataFragment fragment = getDataFragment();
        if (isNull(fragment)) {
            Timber.d("DataFragment is not available");
            return;
        }

        fragment.setBackupSummary(backup);
        fragment.setRestoreSummary(backup);
    }

    @Override
    public void showChangeTimeSummaryStartingPointToWeekSuccessMessage() {
        View contentView = findViewById(android.R.id.content);
        if (isNull(contentView)) {
            return;
        }

        Snackbar.make(
                contentView,
                R.string.message_change_time_summary_starting_point_week,
                Snackbar.LENGTH_LONG
        ).show();
    }

    @Override
    public void showChangeTimeSummaryStartingPointToMonthSuccessMessage() {
        View contentView = findViewById(android.R.id.content);
        if (isNull(contentView)) {
            return;
        }

        Snackbar.make(
                contentView,
                R.string.message_change_time_summary_starting_point_month,
                Snackbar.LENGTH_LONG
        ).show();
    }

    @Override
    public void showChangeTimeSummaryStartingPointErrorMessage() {
        View contentView = findViewById(android.R.id.content);
        if (isNull(contentView)) {
            return;
        }

        Snackbar.make(
                contentView,
                R.string.error_message_change_time_summary_starting_point,
                Snackbar.LENGTH_LONG
        ).show();
    }

    public abstract static class BasePreferenceFragment extends PreferenceFragment {
        @Override
        public void onResume() {
            super.onResume();

            // Set the title for the preference fragment.
            getActivity().setTitle(getTitle());
        }

        SettingsActivity getSettingsActivity() {
            return (SettingsActivity) getActivity();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
            super.onPreferenceTreeClick(preferenceScreen, preference);
            if (preference instanceof PreferenceScreen) {
                getSettingsActivity().switchPreferenceScreen(preference.getKey());
            } else {
                Timber.d("Preference '%s' is not implemented", preference.getTitle());
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
        public abstract int getTitle();
    }

    public static class SettingsFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings);
        }

        @Override
        public int getTitle() {
            return R.string.activity_settings_title;
        }
    }

    public static class ProjectFragment extends BasePreferenceFragment
            implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_project);

            try {
                // Set the preference value for the clock out confirmation.
                CheckBoxPreference confirmClockOut =
                        (CheckBoxPreference) findPreference(SETTINGS_PROJECT_CONFIRM_CLOCK_OUT_KEY);
                confirmClockOut.setChecked(Settings.shouldConfirmClockOut(getActivity()));
            } catch (ClassCastException e) {
                Timber.w(e, "Unable to get value for 'confirm_clock_out'");
            }

            try {
                int startingPointForTimeSummary = Settings.getStartingPointForTimeSummary(getActivity());

                ListPreference timeSummary = (ListPreference) findPreference(SETTINGS_PROJECT_TIME_SUMMARY_KEY);
                timeSummary.setValue(String.valueOf(startingPointForTimeSummary));
                timeSummary.setOnPreferenceChangeListener(this);
            } catch (ClassCastException e) {
                Timber.w(e, "Unable to set listener for 'time_summary'");
            }

            try {
                CheckBoxPreference ongoingNotification =
                        (CheckBoxPreference) findPreference(SETTINGS_PROJECT_ONGOING_NOTIFICATION_ENABLE_KEY);
                ongoingNotification.setChecked(Settings.isOngoingNotificationEnabled(getActivity()));
            } catch (ClassCastException e) {
                Timber.w(e, "Unable to get value for 'ongoing_notification'");
            }

            try {
                CheckBoxPreference ongoingNotificationChronometer =
                        (CheckBoxPreference) findPreference(SETTINGS_PROJECT_ONGOING_NOTIFICATION_CHRONOMETER_KEY);
                ongoingNotificationChronometer.setChecked(Settings.isOngoingNotificationChronometerEnabled(getActivity()));
            } catch (ClassCastException e) {
                Timber.w(e, "Unable to get value for 'ongoing_notification_chronometer'");
            }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
            if (SETTINGS_PROJECT_CONFIRM_CLOCK_OUT_KEY.equals(preference.getKey())) {
                try {
                    // Set the clock out confirmation preference.
                    boolean checked = ((CheckBoxPreference) preference).isChecked();
                    Settings.setConfirmClockOut(getActivity(), checked);
                    return true;
                } catch (ClassCastException e) {
                    Timber.w(e, "Unable to set value for 'confirm_clock_out'");
                }
            }

            if (SETTINGS_PROJECT_TIME_SUMMARY_KEY.equals(preference.getKey())) {
                return true;
            }

            if (SETTINGS_PROJECT_ONGOING_NOTIFICATION_ENABLE_KEY.equals(preference.getKey())) {
                try {
                    // Set the clock out confirmation preference.
                    boolean checked = ((CheckBoxPreference) preference).isChecked();
                    if (checked) {
                        Settings.enableOngoingNotification(getActivity());
                        return true;
                    }

                    Settings.disableOngoingNotification(getActivity());
                    return true;
                } catch (ClassCastException e) {
                    Timber.w(e, "Unable to set value for 'ongoing_notification'");
                }
            }
            if (SETTINGS_PROJECT_ONGOING_NOTIFICATION_CHRONOMETER_KEY.equals(preference.getKey())) {
                try {
                    // Set the clock out confirmation preference.
                    boolean checked = ((CheckBoxPreference) preference).isChecked();
                    if (checked) {
                        Settings.enableOngoingNotificationChronometer(getActivity());
                        return true;
                    }

                    Settings.disableOngoingNotificationChronometer(getActivity());
                    return true;
                } catch (ClassCastException e) {
                    Timber.w(e, "Unable to set value for 'ongoing_notification_chronometer'");
                }
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override
        public int getTitle() {
            return R.string.activity_settings_project;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (SETTINGS_PROJECT_TIME_SUMMARY_KEY.equals(preference.getKey())) {
                changeTimeSummaryStartingPoint(newValue);
                return true;
            }
            return false;
        }

        private void changeTimeSummaryStartingPoint(Object newStartingPoint) {
            int startingPoint = Integer.parseInt((String) newStartingPoint);
            getSettingsActivity().getPresenter()
                    .changeTimeSummaryStartingPoint(startingPoint);
        }
    }

    public static class DataFragment extends BasePreferenceFragment {
        private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

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
         * Initiate the backup action.
         */
        private void runBackup() {
            // We should only attempt to backup if permission to write
            // to the external storage have been granted.
            if (PermissionUtil.havePermission(getActivity(), WRITE_EXTERNAL_STORAGE)) {
                Timber.d("Permission for writing to external storage is granted");
                Snackbar.make(
                        getActivity().findViewById(android.R.id.content),
                        R.string.message_backing_up_data,
                        Snackbar.LENGTH_SHORT
                ).show();

                BackupService.startBackup(getActivity());
                return;
            }

            // We have not been granted permission to write to the external storage. Display
            // the permission message and allow the user to initiate the permission request.
            Timber.d("Permission for writing to external storage is not granted");
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.message_permission_write_backup,
                    Snackbar.LENGTH_INDEFINITE
            ).setAction(android.R.string.ok, view -> ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE
            )).show();
        }

        /**
         * Initiate the restore action.
         */
        private void runRestore() {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.activity_settings_restore_confirm_title)
                    .setMessage(R.string.activity_settings_restore_confirm_message)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        Snackbar.make(
                                getActivity().findViewById(android.R.id.content),
                                R.string.message_restoring_data,
                                Snackbar.LENGTH_SHORT
                        ).show();

                        RestoreService.startRestore(getActivity());
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }

        /**
         * Get the latest backup, if permission have been granted.
         */
        private void checkLatestBackup() {
            // We should only attempt to check the latest backup if permission
            // to read the external storage have been granted.
            if (PermissionUtil.havePermission(getActivity(), READ_EXTERNAL_STORAGE)) {
                // Tell the SettingsActivity to fetch the latest backup.
                Timber.d("Permission for reading external storage is granted");
                getSettingsActivity().getPresenter()
                        .getLatestBackup();

                // No need to go any further.
                return;
            }

            // We have not been granted permission to read the external storage. Display the
            // permission message and allow the user to initiate the permission request.
            Timber.d("Permission for reading external storage is not granted");
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.message_permission_read_backup,
                    Snackbar.LENGTH_INDEFINITE
            ).setAction(android.R.string.ok, view -> ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE
            )).show();
        }

        /**
         * Set the backup summary based on the latest backup.
         *
         * @param backup Latest available backup.
         */
        void setBackupSummary(@Nullable Backup backup) {
            Preference preference = findPreference(SETTINGS_DATA_BACKUP_KEY);
            if (isNull(preference)) {
                Timber.w("Unable to find preference with key: %s", SETTINGS_DATA_BACKUP_KEY);
                return;
            }

            String text = getString(R.string.activity_settings_backup_unable_to_find);
            if (nonNull(backup)) {
                text = getString(R.string.activity_settings_backup_none_available);

                Date date = backup.getDate();
                if (nonNull(date)) {
                    text = getString(
                            R.string.activity_settings_backup_performed_at,
                            format.format(date)
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
            if (isNull(preference)) {
                Timber.w("Unable to find preference with key: %s", SETTINGS_DATA_RESTORE_KEY);
                return;
            }

            String text = getString(R.string.activity_settings_restore_unable_to_find);
            boolean enable = false;
            if (nonNull(backup)) {
                text = getString(R.string.activity_settings_restore_none_available);

                Date date = backup.getDate();
                if (nonNull(date)) {
                    text = getString(
                            R.string.activity_settings_restore_from,
                            format.format(date)
                    );
                    enable = true;
                }
            }

            preference.setSummary(text);
            preference.setEnabled(enable);
        }
    }
}
