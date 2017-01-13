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
import android.support.design.widget.Snackbar;
import android.view.View;

import javax.inject.Inject;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.presentation.settings.presenter.ProjectPresenter;
import me.raatiniemi.worker.presentation.util.ConfirmClockOutPreferences;
import me.raatiniemi.worker.presentation.util.OngoingNotificationPreferences;
import me.raatiniemi.worker.presentation.util.PreferenceUtil;
import me.raatiniemi.worker.presentation.util.TimeSummaryPreferences;
import timber.log.Timber;

import static me.raatiniemi.util.NullUtil.isNull;
import static me.raatiniemi.util.NullUtil.nonNull;

public class ProjectFragment extends BasePreferenceFragment
        implements ProjectView, Preference.OnPreferenceChangeListener {
    private static final String CONFIRM_CLOCK_OUT_KEY = "settings_project_confirm_clock_out";
    private static final String TIME_SUMMARY_KEY = "settings_project_time_summary";
    private static final String ONGOING_NOTIFICATION_ENABLE_KEY = "settings_project_ongoing_notification_enable";
    private static final String ONGOING_NOTIFICATION_CHRONOMETER_KEY = "settings_project_ongoing_notification_chronometer";

    @Inject
    ConfirmClockOutPreferences confirmClockOutPreferences;

    @Inject
    OngoingNotificationPreferences ongoingNotificationPreferences;

    @Inject
    TimeSummaryPreferences timeSummaryPreferences;

    @Inject
    ProjectPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Worker application = (Worker) getActivity().getApplication();
        application.getSettingsComponent().inject(this);

        presenter.attachView(this);

        addPreferencesFromResource(R.xml.settings_project);

        try {
            // Set the preference value for the clock out confirmation.
            CheckBoxPreference confirmClockOut =
                    (CheckBoxPreference) findPreference(CONFIRM_CLOCK_OUT_KEY);
            confirmClockOut.setChecked(confirmClockOutPreferences.shouldConfirmClockOut());
        } catch (ClassCastException e) {
            Timber.w(e, "Unable to get value for 'confirm_clock_out'");
        }

        try {
            int startingPointForTimeSummary = timeSummaryPreferences.getStartingPointForTimeSummary();

            ListPreference timeSummary = (ListPreference) findPreference(TIME_SUMMARY_KEY);
            timeSummary.setValue(String.valueOf(startingPointForTimeSummary));
            timeSummary.setOnPreferenceChangeListener(this);
        } catch (ClassCastException e) {
            Timber.w(e, "Unable to set listener for 'time_summary'");
        }

        try {
            CheckBoxPreference ongoingNotification =
                    (CheckBoxPreference) findPreference(ONGOING_NOTIFICATION_ENABLE_KEY);
            ongoingNotification.setChecked(ongoingNotificationPreferences.isOngoingNotificationEnabled());
        } catch (ClassCastException e) {
            Timber.w(e, "Unable to get value for 'ongoing_notification'");
        }

        try {
            CheckBoxPreference ongoingNotificationChronometer =
                    (CheckBoxPreference) findPreference(ONGOING_NOTIFICATION_CHRONOMETER_KEY);
            ongoingNotificationChronometer.setChecked(ongoingNotificationPreferences.isOngoingNotificationChronometerEnabled());
        } catch (ClassCastException e) {
            Timber.w(e, "Unable to get value for 'ongoing_notification_chronometer'");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (nonNull(presenter)) {
            presenter.detachView();
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
        switch (preference.getKey()) {
            case CONFIRM_CLOCK_OUT_KEY:
                toggleConfirmClockOut(preference);
                return true;
            case TIME_SUMMARY_KEY:
                return true;
            case ONGOING_NOTIFICATION_ENABLE_KEY:
                toggleOngoingNotification(preference);
                return true;
            case ONGOING_NOTIFICATION_CHRONOMETER_KEY:
                toggleOngoingNotificationChronometer(preference);
                return true;
            default:
                return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    private void toggleConfirmClockOut(@NonNull Preference preference) {
        PreferenceUtil.readCheckBoxPreference(preference,
                confirmClockOutPreferences::setConfirmClockOut);
    }

    private void toggleOngoingNotification(@NonNull Preference preference) {
        PreferenceUtil.readCheckBoxPreference(preference, isChecked -> {
            if (isChecked) {
                ongoingNotificationPreferences.enableOngoingNotification();
                return;
            }

            ongoingNotificationPreferences.disableOngoingNotification();
        });
    }

    private void toggleOngoingNotificationChronometer(@NonNull Preference preference) {
        PreferenceUtil.readCheckBoxPreference(preference, isChecked -> {
            if (isChecked) {
                ongoingNotificationPreferences.enableOngoingNotificationChronometer();
                return;
            }

            ongoingNotificationPreferences.disableOngoingNotificationChronometer();
        });
    }

    @Override
    public int getTitle() {
        return R.string.activity_settings_project;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (TIME_SUMMARY_KEY.equals(preference.getKey())) {
            changeTimeSummaryStartingPoint(newValue);
            return true;
        }
        return false;
    }

    private void changeTimeSummaryStartingPoint(Object newStartingPoint) {
        int startingPoint = Integer.parseInt((String) newStartingPoint);
        presenter.changeTimeSummaryStartingPoint(startingPoint);
    }

    @Override
    public void showChangeTimeSummaryStartingPointToWeekSuccessMessage() {
        View contentView = getActivity().findViewById(android.R.id.content);
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
        View contentView = getActivity().findViewById(android.R.id.content);
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
        View contentView = getActivity().findViewById(android.R.id.content);
        if (isNull(contentView)) {
            return;
        }

        Snackbar.make(
                contentView,
                R.string.error_message_change_time_summary_starting_point,
                Snackbar.LENGTH_LONG
        ).show();
    }
}
