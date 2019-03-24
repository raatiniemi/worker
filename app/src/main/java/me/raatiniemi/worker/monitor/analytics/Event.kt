/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.monitor.analytics

sealed class Event(val name: String, val parameters: Map<String, String> = emptyMap()) {
    object TapProjectOpen : Event(TAP_PROJECT_OPEN_NAME)
    object TapProjectToggle : Event(TAP_PROJECT_TOGGLE_NAME)
    object TapProjectAt : Event(TAP_PROJECT_AT_NAME)
    object TapProjectRemove : Event(TAP_PROJECT_REMOVE_NAME)

    object ProjectCreate : Event(PROJECT_CREATE_NAME)
    object ProjectClockIn : Event(PROJECT_CLOCK_IN_NAME)
    object ProjectClockOut : Event(PROJECT_CLOCK_OUT_NAME)
    object ProjectRemove : Event(PROJECT_REMOVE_NAME)

    data class TimeReportToggle(private val count: Int) : Event(
            TIME_REPORT_TOGGLE_NAME,
            mapOf(PARAMETER_COUNT_NAME to count.toString())
    )

    data class TimeReportRemove(private val count: Int) : Event(
            TIME_REPORT_REMOVE_NAME,
            mapOf(PARAMETER_COUNT_NAME to count.toString())
    )

    companion object {
        private const val TAP_PROJECT_OPEN_NAME = "tap_project_open"
        private const val TAP_PROJECT_TOGGLE_NAME = "tap_project_toggle"
        private const val TAP_PROJECT_AT_NAME = "tap_project_at"
        private const val TAP_PROJECT_REMOVE_NAME = "tap_project_remove"

        private const val PROJECT_CREATE_NAME = "project_create"
        private const val PROJECT_CLOCK_IN_NAME = "project_clock_in"
        private const val PROJECT_CLOCK_OUT_NAME = "project_clock_out"
        private const val PROJECT_REMOVE_NAME = "project_remove"

        private const val TIME_REPORT_TOGGLE_NAME = "time_report_toggle"
        private const val TIME_REPORT_REMOVE_NAME = "time_report_remove"

        private const val PARAMETER_COUNT_NAME = "count"
    }
}
