/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.project.repository

import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.model.LoadPosition
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.LoadSize
import me.raatiniemi.worker.domain.project.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.*

@Suppress("FunctionName")
abstract class ProjectRepositoryTest {
    protected abstract val projects: ProjectRepository

    // Count

    @Test
    fun count_withoutProjects() {
        val expected = 0

        val actual = runBlocking {
            projects.count()
        }

        assertEquals(expected, actual)
    }

    @Test
    fun count_withProject() {
        runBlocking {
            projects.add(NewProject(android.name))
        }
        val expected = 1

        val actual = runBlocking {
            projects.count()
        }

        assertEquals(expected, actual)
    }

    @Test
    fun count_withProjects() {
        runBlocking {
            projects.add(NewProject(android.name))
            projects.add(NewProject(cli.name))
        }
        val expected = 2

        val actual = runBlocking {
            projects.count()
        }

        assertEquals(expected, actual)
    }

    // Find all (paging)

    @Test
    fun findAll_pagingWithoutProjects() {
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )
        val expected = emptyList<Project>()

        val actual = runBlocking {
            projects.findAll(loadRange)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithProject() {
        runBlocking {
            projects.add(NewProject(android.name))
        }
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )
        val expected = listOf(
            android
        )

        val actual = runBlocking {
            projects.findAll(loadRange)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithProjects() {
        runBlocking {
            projects.add(NewProject(android.name))
            projects.add(NewProject(cli.name))
        }
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )
        val expected = listOf(
            android,
            cli
        )

        val actual = runBlocking {
            projects.findAll(loadRange)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithProjectBeforePage() {
        runBlocking {
            projects.add(NewProject(android.name))
            projects.add(NewProject(cli.name))
        }
        val loadRange = LoadRange(
            LoadPosition(1),
            LoadSize(10)
        )
        val expected = listOf(
            cli
        )

        val actual = runBlocking {
            projects.findAll(loadRange)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithProjectAfterPage() {
        runBlocking {
            projects.add(NewProject(android.name))
            projects.add(NewProject(cli.name))
        }
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(1)
        )
        val expected = listOf(
            android
        )

        val actual = runBlocking {
            projects.findAll(loadRange)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingSortedPage() {
        runBlocking {
            projects.add(NewProject(cli.name))
            projects.add(NewProject(android.name))
        }
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )
        val expected = listOf(
            Project(ProjectId(2), android.name),
            Project(ProjectId(1), cli.name)
        )

        val actual = runBlocking {
            projects.findAll(loadRange)
        }

        assertEquals(expected, actual)
    }

    // Find all

    @Test
    fun findAll_withoutProjects() {
        val expected = emptyList<Project>()

        val actual = runBlocking {
            projects.findAll()
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withProject() {
        runBlocking {
            projects.add(NewProject(android.name))
        }
        val expected = listOf(
            android
        )

        val actual = runBlocking {
            projects.findAll()
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withProjects() {
        runBlocking {
            projects.add(NewProject(android.name))
            projects.add(NewProject(cli.name))
        }
        val expected = listOf(
            android,
            cli
        )

        val actual = runBlocking {
            projects.findAll()
        }

        assertEquals(expected, actual)
    }

    // Find by name

    @Test
    fun findByName_withoutProject() {
        val actual = runBlocking {
            projects.findByName(android.name)
        }

        assertNull(actual)
    }

    @Test
    fun findByName_withoutMatchingProject() {
        runBlocking {
            projects.add(NewProject(android.name))
        }

        val actual = runBlocking {
            projects.findByName(cli.name)
        }

        assertNull(actual)
    }

    @Test
    fun findByName_withLowercaseProjectName() {
        runBlocking {
            projects.add(NewProject(android.name))
        }
        val name = android.name.value.toLowerCase(Locale.getDefault())

        val actual = runBlocking {
            projects.findByName(
                android.name.copy(value = name)
            )
        }

        assertEquals(android, actual)
    }

    @Test
    fun findByName_withProject() {
        runBlocking {
            projects.add(NewProject(android.name))
        }

        val actual = runBlocking {
            projects.findByName(android.name)
        }

        assertEquals(android, actual)
    }

    // Find by id

    @Test
    fun findById_withoutProjects() {
        val actual = runBlocking {
            projects.findById(android.id)
        }

        assertNull(actual)
    }

    @Test
    fun findById_withoutMatchingProject() {
        runBlocking {
            projects.add(NewProject(android.name))
        }

        val actual = runBlocking {
            projects.findById(cli.id)
        }

        assertNull(actual)
    }

    @Test
    fun findById_withProject() {
        runBlocking {
            projects.add(NewProject(android.name))
        }

        val actual = runBlocking {
            projects.findById(android.id)
        }

        assertEquals(android, actual)
    }

    @Test
    fun findById_withProjects() {
        runBlocking {
            projects.add(NewProject(android.name))
            projects.add(NewProject(cli.name))
        }

        val actual = runBlocking {
            projects.findById(cli.id)
        }

        assertEquals(cli, actual)
    }

    // Remove

    @Test
    fun remove_withoutProjects() {
        runBlocking {
            projects.remove(android)
        }
    }

    @Test
    fun remove_withoutMatchingProject() {
        runBlocking {
            projects.add(NewProject(android.name))
        }
        val expected = listOf(
            android
        )

        val actual = runBlocking {
            projects.remove(cli)

            projects.findAll()
        }

        assertEquals(expected, actual)
    }

    @Test
    fun remove_withProject() {
        runBlocking {
            projects.add(NewProject(android.name))
        }
        val expected = emptyList<Project>()

        val actual = runBlocking {
            projects.remove(android)

            projects.findAll()
        }

        assertEquals(expected, actual)
    }

    @Test
    fun remove_withProjects() {
        runBlocking {
            projects.add(NewProject(android.name))
            projects.add(NewProject(cli.name))
        }
        val expected = listOf(
            cli
        )

        val actual = runBlocking {
            projects.remove(android)

            projects.findAll()
        }

        assertEquals(expected, actual)
    }
}
