/*
 * Copyright (C) 2022 Tobias Raatiniemi
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
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.repository.ProjectInMemoryRepository
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetProjectTest {
    private val projects: ProjectRepository = ProjectInMemoryRepository()

    private lateinit var getProject: GetProject

    @Before
    fun setUp() {
        getProject = GetProject(projects)
    }

    @Test
    fun execute() = runBlocking {
        projects.add(NewProject(android.name))

        val actual = getProject(android.id.value)

        assertEquals(android, actual)
    }

    @Test(expected = NoProjectException::class)
    fun `execute withoutProject`() = runBlocking<Unit> {
        getProject(android.id.value)
    }
}
