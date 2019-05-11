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

import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.domain.interactor.countProjects
import me.raatiniemi.worker.domain.interactor.findProjects
import me.raatiniemi.worker.domain.model.NewProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.repository.ProjectRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProjectDataSourceTest {
    private lateinit var repository: ProjectRepository
    private lateinit var dataSource: ProjectDataSource

    @Before
    fun setUp() {
        repository = ProjectInMemoryRepository()
        dataSource = ProjectDataSource(
            countProjects(repository),
            findProjects(repository)
        )
    }

    @Test
    fun loadInitial_withoutProjects() {
        val expected = PositionalDataSourceResult<Project>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withProject() {
        val projects = listOf(
            repository.add(NewProject("Project #1"))
        )
        val expected = PositionalDataSourceResult(
            data = projects,
            position = 0,
            totalCount = projects.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withProjects() {
        val projects = listOf(
            repository.add(NewProject("Project #1")),
            repository.add(NewProject("Project #2")),
            repository.add(NewProject("Project #3")),
            repository.add(NewProject("Project #4")),
            repository.add(NewProject("Project #5")),
            repository.add(NewProject("Project #6")),
            repository.add(NewProject("Project #7")),
            repository.add(NewProject("Project #8")),
            repository.add(NewProject("Project #9"))
        )
        val expected = PositionalDataSourceResult(
            data = projects,
            position = 0,
            totalCount = projects.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withProjectsBeyondPageSize() {
        val projects = listOf(
            repository.add(NewProject("Project #1")),
            repository.add(NewProject("Project #2")),
            repository.add(NewProject("Project #3")),
            repository.add(NewProject("Project #4")),
            repository.add(NewProject("Project #5")),
            repository.add(NewProject("Project #6")),
            repository.add(NewProject("Project #7")),
            repository.add(NewProject("Project #8")),
            repository.add(NewProject("Project #9"))
        )
        val expected = PositionalDataSourceResult(
            data = projects.take(5),
            position = 0,
            totalCount = projects.size
        )

        dataSource.loadInitial(
            loadInitialParams(requestedStartPosition = 0, requestedLoadSize = 5),
            loadInitialCallback {
                assertEquals(expected, it)
            }
        )
    }

    @Test
    fun loadInitial_withProjectsAndPosition() {
        val projects = listOf(
            repository.add(NewProject("Project #1")),
            repository.add(NewProject("Project #2")),
            repository.add(NewProject("Project #3")),
            repository.add(NewProject("Project #4")),
            repository.add(NewProject("Project #5")),
            repository.add(NewProject("Project #6")),
            repository.add(NewProject("Project #7")),
            repository.add(NewProject("Project #8")),
            repository.add(NewProject("Project #9"))
        )
        val expected = PositionalDataSourceResult(
            data = projects.drop(5),
            position = 5,
            totalCount = projects.size
        )

        dataSource.loadInitial(
            loadInitialParams(requestedStartPosition = 5, requestedLoadSize = 5),
            loadInitialCallback {
                assertEquals(expected, it)
            }
        )
    }
}
