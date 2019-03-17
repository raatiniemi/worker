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

package me.raatiniemi.worker.data.projects.datasource

import androidx.paging.PositionalDataSource
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import me.raatiniemi.worker.features.project.model.ProjectProvider
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore
import timber.log.Timber

internal class TimeReportDataSource(
        private val projectProvider: ProjectProvider,
        private val keyValueStore: KeyValueStore,
        private val repository: TimeReportRepository
) : PositionalDataSource<TimeReportDay>() {
    private val shouldHideRegisteredTime: Boolean
        get() = keyValueStore.bool(
                AppKeys.HIDE_REGISTERED_TIME,
                false
        )

    private val project: Project?
        get() = projectProvider.value.run {
            val project = value
            if (project == null) {
                Timber.w("No project is available from `ProjectHolder`")
                return null
            }

            return project
        }

    private fun countTotal(): Int {
        val project = this.project ?: return 0

        return if (shouldHideRegisteredTime) {
            repository.countNotRegistered(project.id)
        } else {
            repository.count(project.id)
        }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<TimeReportDay>) {
        val totalCount = countTotal()
        val position = computeInitialLoadPosition(params, totalCount)
        val loadSize = computeInitialLoadSize(params, position, totalCount)

        callback.onResult(loadData(position, loadSize), position, totalCount)
    }

    private fun loadData(position: Int, loadSize: Int): List<TimeReportDay> {
        val project = this.project ?: return emptyList()

        return if (shouldHideRegisteredTime) {
            repository.findNotRegistered(project.id, position, loadSize)
        } else {
            repository.findAll(project.id, position, loadSize)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<TimeReportDay>) {
        callback.onResult(loadData(params.startPosition, params.loadSize))
    }
}
