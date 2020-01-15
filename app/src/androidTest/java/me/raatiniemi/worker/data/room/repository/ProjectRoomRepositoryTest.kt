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

package me.raatiniemi.worker.data.room.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.data.room.Database
import me.raatiniemi.worker.domain.model.LoadPosition
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.LoadSize
import me.raatiniemi.worker.domain.project.model.*
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import me.raatiniemi.worker.koin.androidTestKoinModules
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class ProjectRoomRepositoryTest : AutoCloseKoinTest() {
    private val database by inject<Database>()

    private val repository by inject<ProjectRepository>()

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            loadKoinModules(androidTestKoinModules)
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun count_withoutProjects() = runBlocking {
        val expected = 0

        val actual = repository.count()

        assertEquals(expected, actual)
    }

    @Test
    fun count_withProject() = runBlocking {
        repository.add(NewProject(android.name))
        val expected = 1

        val actual = repository.count()

        assertEquals(expected, actual)
    }

    @Test
    fun count_withProjects() = runBlocking {
        repository.add(NewProject(android.name))
        repository.add(NewProject(cli.name))
        val expected = 2

        val actual = repository.count()

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithoutProjects() = runBlocking {
        val expected = emptyList<Project>()
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithProject() = runBlocking {
        repository.add(NewProject(android.name))
        val expected = listOf(android)
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithProjects() = runBlocking {
        repository.add(NewProject(android.name))
        repository.add(NewProject(cli.name))
        val expected = listOf(android, cli)
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withProjectBeforePage() = runBlocking {
        repository.add(NewProject(android.name))
        repository.add(NewProject(cli.name))
        val expected = listOf(cli)
        val loadRange = LoadRange(
            LoadPosition(1),
            LoadSize(10)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withProjectAfterPage() = runBlocking {
        repository.add(NewProject(android.name))
        repository.add(NewProject(cli.name))
        val expected = listOf(android)
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(1)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_sortedPage() = runBlocking {
        repository.add(NewProject(cli.name))
        repository.add(NewProject(android.name))
        val expected = listOf(
            Project(ProjectId(2), android.name),
            Project(ProjectId(1), cli.name)
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withoutProjects() = runBlocking {
        val actual = repository.findAll()

        assertTrue(actual.isEmpty())
    }

    @Test
    fun findAll_withProject() = runBlocking {
        repository.add(NewProject(android.name))
        val expected = listOf(android)

        val actual = repository.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withProjects() = runBlocking {
        repository.add(NewProject(cli.name))
        repository.add(NewProject(android.name))
        val expected = listOf(
            Project(ProjectId(2), android.name),
            Project(ProjectId(1), cli.name)
        )

        val actual = repository.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun findByName_withoutProject() = runBlocking {
        val actual = repository.findByName(android.name)

        assertNull(actual)
    }

    @Test
    fun findByName_withProject() = runBlocking {
        repository.add(NewProject(android.name))

        val actual = repository.findByName(android.name)

        assertEquals(android, actual)
    }

    @Test
    fun findById_withoutProjects() = runBlocking {
        val actual = repository.findById(android.id)

        assertNull(actual)
    }

    @Test
    fun findById_withProject() = runBlocking {
        repository.add(NewProject(android.name))

        val actual = repository.findById(android.id)

        assertEquals(android, actual)
    }

    @Test
    fun findById_withProjects() = runBlocking {
        repository.add(NewProject(android.name))
        repository.add(NewProject(cli.name))

        val actual = repository.findById(cli.id)

        assertEquals(cli, actual)
    }

    @Test
    fun remove_withoutProjects() = runBlocking {
        repository.remove(android)
    }

    @Test
    fun remove_withProject() = runBlocking {
        repository.add(NewProject(android.name))
        val expected = emptyList<Project>()

        repository.remove(android)

        val actual = repository.findAll()
        assertEquals(expected, actual)
    }

    @Test
    fun remove_withProjects() = runBlocking {
        repository.add(NewProject(android.name))
        repository.add(NewProject(cli.name))
        val expected = listOf(cli)

        repository.remove(android)

        val actual = repository.findAll()
        assertEquals(expected, actual)
    }
}