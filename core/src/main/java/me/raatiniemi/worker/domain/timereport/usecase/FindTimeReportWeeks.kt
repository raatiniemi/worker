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

package me.raatiniemi.worker.domain.timereport.usecase

import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository

class FindTimeReportWeeks(
    private val keyValueStore: KeyValueStore,
    private val repository: TimeReportRepository
) {
    suspend operator fun invoke(project: Project, loadRange: LoadRange): List<TimeReportWeek> {
        val shouldHideRegisteredTime = keyValueStore.bool(AppKeys.HIDE_REGISTERED_TIME, false)
        return if (shouldHideRegisteredTime) {
            repository.findNotRegisteredWeeks(project, loadRange)
        } else {
            repository.findWeeks(project, loadRange)
        }
    }
}
