/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.features.settings.view;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.view.View;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.features.settings.Presenters;
import me.raatiniemi.worker.features.settings.presenter.ProjectPresenter;
import me.raatiniemi.worker.presentation.Preferences;
import me.raatiniemi.worker.presentation.util.KeyValueStore;
import me.raatiniemi.worker.presentation.util.Notifications;
import me.raatiniemi.worker.presentation.util.PreferenceUtil;
import timber.log.Timber;

import static me.raatiniemi.worker.presentation.util.PresenterUtil.detachViewIfNotNull;
import static me.raatiniemi.worker.util.NullUtil.isNull;
import static me.raatiniemi.worker.util.NullUtil.nonNull;

public class ProjectFragment extends BasePreferenceFragment
        implements ProjectView, Preference.OnPreferenceChangeListener {
    private static final String CONFIRM_CLOCK_OUT_KEY = "settings_project_confirm_clock_out";
    private static final String TIME_SUMMARY_KEY = "settings_project_time_summary";
    private static final String TIME_SHEET_SUMMARY_FORMAT_KEY = "settings_project_time_sheet_summary_format";
    private static final String ONGOING_NOTIFICATION_ENABLE_KEY = "settings_project_ongoing_notification_enable";
    private static final String ONGOING_NOTIFICATION_CHRONOMETER_KEY = "settings_project_ongoing_notification_chronometer";

    private final Preferences preferences = new Preferences();
    private final KeyValueStore keyValueStore = preferences.getKeyValueStore();

    private final Presenters presenters = new Presenters();
    private final ProjectPresenter presenter = presenters.getProject();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter.attachView(this);

        addPreferencesFromResource(R.xml.settings_project);

        populateCheckBoxPreference(CONFIRM_CLOCK_OUT_KEY, keyValueStore.confirmClockOut());

        try {
            int startingPointForTimeSummary = keyValueStore.startingPointForTimeSummary();

            ListPreference timeSummary = (ListPreference) findPreference(TIME_SUMMARY_KEY);
            timeSummary.setValue(String.valueOf(startingPointForTimeSummary));
            timeSummary.setOnPreferenceChangeListener(this);
        } catch (ClassCastException e) {
            Timber.w(e, "Unable to set listener for 'time_summary'");
        }

        try {
            int timeSheetSummaryFormatValue = keyValueStore.timeSheetSummaryFormat();

            ListPreference timeSheetSummaryFormat = (ListPreference) findPreference(TIME_SHEET_SUMMARY_FORMAT_KEY);
            timeSheetSummaryFormat.setValue(String.valueOf(timeSheetSummaryFormatValue));
            timeSheetSummaryFormat.setOnPreferenceChangeListener(this);
        } catch (ClassCastException e) {
            Timber.w(e, "Unable to set listener for 'timesheet_summary_format'");
        }

        populateCheckBoxPreference(ONGOING_NOTIFICATION_ENABLE_KEY, keyValueStore.ongoingNotification());
        if (Notifications.Companion.isChannelsAvailable()) {
            Preference preference = findPreference(ONGOING_NOTIFICATION_ENABLE_KEY);
            if (nonNull(preference)) {
                preference.setSummary(R.string.activity_settings_project_ongoing_notification_enable_summary);
            }

            preference.setEnabled(isOngoingChannelEnabled());
        }

        populateCheckBoxPreference(ONGOING_NOTIFICATION_CHRONOMETER_KEY, keyValueStore.ongoingNotificationChronometer());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isOngoingChannelEnabled() {
        try {
            NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            return null == nm || !Notifications.Companion.isOngoingChannelDisabled(nm);
        } catch (ClassCastException e) {
            return true;
        }
    }

    private void populateCheckBoxPreference(
            @NonNull String preferenceKey,
            boolean shouldCheck
    ) {
        Preference preference = findPreference(preferenceKey);
        if (isNull(preference)) {
            Timber.w("Unable to find preference with key: %s", preferenceKey);
            return;
        }

        PreferenceUtil.populateCheckBoxPreference(preference, shouldCheck);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        detachViewIfNotNull(presenter);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
        switch (preference.getKey()) {
            case CONFIRM_CLOCK_OUT_KEY:
                toggleConfirmClockOut(preference);
                return true;
            case TIME_SUMMARY_KEY:
            case TIME_SHEET_SUMMARY_FORMAT_KEY:
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
        PreferenceUtil.readCheckBoxPreference(preference, keyValueStore::setConfirmClockOut);
    }

    private void toggleOngoingNotification(@NonNull Preference preference) {
        PreferenceUtil.readCheckBoxPreference(preference, isChecked -> {
            if (isChecked) {
                keyValueStore.enableOngoingNotification();
                return;
            }

            keyValueStore.disableOngoingNotification();
        });
    }

    private void toggleOngoingNotificationChronometer(@NonNull Preference preference) {
        PreferenceUtil.readCheckBoxPreference(preference, isChecked -> {
            if (isChecked) {
                keyValueStore.enableOngoingNotificationChronometer();
                return;
            }

            keyValueStore.disableOngoingNotificationChronometer();
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
        } else if (TIME_SHEET_SUMMARY_FORMAT_KEY.equals(preference.getKey())) {
            changeTimeSheetSummaryFormat(newValue);
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

    private void changeTimeSheetSummaryFormat(Object newValue) {
        int newFormat = Integer.parseInt((String) newValue);
        presenter.changeTimeSheetSummaryFormat(newFormat);
    }

    @Override
    public void showChangeTimeSheetSummaryToFractionSuccessMessage() {
        View contentView = getActivity().findViewById(android.R.id.content);
        if (isNull(contentView)) {
            return;
        }

        Snackbar.make(
                contentView,
                R.string.message_change_time_sheet_summary_format_fraction,
                Snackbar.LENGTH_LONG
        ).show();
    }

    @Override
    public void showChangeTimeSheetSummaryToDigitalClockSuccessMessage() {
        View contentView = getActivity().findViewById(android.R.id.content);
        if (isNull(contentView)) {
            return;
        }

        Snackbar.make(
                contentView,
                R.string.message_change_time_sheet_summary_format_digital_clock,
                Snackbar.LENGTH_LONG
        ).show();
    }

    @Override
    public void showChangeTimeSheetSummaryFormatErrorMessage() {
        View contentView = getActivity().findViewById(android.R.id.content);
        if (isNull(contentView)) {
            return;
        }

        Snackbar.make(
                contentView,
                R.string.error_message_change_time_sheet_summary_format,
                Snackbar.LENGTH_LONG
        ).show();
    }
}
