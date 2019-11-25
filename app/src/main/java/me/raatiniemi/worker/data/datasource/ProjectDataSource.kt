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

package me.raatiniemi.worker.data.datasource

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import me.raatiniemi.worker.domain.model.LoadPosition
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.LoadSize
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.usecase.CountProjects
import me.raatiniemi.worker.domain.project.usecase.FindProjects

internal class ProjectDataSource(
    private val countProjects: CountProjects,
    private val findProjects: FindProjects
) : PositionalDataSource<Project>() {
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Project>) {
        val totalCount = countProjects()
        val position = computeInitialLoadPosition(params, totalCount)
        val loadSize = computeInitialLoadSize(params, position, totalCount)

        val loadRange = LoadRange(
            LoadPosition(position),
            LoadSize(loadSize)
        )
        callback.onResult(findProjects(loadRange), position, totalCount)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Project>) {
        val loadRange = LoadRange(
            LoadPosition(params.startPosition),
            LoadSize(params.loadSize)
        )
        callback.onResult(findProjects(loadRange))
    }

    class Factory(
        private val countProjects: CountProjects,
        private val findProjects: FindProjects
    ) : DataSource.Factory<Int, Project>() {
        override fun create(): ProjectDataSource {
            return ProjectDataSource(
                countProjects,
                findProjects
            )
        }
    }
}
