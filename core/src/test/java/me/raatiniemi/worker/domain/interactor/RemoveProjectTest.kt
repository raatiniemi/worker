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

import me.raatiniemi.worker.domain.exception.NoProjectIdException
import me.raatiniemi.worker.domain.model.NewProject
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.repository.ProjectRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RemoveProjectTest {
    private val repository: ProjectRepository = ProjectInMemoryRepository()
    private lateinit var removeProject: RemoveProject

    @Before
    fun setUp() {
        removeProject = RemoveProject(repository)
    }

    @Test(expected = NoProjectIdException::class)
    fun `remove project without id`() {
        val project = Project(null, "Project name")

        removeProject(project)
    }

    @Test
    fun `remove project with project`() {
        repository.add(NewProject("Project name"))
        val project = Project(1L, "Project name")

        removeProject(project)

        val actual = repository.findAll()
        assertEquals(emptyList<Project>(), actual)
    }

    @Test
    fun `remove project with projects`() {
        repository.add(NewProject("Project #1"))
        repository.add(NewProject("Project #2"))
        val expected = listOf(Project(2L, "Project #2"))
        val project = Project(1L, "Project #1")

        removeProject(project)

        val actual = repository.findAll()
        assertEquals(expected, actual)
    }
}
