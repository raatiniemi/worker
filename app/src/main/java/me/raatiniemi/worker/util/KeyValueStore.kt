/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.util

import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint

interface KeyValueStore {
    fun set(key: String, value: Boolean)
    fun set(key: String, value: Int)

    fun bool(key: String, defaultValue: Boolean): Boolean
    fun int(key: String, defaultValue: Int = 0): Int

    // TODO: Move configurations to extensions when calling code is in kotlin.

    fun setHideRegisteredTime(value: Boolean) {
        set(AppKeys.HIDE_REGISTERED_TIME.rawValue, value)
    }

    fun hideRegisteredTime(): Boolean {
        return bool(AppKeys.HIDE_REGISTERED_TIME.rawValue, false)
    }

    fun setConfirmClockOut(value: Boolean) {
        return set(AppKeys.CONFIRM_CLOCK_OUT.rawValue, value)
    }

    fun confirmClockOut(): Boolean {
        return bool(AppKeys.CONFIRM_CLOCK_OUT.rawValue, true)
    }

    fun enableOngoingNotification() {
        set(AppKeys.ONGOING_NOTIFICATION_ENABLED.rawValue, true)
    }

    fun disableOngoingNotification() {
        set(AppKeys.ONGOING_NOTIFICATION_ENABLED.rawValue, false)
    }

    fun ongoingNotification(): Boolean {
        return bool(AppKeys.ONGOING_NOTIFICATION_ENABLED.rawValue, true)
    }

    fun enableOngoingNotificationChronometer() {
        set(AppKeys.ONGOING_NOTIFICATION_CHRONOMETER_ENABLED.rawValue, true)
    }

    fun disableOngoingNotificationChronometer() {
        set(AppKeys.ONGOING_NOTIFICATION_CHRONOMETER_ENABLED.rawValue, false)
    }

    fun ongoingNotificationChronometer(): Boolean {
        return bool(AppKeys.ONGOING_NOTIFICATION_CHRONOMETER_ENABLED.rawValue, true)
    }

    fun useWeekForTimeSummaryStartingPoint() {
        set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.WEEK.rawValue)
    }

    fun useMonthForTimeSummaryStartingPoint() {
        set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)
    }

    fun startingPointForTimeSummary(): Int {
        return int(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)
    }

    fun useFractionAsTimeSheetSummaryFormat() {
        set(AppKeys.TIME_SHEET_SUMMARY_FORMAT.rawValue, TIME_SHEET_SUMMARY_FORMAT_FRACTION)
    }

    fun useDigitalClockAsTimeSheetSummaryFormat() {
        set(AppKeys.TIME_SHEET_SUMMARY_FORMAT.rawValue, TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK)
    }

    fun timeSheetSummaryFormat(): Int {
        return int(AppKeys.TIME_SHEET_SUMMARY_FORMAT.rawValue, TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK)
    }
}

// TODO: Should time sheet summary format constants be moved to a better location?
const val TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK: Int = 1
const val TIME_SHEET_SUMMARY_FORMAT_FRACTION: Int = 2
