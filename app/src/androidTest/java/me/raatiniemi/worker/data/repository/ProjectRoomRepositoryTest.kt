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

package me.raatiniemi.worker.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.data.Database
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.repository.ProjectRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProjectRoomRepositoryTest {
    private lateinit var database: Database
    private lateinit var repository: ProjectRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, Database::class.java)
                .allowMainThreadQueries()
                .build()

        repository = ProjectRoomRepository(database.projects())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun findAll_withoutProjects() {
        val actual = repository.findAll()

        assertTrue(actual.isEmpty())
    }

    @Test
    fun findAll_withProject() {
        repository.add(Project(null, "Name"))
        val expected = listOf(Project(1, "Name"))

        val actual = repository.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withProjects() {
        repository.add(Project(null, "Name #2"))
        repository.add(Project(null, "Name #1"))
        val expected = listOf(
                Project(2, "Name #1"),
                Project(1, "Name #2")
        )

        val actual = repository.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun findByName_withoutProject() {
        val actual = repository.findByName("Name")

        assertFalse(actual.isPresent)
    }

    @Test
    fun findByName_withProject() {
        repository.add(Project(null, "Name"))
        val expected = Project(1, "Name")

        val actual = repository.findByName("Name")

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun findById_withoutProjects() {
        val actual = repository.findById(1)

        assertFalse(actual.isPresent)
    }

    @Test
    fun findById_withProject() {
        repository.add(Project(null, "Name"))
        val expected = Project(1, "Name")

        val actual = repository.findById(1)

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun findById_withProjects() {
        repository.add(Project(null, "Name #2"))
        repository.add(Project(null, "Name #1"))
        val expected = Project(2, "Name #1")

        val actual = repository.findById(2)

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun remove_withoutProjects() {
        repository.remove(1)
    }

    @Test
    fun remove_withProject() {
        repository.add(Project(null, "Name"))

        repository.remove(1)

        val actual = repository.findAll()
        assertTrue(actual.isEmpty())
    }

    @Test
    fun remove_withProjects() {
        repository.add(Project(null, "Name #2"))
        repository.add(Project(null, "Name #1"))
        val expected = listOf(
                Project(2, "Name #1")
        )

        repository.remove(1)

        val actual = repository.findAll()
        assertEquals(expected, actual)
    }
}