package me.raatiniemi.worker.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.MvpActivity;
import me.raatiniemi.worker.model.backup.Backup;
import me.raatiniemi.worker.service.DataIntentService;

public class SettingsActivity extends MvpActivity<SettingsPresenter> {
    /**
     * Tag for logging.
     */
    private static final String TAG = "SettingsActivity";

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
        return new SettingsPresenter(this);
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
            String message = "Switch to preference screen '" + key + "' is not implemented";
            Log.w(TAG, message);

            Toast.makeText(this, message, Toast.LENGTH_SHORT)
                .show();

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
                String message = "Preference '" + preference.getTitle() + "' is not implemented";
                Log.d(TAG, message);

                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
                    .show();
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
        }

        @Override
        public int getTitle() {
            return R.string.settings_category_preferences;
        }
    }

    public static class DataFragment extends BasePreferenceFragment {
        private static final SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_data);

            // Tell the SettingsActivity to fetch the latest backup.
            getInstance().getPresenter()
                .getLatestBackup();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
            String intentAction;
            String message;

            // Check if we support the user action, if not, send it to the
            // parent which will handle it.
            switch (preference.getKey()) {
                case SETTINGS_DATA_BACKUP_KEY:
                    // TODO: Refresh the backup summary, if the backup is successful.
                    intentAction = DataIntentService.INTENT_ACTION_BACKUP;
                    message = "Backing up data...";
                    break;
                case SETTINGS_DATA_RESTORE_KEY:
                    intentAction = DataIntentService.INTENT_ACTION_RESTORE;
                    message = "Restoring data...";
                    break;
                default:
                    return super.onPreferenceTreeClick(preferenceScreen, preference);
            }

            // Check that no other data operation is already running, we
            // wouldn't want backup and restore running simultaneously.
            if (DataIntentService.RUNNING.NONE != DataIntentService.getRunning()) {
                Toast.makeText(
                    getActivity(),
                    "Data operation is already running...",
                    Toast.LENGTH_LONG
                ).show();

                // No need to go any futher, we can't allow for any
                // additional data operation to start.
                return false;
            }

            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

            // Start the backup data operation.
            Intent intent = new Intent(getActivity(), DataIntentService.class);
            intent.setAction(intentAction);
            getActivity().startService(intent);

            return false;
        }

        @Override
        public int getTitle() {
            return R.string.settings_screen_data;
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

            String text = "Unable to locate latest backup.";
            if (null != backup) {
                text = "No backup have been performed.";

                Date date = backup.getDate();
                if (null != date) {
                    text = "Last backup was performed at " + mFormat.format(date) + ".";
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

            String text = "Unable to restore, failed to locate backups.";
            boolean enable = false;
            if (null != backup) {
                text = "Nothing to restore, no backup is available.";

                Date date = backup.getDate();
                if (null != date) {
                    text = "Restore backup from " + mFormat.format(date) + ".";
                    enable = true;
                }
            }

            preference.setSummary(text);
            preference.setEnabled(enable);
        }
    }
}
