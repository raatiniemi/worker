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

package me.raatiniemi.worker.domain.interactor

import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore

typealias CountTimeReports = (Project) -> Int
typealias FindTimeReports = (Project, LoadRange) -> List<TimeReportDay>

fun countTimeReports(
    keyValueStore: KeyValueStore,
    repository: TimeReportRepository
): CountTimeReports {
    return { project ->
        val shouldHideRegisteredTime = keyValueStore.bool(AppKeys.HIDE_REGISTERED_TIME, false)
        if (shouldHideRegisteredTime) {
            repository.countNotRegistered(project)
        } else {
            repository.count(project)
        }
    }
}

fun findTimeReports(
    keyValueStore: KeyValueStore,
    repository: TimeReportRepository
): FindTimeReports {
    return { project, loadRange ->
        val shouldHideRegisteredTime = keyValueStore.bool(AppKeys.HIDE_REGISTERED_TIME, false)
        if (shouldHideRegisteredTime) {
            repository.findNotRegistered(project, loadRange)
        } else {
            repository.findAll(project, loadRange)
        }
    }
}
