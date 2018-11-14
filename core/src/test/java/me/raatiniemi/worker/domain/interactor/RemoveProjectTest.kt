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
    private lateinit var useCase: RemoveProject

    @Before
    fun setUp() {
        useCase = RemoveProject(repository)
    }

    @Test
    fun execute_withProject() {
        repository.add(Project.from("Project name"))
        val project = Project.from(1L, "Project name")

        useCase.execute(project)

        val actual = repository.findAll()
        assertEquals(emptyList<Project>(), actual)
    }

    @Test
    fun execute_withProjects() {
        repository.add(Project.from("Project #1"))
        repository.add(Project.from("Project #2"))
        val expected = listOf(Project.from(2L, "Project #2"))
        val project = Project.from(1L, "Project #1")

        useCase.execute(project)

        val actual = repository.findAll()
        assertEquals(expected, actual)
    }
}
