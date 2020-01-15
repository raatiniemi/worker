/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

/**
 * Declares constants used to trace performance of certain events in the application.
 */
internal object TracePerformanceEvents {
    const val CREATE_PROJECT = "create_project"
    const val REMOVE_PROJECT = "remove_project"

    const val CLOCK_IN = "clock_in"
    const val CLOCK_OUT = "clock_out"

    const val REFRESH_PROJECTS = "refresh_projects"
    const val REFRESH_TIME_REPORT = "refresh_time_report"
}
