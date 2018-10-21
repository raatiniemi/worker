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

package me.raatiniemi.worker.features.project.timesheet.model

import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimesheetItem

data class TimesheetAdapterResult(
        val group: Int,
        val child: Int,
        private val item: TimesheetItem
) : Comparable<TimesheetAdapterResult> {
    val timeInterval: TimeInterval
        get() = item.asTimeInterval()

    override fun compareTo(other: TimesheetAdapterResult): Int {
        if (group == other.group) {
            if (child == other.child) {
                return 0
            }

            return if (child > other.child) {
                1
            } else -1
        }

        return if (group > other.group) {
            1
        } else -1
    }
}
