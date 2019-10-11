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

package me.raatiniemi.worker.domain.timereport.repository

import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek

interface TimeReportRepository {
    fun countWeeks(project: Project): Int

    fun countNotRegisteredWeeks(project: Project): Int

    fun count(project: Project): Int

    fun countNotRegistered(project: Project): Int

    fun findWeeks(project: Project, loadRange: LoadRange): List<TimeReportWeek>

    fun findNotRegisteredWeeks(project: Project, loadRange: LoadRange): List<TimeReportWeek>

    fun findAll(project: Project, loadRange: LoadRange): List<TimeReportDay>

    fun findNotRegistered(project: Project, loadRange: LoadRange): List<TimeReportDay>
}
