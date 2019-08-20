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
import me.raatiniemi.worker.domain.model.LoadPosition
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.LoadSize
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.usecase.CountTimeReports
import me.raatiniemi.worker.domain.usecase.FindTimeReports
import me.raatiniemi.worker.features.projects.model.ProjectProvider
import timber.log.Timber

internal class TimeReportDataSource(
    private val projectProvider: ProjectProvider,
    private val countTimeReports: CountTimeReports,
    private val findTimeReports: FindTimeReports
) : PositionalDataSource<TimeReportDay>() {
    private val project: Project?
        get() {
            val project = projectProvider.value
            if (project == null) {
                Timber.w("No project is available from `ProjectHolder`")
                return null
            }

            return project
        }

    private fun countTotal(): Int {
        return project?.let(countTimeReports) ?: 0
    }

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<TimeReportDay>
    ) {
        val totalCount = countTotal()
        val position = computeInitialLoadPosition(params, totalCount)
        val loadSize = computeInitialLoadSize(params, position, totalCount)

        val loadRange = LoadRange(
            LoadPosition(position),
            LoadSize(loadSize)
        )
        callback.onResult(loadData(loadRange), position, totalCount)
    }

    private fun loadData(loadRange: LoadRange): List<TimeReportDay> {
        val project = this.project
        return when (project) {
            is Project -> findTimeReports(project, loadRange)
            else -> emptyList()
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<TimeReportDay>) {
        val loadRange = LoadRange(
            LoadPosition(params.startPosition),
            LoadSize(params.loadSize)
        )
        callback.onResult(loadData(loadRange))
    }
}
