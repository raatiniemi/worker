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
}

// TODO: Should time sheet summary format constants be moved to a better location?
const val TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK: Int = 1
const val TIME_REPORT_SUMMARY_FORMAT_FRACTION: Int = 2
