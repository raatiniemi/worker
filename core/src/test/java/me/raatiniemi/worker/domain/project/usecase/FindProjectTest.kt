/*
 * Copyright (C) 2020 Tobias Raatiniemi
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
import me.raatiniemi.worker.domain.project.model.ProjectName
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
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
    fun `find project without projects`() = runBlocking {
        val actual = findProject(android.name)

        assertNull(actual)
    }

    @Test
    fun `find project with project`() = runBlocking {
        repository.add(NewProject(android.name))

        val actual = findProject(android.name)

        assertEquals(android, actual)
    }

    @Test
    fun `find project with lowercase project name`() = runBlocking {
        repository.add(NewProject(android.name))

        val actual = findProject(
            android.name.value.toLowerCase()
                .let { ProjectName(it) }
        )

        assertEquals(android, actual)
    }
}
