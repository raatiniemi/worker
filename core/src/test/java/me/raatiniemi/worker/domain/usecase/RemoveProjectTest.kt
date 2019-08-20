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

package me.raatiniemi.worker.domain.usecase

import me.raatiniemi.worker.domain.project.model.NewProject
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.cli
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

    @Test
    fun `remove project with project`() {
        repository.add(NewProject(android.name))

        removeProject(android)

        val actual = repository.findAll()
        assertEquals(emptyList<Project>(), actual)
    }

    @Test
    fun `remove project with projects`() {
        repository.add(NewProject(android.name))
        repository.add(NewProject(cli.name))
        val expected = listOf(cli)

        removeProject(android)

        val actual = repository.findAll()
        assertEquals(expected, actual)
    }
}
