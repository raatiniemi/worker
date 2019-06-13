/*
 * Copyright (C) 2018 Tobias Raatiniemi
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
import me.raatiniemi.worker.domain.model.projectName
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.repository.ProjectRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FindProjectTest {
    private val repository: ProjectRepository = ProjectInMemoryRepository()
    private lateinit var findProject: FindProject

    @Before
    fun setUp() {
        findProject = FindProject(repository)
    }

    @Test
    fun `invoke without projects`() {
        val actual = findProject(projectName("Name"))

        assertNull(actual)
    }

    @Test
    fun `invoke with projects`() {
        val expected = Project(id = 1, name = projectName("Name"))
        repository.add(NewProject(projectName("Name")))

        val actual = findProject(projectName("Name"))

        assertEquals(expected, actual)
    }

    @Test
    fun `invoke with lowercase project name`() {
        val expected = Project(id = 1, name = projectName("Name"))
        repository.add(NewProject(projectName("Name")))

        val actual = findProject(projectName("Name"))

        assertEquals(expected, actual)
    }
}
