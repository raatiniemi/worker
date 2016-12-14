/*
 * Copyright (C) 2016 Worker Project
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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.presentation.util.Settings;
import timber.log.Timber;

public class ProjectFragment extends BasePreferenceFragment
        implements Preference.OnPreferenceChangeListener {
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
