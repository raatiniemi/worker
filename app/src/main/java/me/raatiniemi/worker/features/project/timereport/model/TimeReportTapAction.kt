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

package me.raatiniemi.worker.features.project.timereport.model

import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.model.TimeReportItem

sealed class TimeReportTapAction : TimeReportSelectAction {
    data class TapDay(val day: TimeReportDay) : TimeReportTapAction() {
        override val items = day.items
    }

    data class TapItem(val item: TimeReportItem) : TimeReportTapAction() {
        override val items = listOf(item)
    }
}