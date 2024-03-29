/*
 * Copyright (C) 2022 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.projects.timereport.model

import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay

sealed class TimeReportTapAction : TimeReportSelectAction {
    data class TapDay(val day: TimeReportDay) : TimeReportTapAction() {
        override val items = day.timeIntervals.toSet()
    }

    data class TapItem(val timeInterval: TimeInterval) : TimeReportTapAction() {
        override val items = setOf(timeInterval)
    }
}
