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
        projectRepository.add(NewProject(android.name))
        val expected = emptyList<Project>()

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `invoke with active project`() {
        projectRepository.add(NewProject(android.name))
        projectRepository.add(NewProject(cli.name))
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(10))
        }.also {
            timeIntervalRepository.update(it)
        }
        timeIntervalRepository.add(
            newTimeInterval(cli) {
                start = Milliseconds(1)
            }
        )
        val expected = listOf(cli)

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }

    @Test
    fun `invoke with active projects`() {
        projectRepository.add(NewProject(android.name))
        projectRepository.add(NewProject(cli.name))
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        timeIntervalRepository.add(
            newTimeInterval(cli) {
                start = Milliseconds(1)
            }
        )
        val expected = listOf(android, cli)

        val actual = findActiveProjects()

        assertEquals(expected, actual)
    }
}