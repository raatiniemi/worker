/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.data.room.entity.timeinterval

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.data.room.Database
import me.raatiniemi.worker.domain.project.model.NewProject
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
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
class TimeIntervalDaoTest : AutoCloseKoinTest() {
    private val database by inject<Database>()

    private val timeIntervals: TimeIntervalDao
        get() = database.timeIntervals()

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            loadKoinModules(androidTestKoinModules)
        }

        runBlocking {
            val projects = get<ProjectRepository>()
            projects.add(NewProject(android.name))
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun findAll_withoutTimeIntervals() {
        val actual = timeIntervals.findAll(1, 0)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun findAll_withoutTimeIntervalsForProject() = runBlocking {
        timeIntervals.add(timeIntervalEntity())

        val actual = timeIntervals.findAll(2, 0)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun findAll_withTimeIntervals() = runBlocking {
        timeIntervals.add(timeIntervalEntity())
        val expected = listOf(
            timeIntervalEntity { id = 1 }
        )

        val actual = timeIntervals.findAll(1, 0)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalsOnStart() = runBlocking {
        timeIntervals.add(timeIntervalEntity())
        val expected = listOf(
            timeIntervalEntity { id = 1 }
        )

        val actual = timeIntervals.findAll(1, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalsBeforeStart() = runBlocking {
        timeIntervals.add(timeIntervalEntity())

        val actual = timeIntervals.findAll(1, 2)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun findAll_withActiveTimeIntervalsBeforeStart() = runBlocking {
        timeIntervals.add(timeIntervalEntity { stopInMilliseconds = 0 })
        val expected = listOf(
            timeIntervalEntity {
                id = 1
                stopInMilliseconds = 0
            }
        )

        val actual = timeIntervals.findAll(1, 2)

        assertEquals(expected, actual)
    }

    @Test
    fun find_withoutTimeInterval() {
        val actual = timeIntervals.find(1)

        assertNull(actual)
    }

    @Test
    fun find_withTimeInterval() = runBlocking {
        timeIntervals.add(timeIntervalEntity())
        val expected = timeIntervalEntity { id = 1 }

        val actual = timeIntervals.find(1)

        assertEquals(expected, actual)
    }

    @Test
    fun findActiveTime_withoutTimeInterval() = runBlocking {
        val actual = timeIntervals.findActiveTime(1)

        assertNull(actual)
    }

    @Test
    fun findActiveTime_withoutActiveTimeInterval() = runBlocking {
        timeIntervals.add(timeIntervalEntity())

        val actual = timeIntervals.findActiveTime(1)

        assertNull(actual)
    }

    @Test
    fun findActiveTime_withActiveTimeInterval() = runBlocking {
        timeIntervals.add(timeIntervalEntity { stopInMilliseconds = 0 })
        val expected = timeIntervalEntity {
            id = 1
            stopInMilliseconds = 0
        }

        val actual = timeIntervals.findActiveTime(1)

        assertEquals(expected, actual)
    }

    @Test
    fun update_withoutExistingTimeInterval() = runBlocking {
        timeIntervals.update(
            listOf(
                timeIntervalEntity { id = 1 }
            )
        )

        val actual = timeIntervals.findAll(1, 0)
        assertEquals(emptyList<TimeIntervalEntity>(), actual)
    }

    @Test
    fun update_withTimeIntervals() = runBlocking {
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = 4
            stopInMilliseconds = 0
        })
        val expected = listOf(
            timeIntervalEntity { id = 1 },
            timeIntervalEntity {
                id = 2
                startInMilliseconds = 4
                stopInMilliseconds = 6
            }
        )

        timeIntervals.update(
            listOf(
                timeIntervalEntity {
                    id = 2
                    startInMilliseconds = 4
                    stopInMilliseconds = 6
                }
            )
        )

        val actual = timeIntervals.findAll(1, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun remove_withoutExistingTimeInterval() {
        timeIntervals.remove(
            listOf(
                timeIntervalEntity { id = 1 }
            )
        )

        val actual = timeIntervals.findAll(1, 0)
        assertEquals(emptyList<TimeIntervalEntity>(), actual)
    }

    @Test
    fun remove_withTimeIntervals() = runBlocking {
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(timeIntervalEntity())
        val expected = listOf(
            timeIntervalEntity { id = 2 }
        )

        timeIntervals.remove(
            listOf(
                timeIntervalEntity { id = 1 }
            )
        )

        val actual = timeIntervals.findAll(1, 0)
        assertEquals(expected, actual)
    }
}
