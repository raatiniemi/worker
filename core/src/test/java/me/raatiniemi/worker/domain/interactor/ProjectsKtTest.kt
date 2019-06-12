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

import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.repository.ProjectRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectsKtTest {
    private lateinit var repository: ProjectRepository

    private lateinit var countProjects: CountProjects
    private lateinit var findProjects: FindProjects
    private lateinit var findAllProjects: FindAllProjects

    @Before
    fun setUp() {
        repository = ProjectInMemoryRepository()

        countProjects = countProjects(repository)
        findProjects = findProjects(repository)
        findAllProjects = findAllProjects(repository)
    }

    @Test
    fun `count projects without projects`() {
        val expected = 0

        val actual = countProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `count projects with project`() {
        repository.add(NewProject(ProjectName("Project #1")))
        val expected = 1

        val actual = countProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `count projects with projects`() {
        repository.add(NewProject(ProjectName("Project #1")))
        repository.add(NewProject(ProjectName("Project #2")))
        val expected = 2

        val actual = countProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `find projects without projects`() {
        val expected = emptyList<Project>()
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = findProjects(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find projects with project`() {
        repository.add(NewProject(ProjectName("Project #1")))
        val expected = listOf(
            Project(1, "Project #1")
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = findProjects(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find projects with projects`() {
        repository.add(NewProject(ProjectName("Project #1")))
        repository.add(NewProject(ProjectName("Project #2")))
        val expected = listOf(
            Project(1, "Project #1"),
            Project(2, "Project #2")
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = findProjects(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all projects without projects`() {
        val expected = emptyList<Project>()

        val actual = findAllProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `find all projects with project`() {
        repository.add(NewProject(ProjectName("Project #1")))
        val expected = listOf(
            Project(1, "Project #1")
        )

        val actual = findAllProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `find all projects with projects`() {
        repository.add(NewProject(ProjectName("Project #1")))
        repository.add(NewProject(ProjectName("Project #2")))
        val expected = listOf(
            Project(1, "Project #1"),
            Project(2, "Project #2")
        )

        val actual = findAllProjects()

        assertEquals(expected, actual)
    }
}
