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
import me.raatiniemi.worker.domain.repository.ProjectRepository

internal class ProjectDataSource(
    private val repository: ProjectRepository
) : PositionalDataSource<Project>() {
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Project>) {
        val totalCount = repository.count()
        val position = computeInitialLoadPosition(params, totalCount)
        val loadSize = computeInitialLoadSize(params, position, totalCount)

        callback.onResult(repository.findAll(position, loadSize), position, totalCount)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Project>) {
        callback.onResult(repository.findAll(params.startPosition, params.loadSize))
    }
}
