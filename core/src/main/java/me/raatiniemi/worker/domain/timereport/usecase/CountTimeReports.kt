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

package me.raatiniemi.worker.domain.timereport.usecase

import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository

class CountTimeReports(
    private val keyValueStore: KeyValueStore,
    private val repository: TimeReportRepository
) {
    operator fun invoke(project: Project): Int {
        val shouldHideRegisteredTime = keyValueStore.bool(AppKeys.HIDE_REGISTERED_TIME, false)
        return if (shouldHideRegisteredTime) {
            repository.countNotRegistered(project)
        } else {
            repository.count(project)
        }
    }
}
