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

package me.raatiniemi.worker.domain.repository

import me.raatiniemi.worker.domain.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectInMemoryRepositoryTest {
    private lateinit var repository: ProjectRepository

    @Before
    fun setUp() {
        repository = ProjectInMemoryRepository()
    }

    @Test
    fun `count without projects`() {
        val expected = 0

        val actual = repository.count()

        assertEquals(expected, actual)
    }

    @Test
    fun `count with project`() {
        repository.add(NewProject(ProjectName("Project name #1")))
        val expected = 1

        val actual = repository.count()

        assertEquals(expected, actual)
    }

    @Test
    fun `count with projects`() {
        repository.add(NewProject(ProjectName("Project name #1")))
        repository.add(NewProject(ProjectName("Project name #2")))
        val expected = 2

        val actual = repository.count()

        assertEquals(expected, actual)
    }

    @Test
    fun `find all paging without projects`() {
        val expected = emptyList<Project>()
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all paging with project`() {
        repository.add(NewProject(ProjectName("Project name #1")))
        val expected = listOf(
            Project(1, ProjectName("Project name #1"))
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all paging with projects`() {
        repository.add(NewProject(ProjectName("Project name #1")))
        repository.add(NewProject(ProjectName("Project name #2")))
        val expected = listOf(
            Project(1, ProjectName("Project name #1")),
            Project(2, ProjectName("Project name #2"))
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with project before page`() {
        repository.add(NewProject(ProjectName("Project name #1")))
        repository.add(NewProject(ProjectName("Project name #2")))
        val expected = listOf(
            Project(2, ProjectName("Project name #2"))
        )
        val loadRange = LoadRange(
            LoadPosition(1),
            LoadSize(10)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with project after page`() {
        repository.add(NewProject(ProjectName("Project name #1")))
        repository.add(NewProject(ProjectName("Project name #2")))
        val expected = listOf(
            Project(1, ProjectName("Project name #1"))
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(1)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all sorted page`() {
        repository.add(NewProject(ProjectName("Project name #2")))
        repository.add(NewProject(ProjectName("Project name #1")))
        val expected = listOf(
            Project(2, ProjectName("Project name #1")),
            Project(1, ProjectName("Project name #2"))
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without projects`() {
        val expected = emptyList<Project>()

        val actual = repository.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with project`() {
        repository.add(NewProject(ProjectName("Project #1")))
        val expected = listOf(
            Project(1, ProjectName("Project #1"))
        )

        val actual = repository.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with projects`() {
        repository.add(NewProject(ProjectName("Project #1")))
        repository.add(NewProject(ProjectName("Project #2")))
        val expected = listOf(
            Project(1, ProjectName("Project #1")),
            Project(2, ProjectName("Project #2"))
        )

        val actual = repository.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun `find by name without project`() {
        val actual = repository.findByName("Project #1")

        assertNull(actual)
    }

    @Test
    fun `find by name without matching project`() {
        repository.add(NewProject(ProjectName("Project #1")))

        val actual = repository.findByName("Project #2")

        assertNull(actual)
    }

    @Test
    fun `find by name with project`() {
        repository.add(NewProject(ProjectName("Project #1")))
        val expected = Project(1, ProjectName("Project #1"))

        val actual = repository.findByName("Project #1")

        assertEquals(expected, actual)
    }

    @Test
    fun `find by name with lowercase project name`() {
        repository.add(NewProject(ProjectName("Project #1")))
        val expected = Project(1, ProjectName("Project #1"))

        val actual = repository.findByName("project #1")

        assertEquals(expected, actual)
    }

    @Test
    fun `find by id without project`() {
        val actual = repository.findById(1)

        assertNull(actual)
    }

    @Test
    fun `find by id without matching project`() {
        repository.add(NewProject(ProjectName("Project #1")))

        val actual = repository.findById(2)

        assertNull(actual)
    }

    @Test
    fun `find by id with project`() {
        repository.add(NewProject(ProjectName("Project #1")))
        val expected = Project(1, ProjectName("Project #1"))

        val actual = repository.findById(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `remove without project`() {
        repository.remove(1)
    }

    @Test
    fun `remove without matching project`() {
        repository.add(NewProject(ProjectName("Project #1")))
        val expected = listOf(
            Project(1, ProjectName("Project #1"))
        )

        repository.remove(2)

        val actual = repository.findAll()
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with project`() {
        repository.add(NewProject(ProjectName("Project #1")))
        val expected = emptyList<Project>()

        repository.remove(1)

        val actual = repository.findAll()
        assertEquals(expected, actual)
    }
}
