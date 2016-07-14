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

import me.raatiniemi.worker.domain.exception.InvalidStartingPointException;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;

/**
 * Communicate with the shared preferences.
 */
public class Settings {
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

    private Settings() {
    }

    /**
     * Check if the registered time should be hidden.
     *
     * @param context Context to be used to lookup the {@link android.content.SharedPreferences}.
     * @return 'true' if registered time should be hidden, otherwise 'false'.
     */
    public static boolean shouldHideRegisteredTime(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_HIDE_REGISTERED_TIME, false);
    }

    /**
     * Set the preference indicating whether the registered time should be hidden.
     *
     * @param context  Context to be used to edit the {@link android.content.SharedPreferences}.
     * @param newValue 'true' if registered should be hidden, otherwise 'false'.
     */
    public static void setHideRegisteredTime(final Context context, boolean newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_HIDE_REGISTERED_TIME, newValue).apply();
    }

    /**
     * Check if clock out require confirmation.
     *
     * @param context Context to be used to look up the {@link android.content.SharedPreferences}.
     * @return 'true' if clock out require confirmation, otherwise 'false'.
     */
    public static boolean shouldConfirmClockOut(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_CONFIRM_CLOCK_OUT, true);
    }

    /**
     * Set the preference for clock out confirmation.
     *
     * @param context  Context to be used to edit the {@link android.content.SharedPreferences}.
     * @param newValue 'true' if clock out requires confirmation, otherwise 'false'.
     */
    public static void setConfirmClockOut(final Context context, boolean newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_CONFIRM_CLOCK_OUT, newValue).apply();
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

    /**
     * Get the time summary starting point, default value is {@link GetProjectTimeSince#sMonth}.
     *
     * @param context Context to be used to read from the {@link android.content.SharedPreferences}.
     * @return Time summary starting point, e.g. {@link GetProjectTimeSince#sWeek} or {@link GetProjectTimeSince#sMonth}.
     */
    public static int getStartingPointForTimeSummary(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PREF_TIME_SUMMARY, GetProjectTimeSince.sMonth);
    }

    /**
     * Use week for time summary starting point, i.e. {@link GetProjectTimeSince#sWeek}.
     *
     * @param context Context to be used to edit the {@link SharedPreferences}.
     */
    public static void useWeekForTimeSummaryStartingPoint(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(PREF_TIME_SUMMARY, GetProjectTimeSince.sWeek).apply();
    }

    /**
     * Use month for time summary starting point, i.e. {@link GetProjectTimeSince#sMonth}.
     *
     * @param context Context to be used to edit the {@link SharedPreferences}.
     */
    public static void useMonthForTimeSummaryStartingPoint(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(PREF_TIME_SUMMARY, GetProjectTimeSince.sMonth).apply();
    }
}
