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

package me.raatiniemi.worker.data.room.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.data.room.Database
import me.raatiniemi.worker.data.projects.TimeIntervalDao
import me.raatiniemi.worker.data.projects.TimeIntervalEntity
import me.raatiniemi.worker.data.projects.timeIntervalEntity
import me.raatiniemi.worker.domain.project.model.NewProject
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.ios
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.newTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.koin.androidTestKoinModules
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class TimeIntervalRoomRepositoryTest : AutoCloseKoinTest() {
    private val database by inject<Database>()
    private val timeIntervals: TimeIntervalDao
        get() = database.timeIntervals()

    private val repository by inject<TimeIntervalRepository>()

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            loadKoinModules(androidTestKoinModules)
        }

        val projects = get<ProjectRepository>()
        projects.add(NewProject(android.name))
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun findAll_withoutTimeInterval() {
        val actual = repository.findAll(android, Milliseconds.empty)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun findAll_withoutTimeIntervalForProject() {
        timeIntervals.add(timeIntervalEntity())

        val actual = repository.findAll(ios, Milliseconds.empty)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun findAll_withTimeInterval() {
        val entity = timeIntervalEntity {
            projectId = android.id.value
        }
        timeIntervals.add(entity)
        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(entity.startInMilliseconds)
                builder.stop = Milliseconds(entity.stopInMilliseconds)
                builder.isRegistered = entity.registered == 1L
            }
        )

        val actual = repository.findAll(android, Milliseconds(0))

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalOnStart() {
        val entity = timeIntervalEntity {
            projectId = android.id.value
        }
        timeIntervals.add(entity)
        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(entity.startInMilliseconds)
                builder.stop = Milliseconds(entity.stopInMilliseconds)
                builder.isRegistered = entity.registered == 1L
            }
        )

        val actual = repository.findAll(android, Milliseconds(1))

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalBeforeStart() {
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )

        val actual = repository.findAll(android, Milliseconds(2))

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun findAll_withActiveTimeIntervalBeforeStart() {
        val entity = timeIntervalEntity {
            projectId = android.id.value
            stopInMilliseconds = 0
        }
        timeIntervals.add(entity)
        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(entity.startInMilliseconds)
                builder.stop = null
                builder.isRegistered = entity.registered == 1L
            }
        )

        val actual = repository.findAll(android, Milliseconds(2))

        assertEquals(expected, actual)
    }

    @Test
    fun findById_withoutTimeInterval() {
        val actual = repository.findById(TimeIntervalId(1))

        assertNull(actual)
    }

    @Test
    fun findById_withTimeInterval() {
        val entity = timeIntervalEntity()
        timeIntervals.add(entity)
        val expected = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(entity.startInMilliseconds)
            builder.stop = Milliseconds(entity.stopInMilliseconds)
            builder.isRegistered = entity.registered == 1L
        }

        val actual = repository.findById(TimeIntervalId(1))

        assertEquals(expected, actual)
    }

    @Test
    fun findActiveByProjectId_withoutTimeInterval() {
        val actual = repository.findActiveByProjectId(android.id)

        assertNull(actual)
    }

    @Test
    fun findActiveByProjectId_withoutActiveTimeInterval() {
        timeIntervals.add(timeIntervalEntity())

        val actual = repository.findActiveByProjectId(android.id)

        assertNull(actual)
    }

    @Test
    fun getActiveTimeForProject_withTimeInterval() {
        val entity = timeIntervalEntity { stopInMilliseconds = 0 }
        timeIntervals.add(entity)
        val expected = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(entity.startInMilliseconds)
            builder.stop = null
            builder.isRegistered = entity.registered == 1L
        }

        val actual = repository.findActiveByProjectId(android.id)

        assertEquals(expected, actual)
    }

    @Test
    fun add() {
        val newTimeInterval = newTimeInterval(android) {
            start = Milliseconds(1)
        }
        val expected = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = null
        }

        val actual = repository.add(newTimeInterval)

        assertEquals(expected, actual)
    }

    @Test
    fun update_withoutTimeInterval() {
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(2)
        }

        val actual = repository.update(timeInterval)

        assertNull(actual)
    }

    @Test
    fun update_withTimeInterval() {
        val entity = timeIntervalEntity {
            projectId = android.id.value
            registered = true
        }
        timeIntervals.add(entity)
        val expected = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(entity.startInMilliseconds)
            builder.stop = Milliseconds(entity.stopInMilliseconds)
            builder.isRegistered = entity.registered == 1L
        }

        val actual = repository.update(expected)

        assertEquals(expected, actual)
    }

    @Test
    fun update_withoutTimeIntervals() {
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(2)
        }

        val actual = repository.update(
            listOf(timeInterval)
        )

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun update_withTimeIntervals() {
        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = Milliseconds(4)
                builder.stop = Milliseconds(6)
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                id = 2
                projectId = android.id.value
                startInMilliseconds = 4
                stopInMilliseconds = 6
            }
        )

        val actual = repository.update(expected)

        assertEquals(expected, actual)
    }

    @Test
    fun remove_withoutTimeInterval() {
        val expected = emptyList<TimeIntervalEntity>()

        repository.remove(TimeIntervalId(1))

        val actual = timeIntervals.findAll(1, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun remove_withTimeInterval() {
        val entity = timeIntervalEntity {
            id = 2
            projectId = android.id.value
            startInMilliseconds = 5
            stopInMilliseconds = 9
        }
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )
        timeIntervals.add(entity)
        val expected = listOf(entity)

        repository.remove(TimeIntervalId(1))

        val actual = timeIntervals.findAll(android.id.value, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun remove_withoutTimeIntervals() {
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(2)
        }

        repository.remove(
            listOf(timeInterval)
        )

        val actual = timeIntervals.findAll(android.id.value, 0)
        assertEquals(emptyList<TimeIntervalEntity>(), actual)
    }

    @Test
    fun remove_withTimeIntervals() {
        val entity = timeIntervalEntity {
            id = 2
            projectId = android.id.value
            startInMilliseconds = 5
            stopInMilliseconds = 9
        }
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )
        timeIntervals.add(entity)
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(entity.startInMilliseconds)
            builder.stop = Milliseconds(entity.stopInMilliseconds)
        }
        val expected = listOf(entity)

        repository.remove(
            listOf(timeInterval)
        )

        val actual = timeIntervals.findAll(android.id.value, 0)
        assertEquals(expected, actual)
    }
}
