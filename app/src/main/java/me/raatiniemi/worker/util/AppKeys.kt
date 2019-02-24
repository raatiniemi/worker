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

enum class AppKeys(val rawValue: String) {
    HIDE_REGISTERED_TIME("pref_hide_registered_time"),
    CONFIRM_CLOCK_OUT("pref_confirm_clock_out"),
    ONGOING_NOTIFICATION_ENABLED("pref_ongoing_notification_enabled"),
    ONGOING_NOTIFICATION_CHRONOMETER_ENABLED("pref_ongoing_notification_chronometer_enabled"),
    TIME_SUMMARY("pref_time_summary")
}