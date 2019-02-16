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

import me.raatiniemi.worker.domain.model.Project
import org.junit.Assert.*
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
        repository.add(Project(null, "Project name #1"))
        val expected = 1

        val actual = repository.count()

        assertEquals(expected, actual)
    }

    @Test
    fun `count with projects`() {
        repository.add(Project(null, "Project name #1"))
        repository.add(Project(null, "Project name #2"))
        val expected = 2

        val actual = repository.count()

        assertEquals(expected, actual)
    }

    @Test
    fun `find all paging without projects`() {
        val expected = emptyList<Project>()

        val actual = repository.findAll(0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all paging with project`() {
        repository.add(Project(null, "Project name #1"))
        val expected = listOf(
                Project(1, "Project name #1")
        )

        val actual = repository.findAll(0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all paging with projects`() {
        repository.add(Project(null, "Project name #1"))
        repository.add(Project(null, "Project name #2"))
        val expected = listOf(
                Project(1, "Project name #1"),
                Project(2, "Project name #2")
        )

        val actual = repository.findAll(0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with project before page`() {
        repository.add(Project(null, "Project name #1"))
        repository.add(Project(null, "Project name #2"))
        val expected = listOf(
                Project(2, "Project name #2")
        )

        val actual = repository.findAll(1, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with project after page`() {
        repository.add(Project(null, "Project name #1"))
        repository.add(Project(null, "Project name #2"))
        val expected = listOf(
                Project(1, "Project name #1")
        )

        val actual = repository.findAll(0, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all sorted page`() {
        repository.add(Project(null, "Project name #2"))
        repository.add(Project(null, "Project name #1"))
        val expected = listOf(
                Project(2, "Project name #1"),
                Project(1, "Project name #2")
        )

        val actual = repository.findAll(0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `findAll withoutProjects`() {
        val actual = repository.findAll()

        assertEquals(emptyList<Project>(), actual)
    }

    @Test
    fun `findAll withProject`() {
        repository.add(Project(null, name = "Project #1"))
        val expected = listOf(
                Project(1, "Project #1")
        )

        val actual = repository.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun `findAll withProjects`() {
        repository.add(Project(null, name = "Project #1"))
        repository.add(Project(null, name = "Project #2"))
        val expected = listOf(
                Project(1, "Project #1"),
                Project(2, "Project #2")
        )

        val actual = repository.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun `findByName withoutProject`() {
        val actual = repository.findByName("Project #1")

        assertFalse(actual.isPresent)
    }

    @Test
    fun `findByName withoutMatchingProject`() {
        repository.add(Project(null, "Project #1"))

        val actual = repository.findByName("Project #2")

        assertFalse(actual.isPresent)
    }

    @Test
    fun `findByName withProject`() {
        repository.add(Project(null, "Project #1"))
        val expected = Project(1, "Project #1")

        val actual = repository.findByName("Project #1")

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun `findByName with lowercase project name`() {
        repository.add(Project(null, "Project #1"))
        val expected = Project(1, "Project #1")

        val actual = repository.findByName("project #1")

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun `findById withoutProject`() {
        val actual = repository.findById(1)

        assertFalse(actual.isPresent)
    }

    @Test
    fun `findById withoutMatchingProject`() {
        repository.add(Project(null, "Project #1"))

        val actual = repository.findById(2)

        assertFalse(actual.isPresent)
    }

    @Test
    fun `findById withProject`() {
        repository.add(Project(null, "Project #1"))
        val expected = Project(1, "Project #1")

        val actual = repository.findById(1)

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun `remove withoutProject`() {
        repository.remove(1)
    }

    @Test
    fun `remove withoutMatchingProject`() {
        repository.add(Project(null, "Project #1"))
        val expected = listOf(
                Project(1, "Project #1")
        )

        repository.remove(2)

        val actual = repository.findAll()
        assertEquals(expected, actual)
    }

    @Test
    fun `remove withProject`() {
        repository.add(Project(null, "Project #1"))

        repository.remove(1)

        val actual = repository.findAll()
        assertEquals(emptyList<Project>(), actual)
    }
}
