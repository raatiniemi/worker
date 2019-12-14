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

package me.raatiniemi.worker.domain.project.usecase

import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.cli
import me.raatiniemi.worker.domain.project.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.hours
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FindActiveProjectsTest {
    private lateinit var createProject: CreateProject
    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut

    private lateinit var findActiveProjects: FindActiveProjects

    @Before
    fun setUp() {
        val projects = ProjectInMemoryRepository()
        val timeIntervals = TimeIntervalInMemoryRepository()
        val findProject = FindProject(projects)

        createProject = CreateProject(findProject, projects)
        clockIn = ClockIn(timeIntervals)
        clockOut = ClockOut(timeIntervals)

        findActiveProjects = FindActiveProjects(projects, timeIntervals)
    }

    @Test
    fun `find active projects without projects`() = runBlocking {
        val expected = emptyList<Project>()

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `find active projects without active projects`() = runBlocking {
        createProject(android.name)
        val expected = emptyList<Project>()

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `find active projects with active project`() = runBlocking {
        val now = Milliseconds.now
        createProject(android.name)
        createProject(cli.name)
        clockIn(android, now)
        clockOut(android, now + 4.hours)
        clockIn(cli, now + 5.hours)
        val expected = listOf(
            cli
        )

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `find active projects with active projects`() = runBlocking {
        val now = Milliseconds.now
        createProject(android.name)
        createProject(cli.name)
        clockIn(android, now)
        clockIn(cli, now + 5.hours)
        val expected = listOf(
            android,
            cli
        )

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }
}
