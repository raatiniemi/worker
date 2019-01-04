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
import me.raatiniemi.worker.data.projects.TimeIntervalDao
import me.raatiniemi.worker.data.projects.TimeIntervalEntity
import me.raatiniemi.worker.data.projects.projectEntity
import me.raatiniemi.worker.data.projects.timeIntervalEntity
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimeIntervalRoomRepositoryTest {
    private val project = Project(1, "Name")

    private lateinit var database: Database
    private lateinit var timeIntervals: TimeIntervalDao
    private lateinit var repository: TimeIntervalRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, Database::class.java)
                .allowMainThreadQueries()
                .build()

        database.projects()
                .add(
                        projectEntity {
                            id = project.id!!
                            name = project.name
                        }
                )
        timeIntervals = database.timeIntervals()
        repository = TimeIntervalRoomRepository(timeIntervals)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun findAll_withoutTimeInterval() {
        val actual = repository.findAll(project, 0)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun findAll_withoutTimeIntervalForProject() {
        timeIntervals.add(timeIntervalEntity())
        val project = Project(2, "Name #2")

        val actual = repository.findAll(project, 0)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun findAll_withTimeInterval() {
        val entity = timeIntervalEntity()
        timeIntervals.add(entity)
        val expected = listOf(
                TimeInterval(
                        id = 1,
                        projectId = entity.projectId,
                        startInMilliseconds = entity.startInMilliseconds,
                        stopInMilliseconds = entity.stopInMilliseconds,
                        isRegistered = entity.registered == 1L
                )
        )

        val actual = repository.findAll(project, 0)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalOnStart() {
        val entity = timeIntervalEntity()
        timeIntervals.add(entity)
        val expected = listOf(
                TimeInterval(
                        id = 1,
                        projectId = entity.projectId,
                        startInMilliseconds = entity.startInMilliseconds,
                        stopInMilliseconds = entity.stopInMilliseconds,
                        isRegistered = entity.registered == 1L
                )
        )

        val actual = repository.findAll(project, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalBeforeStart() {
        timeIntervals.add(timeIntervalEntity())

        val actual = repository.findAll(project, 2)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun findAll_withActiveTimeIntervalBeforeStart() {
        val entity = timeIntervalEntity { stopInMilliseconds = 0 }
        timeIntervals.add(entity)
        val expected = listOf(
                TimeInterval(
                        id = 1,
                        projectId = entity.projectId,
                        startInMilliseconds = entity.startInMilliseconds,
                        stopInMilliseconds = entity.stopInMilliseconds,
                        isRegistered = entity.registered == 1L
                )
        )

        val actual = repository.findAll(project, 2)

        assertEquals(expected, actual)
    }

    @Test
    fun findById_withoutTimeInterval() {
        val actual = repository.findById(1)

        assertFalse(actual.isPresent)
    }

    @Test
    fun findById_withTimeInterval() {
        val entity = timeIntervalEntity()
        timeIntervals.add(entity)
        val expected = TimeInterval(
                id = 1,
                projectId = entity.projectId,
                startInMilliseconds = entity.startInMilliseconds,
                stopInMilliseconds = entity.stopInMilliseconds,
                isRegistered = entity.registered == 1L
        )

        val actual = repository.findById(1)

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun findActiveByProjectId_withoutTimeInterval() {
        val actual = repository.findActiveByProjectId(1)

        assertFalse(actual.isPresent)
    }

    @Test
    fun findActiveByProjectId_withoutActiveTimeInterval() {
        timeIntervals.add(timeIntervalEntity())

        val actual = repository.findActiveByProjectId(1)

        assertFalse(actual.isPresent)
    }

    @Test
    fun getActiveTimeForProject_withTimeInterval() {
        val entity = timeIntervalEntity { stopInMilliseconds = 0 }
        timeIntervals.add(entity)
        val expected = TimeInterval(
                id = 1,
                projectId = entity.projectId,
                startInMilliseconds = entity.startInMilliseconds,
                stopInMilliseconds = entity.stopInMilliseconds,
                isRegistered = entity.registered == 1L
        )

        val actual = repository.findActiveByProjectId(1)

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun add() {
        val timeInterval = TimeInterval(
                projectId = 1,
                startInMilliseconds = 1,
                stopInMilliseconds = 2
        )
        val expected = timeInterval.copy(id = 1)

        val actual = repository.add(timeInterval)

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun update_withoutTimeInterval() {
        val timeInterval = TimeInterval(
                projectId = 1,
                startInMilliseconds = 1,
                stopInMilliseconds = 2
        )

        val actual = repository.update(timeInterval)

        assertFalse(actual.isPresent)
    }

    @Test
    fun update_withTimeInterval() {
        val entity = timeIntervalEntity { registered = true }
        timeIntervals.add(entity)
        val expected = TimeInterval(
                id = 1,
                projectId = entity.projectId,
                startInMilliseconds = entity.startInMilliseconds,
                stopInMilliseconds = entity.stopInMilliseconds,
                isRegistered = entity.registered == 1L
        )

        val actual = repository.update(expected)

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun update_withoutTimeIntervals() {
        val timeInterval = TimeInterval(
                projectId = 1,
                startInMilliseconds = 1,
                stopInMilliseconds = 2
        )

        val actual = repository.update(
                listOf(timeInterval)
        )

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun update_withTimeIntervals() {
        val expected = listOf(
                TimeInterval(
                        id = 1,
                        projectId = 1,
                        startInMilliseconds = 1,
                        stopInMilliseconds = 2
                ),
                TimeInterval(
                        id = 2,
                        projectId = 1,
                        startInMilliseconds = 4,
                        stopInMilliseconds = 6
                )
        )
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(timeIntervalEntity {
            id = 2
            startInMilliseconds = 4
            stopInMilliseconds = 6
        })

        val actual = repository.update(expected)

        assertEquals(expected, actual)
    }

    @Test
    fun remove_withoutTimeInterval() {
        repository.remove(1)

        val actual = timeIntervals.findAll(1, 0)
        assertEquals(emptyList<TimeIntervalEntity>(), actual)
    }

    @Test
    fun remove_withTimeInterval() {
        val entity = timeIntervalEntity {
            id = 2
            startInMilliseconds = 5
            stopInMilliseconds = 9
        }
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(entity)
        val expected = listOf(entity)

        repository.remove(1)

        val actual = timeIntervals.findAll(1, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun remove_withoutTimeIntervals() {
        val timeInterval = TimeInterval(
                id = 1,
                projectId = 1,
                startInMilliseconds = 1,
                stopInMilliseconds = 2
        )

        repository.remove(
                listOf(timeInterval)
        )

        val actual = timeIntervals.findAll(1, 0)
        assertEquals(emptyList<TimeIntervalEntity>(), actual)
    }

    @Test
    fun remove_withTimeIntervals() {
        val entity = timeIntervalEntity {
            id = 2
            startInMilliseconds = 5
            stopInMilliseconds = 9
        }
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(entity)
        val time = TimeInterval(
                id = 1,
                projectId = entity.projectId,
                startInMilliseconds = entity.startInMilliseconds,
                stopInMilliseconds = entity.stopInMilliseconds
        )
        val expected = listOf(entity)

        repository.remove(
                listOf(time)
        )

        val actual = timeIntervals.findAll(1, 0)
        assertEquals(expected, actual)
    }
}