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
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore

internal class TimeReportDataSource(
        private val projectId: Long,
        private val keyValueStore: KeyValueStore,
        private val repository: TimeReportRepository
) : PositionalDataSource<TimeReportDay>() {
    private val shouldHideRegisteredTime: Boolean
        get() = keyValueStore.bool(
                AppKeys.HIDE_REGISTERED_TIME.rawValue,
                false
        )

    private fun countTotal(): Int {
        return if (shouldHideRegisteredTime) {
            repository.countNotRegistered(projectId)
        } else {
            repository.count(projectId)
        }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<TimeReportDay>) {
        val totalCount = countTotal()
        val position = computeInitialLoadPosition(params, totalCount)
        val loadSize = computeInitialLoadSize(params, position, totalCount)

        callback.onResult(
                repository.findAll(projectId, position, loadSize),
                position,
                totalCount
        )
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<TimeReportDay>) {
        callback.onResult(repository.findAll(projectId, params.startPosition, params.loadSize))
    }
}
