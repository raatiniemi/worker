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
import me.raatiniemi.worker.domain.project.model.NewProject
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.cli
import me.raatiniemi.worker.domain.project.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.newTimeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FindActiveProjectsTest {
    private val projects = ProjectInMemoryRepository()
    private val timeIntervals = TimeIntervalInMemoryRepository()

    private lateinit var findActiveProjects: FindActiveProjects

    @Before
    fun setUp() {
        findActiveProjects = FindActiveProjects(projects, timeIntervals)
    }

    @Test
    fun `invoke without projects`() = runBlocking {
        val expected = emptyList<Project>()

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `invoke without active projects`() = runBlocking {
        projects.add(NewProject(android.name))
        val expected = emptyList<Project>()

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `invoke with active project`() = runBlocking {
        projects.add(NewProject(android.name))
        projects.add(NewProject(cli.name))
        timeIntervals.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(10))
        }.also {
            timeIntervals.update(it)
        }
        timeIntervals.add(
            newTimeInterval(cli) {
                start = Milliseconds(1)
            }
        )
        val expected = listOf(cli)

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `invoke with active projects`() = runBlocking {
        projects.add(NewProject(android.name))
        projects.add(NewProject(cli.name))
        timeIntervals.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        timeIntervals.add(
            newTimeInterval(cli) {
                start = Milliseconds(1)
            }
        )
        val expected = listOf(android, cli)

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }
}
