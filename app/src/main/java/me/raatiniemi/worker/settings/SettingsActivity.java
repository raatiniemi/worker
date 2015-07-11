package me.raatiniemi.worker.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.MvpActivity;
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
     * Set the summary for the backup preference.
     *
     * @param summary Summary for the backup preference.
     */
    public void setBackupSummary(String summary) {
        try {
            // Retrieve the DataFragment which has the Backup-preference.
            DataFragment fragment = (DataFragment)
                getFragmentManager().findFragmentByTag(SETTINGS_DATA_KEY);
            if (null == fragment) {
                // This should only be an informational log message since the
                // user might have navigated up the SettingsActivity fragment
                // stack, i.e. the DataFragment have been detached.
                //
                // This is especially true if the storage examination takes
                // longer than normal, e.g. if other operations are also using
                // on the IO-scheduler.
                Log.i(TAG, "Unable to find the DataFragment");
                return;
            }

            // Retrieve the Backup-preference from the DataFragment.
            Preference preference = fragment.findPreference(SETTINGS_DATA_BACKUP_KEY);
            if (null == preference) {
                Log.w(TAG, "Unable to find the Backup-preference");
                return;
            }

            // Set the summary for the Backup-preference.
            preference.setSummary(summary);
        } catch (ClassCastException e) {
            Log.w(TAG, "Unable to cast fragment to DataFragment: " + e.getMessage());
        }
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
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_data);

            // Tell the settings activity to fetch the
            // backup summary via the presenter.
            getInstance().getPresenter()
                .getBackupSummary();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
            // Check if we support the user action, if not, send it to the
            // parent which will handle it.
            switch (preference.getKey()) {
                case SETTINGS_DATA_BACKUP_KEY:
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

            Toast.makeText(getActivity(), "Backing up data...", Toast.LENGTH_SHORT)
                .show();

            // Start the backup data operation.
            Intent intent = new Intent(getActivity(), DataIntentService.class);
            intent.setAction(DataIntentService.INTENT_ACTION_BACKUP);
            getActivity().startService(intent);

            // TODO: Refresh the backup summary, if the backup is successful.

            return false;
        }

        @Override
        public int getTitle() {
            return R.string.settings_screen_data;
        }
    }
}
