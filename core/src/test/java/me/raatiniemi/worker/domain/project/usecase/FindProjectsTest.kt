/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.project.usecase

import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.model.LoadPosition
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.LoadSize
import me.raatiniemi.worker.domain.project.model.NewProject
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.cli
import me.raatiniemi.worker.domain.project.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FindProjectsTest {
    private lateinit var projects: ProjectRepository

    private lateinit var findProjects: FindProjects

    @Before
    fun setUp() {
        projects = ProjectInMemoryRepository()

        findProjects = FindProjects(projects)
    }

    @Test
    fun `find projects without projects`() = runBlocking {
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )
        val expected = emptyList<Project>()

        val actual = findProjects(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find projects with project`() = runBlocking {
        projects.add(NewProject(android.name))
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )
        val expected = listOf(
            android
        )

        val actual = findProjects(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find projects with projects`() = runBlocking {
        projects.add(NewProject(android.name))
        projects.add(NewProject(cli.name))
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )
        val expected = listOf(
            android,
            cli
        )

        val actual = findProjects(loadRange)

        assertEquals(expected, actual)
    }
}
