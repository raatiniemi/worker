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

package me.raatiniemi.worker.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.usecase.CountTimeReportWeeks
import me.raatiniemi.worker.domain.timereport.usecase.FindTimeReportWeeks
import me.raatiniemi.worker.feature.projects.model.ProjectProvider
import timber.log.Timber

internal class TimeReportWeekPagingSource(
    private val projectProvider: ProjectProvider,
    private val countTimeReportWeeks: CountTimeReportWeeks,
    private val findTimeReportWeeks: FindTimeReportWeeks
) : PagingSource<Int, TimeReportWeek>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimeReportWeek> {
        return try {
            val project = requireNotNull(projectProvider.value) {
                "No project is available via provider"
            }

            val loadRange = calculateLoadRange(params)
            val total = countTimeReportWeeks(project)

            LoadResult.Page(
                data = findTimeReportWeeks(project, loadRange),
                prevKey = null,
                nextKey = calculateNextKey(loadRange, total)
            )
        } catch (e: Exception) {
            Timber.e(e, "Unable to load time report week")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TimeReportWeek>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
