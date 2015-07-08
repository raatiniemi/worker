package me.raatiniemi.worker.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.base.view.BaseActivity;

public class SettingsActivity extends BaseActivity {
    /**
     * Tag for logging.
     */
    private static final String TAG = "SettingsActivity";

    /**
     * Key for the data preference.
     */
    private static final String SETTINGS_DATA_KEY = "settings_data";

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
    }

    /**
     * Switch the currently displayed preference screen.
     *
     * @param key Key for the new preference screen.
     */
    void switchPreferenceScreen(String key) {
        switch (key) {
            case SETTINGS_DATA_KEY:
                getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DataFragment())
                    .commit();
                break;
            default:
                String message = "Switch to preference screen '" + key + "' is not implemented";
                Log.w(TAG, message);

                Toast.makeText(this, message, Toast.LENGTH_SHORT)
                    .show();
                break;
        }
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

    public static class BasePreferenceFragment extends PreferenceFragment {
        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
            super.onPreferenceTreeClick(preferenceScreen, preference);
            if (preference instanceof PreferenceScreen) {
                getInstance().switchPreferenceScreen(preference.getKey());
            }
            return false;
        }
    }

    public static class SettingsFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getActivity().setTitle(R.string.settings_category_preferences);
            addPreferencesFromResource(R.xml.settings);
        }
    }

    public static class DataFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getActivity().setTitle(R.string.settings_screen_data);
            addPreferencesFromResource(R.xml.settings_data);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
            if (preference instanceof PreferenceScreen) {
                super.onPreferenceTreeClick(preferenceScreen, preference);
            } else {
                String message = "Preference '" + preference.getTitle() + "' is not implemented";
                Log.d(TAG, message);

                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
                    .show();
            }
            return false;
        }
    }
}
