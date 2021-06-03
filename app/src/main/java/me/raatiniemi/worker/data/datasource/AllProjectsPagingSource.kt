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
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.usecase.CountProjects
import me.raatiniemi.worker.domain.project.usecase.FindProjects
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.timeIntervalStartingPoint
import me.raatiniemi.worker.domain.timeinterval.usecase.GetProjectTimeSince
import me.raatiniemi.worker.feature.projects.all.model.ProjectsItem
import timber.log.Timber

internal class AllProjectsPagingSource(
    private val keyValueStore: KeyValueStore,
    private val countProjects: CountProjects,
    private val findProjects: FindProjects,
    private val getProjectTimeSince: GetProjectTimeSince
) : PagingSource<Int, ProjectsItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProjectsItem> {
        return try {
            val loadRange = calculateLoadRange(params)
            val total = countProjects()

            LoadResult.Page(
                data = findProjects(loadRange)
                    .map { projectsItem(it) },
                prevKey = null,
                nextKey = calculateNextKey(loadRange, total)
            )
        } catch (e: Exception) {
            Timber.e(e, "Unable to load all projects")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProjectsItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private suspend fun projectsItem(project: Project): ProjectsItem {
        return ProjectsItem(project, getProjectTime(project))
    }

    private suspend fun getProjectTime(project: Project): List<TimeInterval> {
        return try {
            getProjectTimeSince(project, timeIntervalStartingPoint(keyValueStore))
        } catch (e: Exception) {
            Timber.w(e, "Unable to get registered time for project")
            emptyList()
        }
    }
}
