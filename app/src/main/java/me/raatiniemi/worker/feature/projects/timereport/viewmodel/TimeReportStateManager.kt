/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.projects.timereport.viewmodel

import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportSelectAction
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportState

internal interface TimeReportStateManager {
    fun expanded(day: TimeReportDay): Boolean

    fun expand(day: TimeReportDay)

    fun collapse(day: TimeReportDay)

    fun state(day: TimeReportDay): TimeReportState

    fun state(timeInterval: TimeInterval): TimeReportState

    fun consume(action: TimeReportSelectAction)
}
