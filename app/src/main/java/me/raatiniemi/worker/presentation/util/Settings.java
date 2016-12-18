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

package me.raatiniemi.worker.presentation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;

/**
 * Communicate with the shared preferences.
 */
public class Settings implements HideRegisteredTimePreferences, ConfirmClockOutPreferences, TimeSummaryPreferences {
    /**
     * Preference key for hiding registered time.
     */
    private static final String PREF_HIDE_REGISTERED_TIME = "pref_hide_registered_time";

    /**
     * Preference key for clock out confirmation.
     */
    private static final String PREF_CONFIRM_CLOCK_OUT = "pref_confirm_clock_out";

    /**
     * Preference key to check if ongoing notification is enabled.
     */
    private static final String PREF_ONGOING_NOTIFICATION_ENABLED = "pref_ongoing_notification_enabled";

    /**
     * Preference key to check if the ongoing notification chronometer is enabled.
     */
    private static final String PREF_ONGOING_NOTIFICATION_CHRONOMETER_ENABLED
            = "pref_ongoing_notification_chronometer_enabled";

    /**
     * Preference key to check which starting point to use for time summary.
     */
    private static final String PREF_TIME_SUMMARY = "pref_time_summary";

    private final SharedPreferences preferences;

    public Settings(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean shouldHideRegisteredTime() {
        return preferences.getBoolean(PREF_HIDE_REGISTERED_TIME, false);
    }

    @Override
    public void setHideRegisteredTime(boolean newValue) {
        preferences.edit()
                .putBoolean(PREF_HIDE_REGISTERED_TIME, newValue)
                .apply();
    }

    @Override
    public boolean shouldConfirmClockOut() {
        return preferences.getBoolean(PREF_CONFIRM_CLOCK_OUT, true);
    }

    @Override
    public void setConfirmClockOut(boolean newValue) {
        preferences.edit()
                .putBoolean(PREF_CONFIRM_CLOCK_OUT, newValue)
                .apply();
    }

    /**
     * Check if ongoing notification is enabled.
     *
     * @param context Context to be used to edit the {@link android.content.SharedPreferences}.
     * @return 'true' if ongoing notification is enabled, otherwise 'false'.
     */
    public static boolean isOngoingNotificationEnabled(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_ONGOING_NOTIFICATION_ENABLED, true);
    }

    /**
     * Enable ongoing notification.
     *
     * @param context Context to be used to edit the {@link android.content.SharedPreferences}.
     */
    public static void enableOngoingNotification(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_ONGOING_NOTIFICATION_ENABLED, true).apply();
    }

    /**
     * Disable ongoing notification.
     *
     * @param context Context to be used to edit the {@link android.content.SharedPreferences}.
     */
    public static void disableOngoingNotification(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_ONGOING_NOTIFICATION_ENABLED, false).apply();
    }

    /**
     * Check if the ongoing notification chronometer is enabled.
     *
     * @param context Context to be used to edit the {@link android.content.SharedPreferences}.
     * @return 'true' if the ongoing notification chronometer is enabled, otherwise 'false'.
     */
    public static boolean isOngoingNotificationChronometerEnabled(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_ONGOING_NOTIFICATION_CHRONOMETER_ENABLED, true);
    }

    /**
     * Enable the ongoing notification chronometer.
     *
     * @param context Context to be used to edit the {@link android.content.SharedPreferences}.
     */
    public static void enableOngoingNotificationChronometer(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_ONGOING_NOTIFICATION_CHRONOMETER_ENABLED, true).apply();
    }

    /**
     * Disable the ongoing notification chronometer.
     *
     * @param context Context to be used to edit the {@link android.content.SharedPreferences}.
     */
    public static void disableOngoingNotificationChronometer(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_ONGOING_NOTIFICATION_CHRONOMETER_ENABLED, false).apply();
    }

    @Override
    public int getStartingPointForTimeSummary() {
        return preferences.getInt(PREF_TIME_SUMMARY, GetProjectTimeSince.MONTH);
    }

    @Override
    public void useWeekForTimeSummaryStartingPoint() {
        preferences.edit()
                .putInt(PREF_TIME_SUMMARY, GetProjectTimeSince.WEEK)
                .apply();
    }

    @Override
    public void useMonthForTimeSummaryStartingPoint() {
        preferences.edit()
                .putInt(PREF_TIME_SUMMARY, GetProjectTimeSince.MONTH)
                .apply();
    }
}
