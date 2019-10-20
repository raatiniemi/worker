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

package me.raatiniemi.worker.features.projects.timereport.viewmodel

import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportLongPressAction
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportSelectAction
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportState
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportTapAction

internal interface TimeReportStateManager {
    fun expanded(position: Int): Boolean

    fun expand(position: Int)

    fun collapse(position: Int)

    fun state(day: TimeReportDay): TimeReportState

    fun state(timeInterval: TimeInterval): TimeReportState

    fun consume(action: TimeReportSelectAction)
}
