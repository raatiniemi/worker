/*
 * Copyright (C) 2017 Worker Project
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

import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.repository.ProjectRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CreateProjectTest {
    private val repository: ProjectRepository = ProjectInMemoryRepository()
    private lateinit var createProject: CreateProject

    @Before
    fun setUp() {
        createProject = CreateProject(repository)
    }

    @Test(expected = ProjectAlreadyExistsException::class)
    fun `execute withExistingProject`() {
        val project = Project.from("Project Name")
        repository.add(project)

        createProject.execute(project)
    }

    @Test
    fun execute() {
        val project = Project.from("Project Name")
        val expected = listOf(Project.from(1L, "Project Name"))

        createProject.execute(project)

        val actual = repository.findAll()
        assertEquals(expected, actual)
    }
}
