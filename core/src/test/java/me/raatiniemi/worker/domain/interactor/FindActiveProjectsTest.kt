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

import me.raatiniemi.worker.domain.model.NewProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.newTimeInterval
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FindActiveProjectsTest {
    private val projectRepository = ProjectInMemoryRepository()
    private val timeIntervalRepository = TimeIntervalInMemoryRepository()

    private lateinit var findActiveProjects: FindActiveProjects

    @Before
    fun setUp() {
        findActiveProjects = FindActiveProjects(projectRepository, timeIntervalRepository)
    }

    @Test
    fun `invoke without projects`() {
        val expected = emptyList<Project>()

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `invoke without active projects`() {
        projectRepository.add(NewProject("Project name #1"))
        val expected = emptyList<Project>()

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `invoke with active project`() {
        projectRepository.add(NewProject("Project name #1"))
        projectRepository.add(NewProject("Project name #2"))
        timeIntervalRepository.add(newTimeInterval {
            projectId = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        })
        timeIntervalRepository.add(newTimeInterval {
            projectId = 2
            startInMilliseconds = 1
            stopInMilliseconds = 0
        })
        val expected = listOf(Project(2, "Project name #2"))

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `invoke with active projects`() {
        projectRepository.add(NewProject("Project name #1"))
        projectRepository.add(NewProject("Project name #2"))
        timeIntervalRepository.add(newTimeInterval {
            projectId = 1
            startInMilliseconds = 1
            stopInMilliseconds = 0
        })
        timeIntervalRepository.add(newTimeInterval {
            projectId = 2
            startInMilliseconds = 1
            stopInMilliseconds = 0
        })
        val expected = listOf(
                Project(1, "Project name #1"),
                Project(2, "Project name #2")
        )

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }
}
