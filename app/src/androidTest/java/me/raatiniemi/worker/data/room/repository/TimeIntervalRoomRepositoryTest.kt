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
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.data.room.Database
import me.raatiniemi.worker.domain.project.model.NewProject
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.cli
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.hours
import me.raatiniemi.worker.domain.time.minutes
import me.raatiniemi.worker.domain.time.setToStartOfDay
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.newTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
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

    private val clockIn by inject<ClockIn>()
    private val clockOut by inject<ClockOut>()

    private val timeIntervals by inject<TimeIntervalRepository>()

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

    // Find all

    @Test
    fun findAll_withoutTimeIntervals() {
        val expected = emptyList<TimeInterval>()

        val actual = timeIntervals.findAll(android, Milliseconds.empty)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withoutTimeIntervalForProject() = runBlocking {
        clockIn(android, Milliseconds.now)
        val expected = emptyList<TimeInterval>()

        val actual = timeIntervals.findAll(cli, Milliseconds.empty)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeInterval() = runBlocking {
        val timeInterval = clockIn(android, Milliseconds.now)
        val expected = listOf(
            timeInterval
        )

        val actual = timeIntervals.findAll(android, Milliseconds.empty)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalOnStartingPoint() = runBlocking {
        val now = Milliseconds.now
        clockIn(android, now)
        val timeInterval = clockOut(android, now + 10.minutes)
        val expected = listOf(
            timeInterval
        )

        val actual = timeIntervals.findAll(android, now)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalBeforeStartingPoint() = runBlocking {
        val now = Milliseconds.now
        clockIn(android, now)
        clockOut(android, now + 10.minutes)
        val expected = emptyList<TimeInterval>()

        val actual = timeIntervals.findAll(android, now + 10.minutes)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withActiveTimeIntervalBeforeStartingPoint() = runBlocking {
        val now = Milliseconds.now
        val timeInterval = clockIn(android, now)
        val expected = listOf(
            timeInterval
        )

        val actual = timeIntervals.findAll(android, now + 10.minutes)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalAfterStartingPoint() = runBlocking {
        val now = Milliseconds.now
        clockIn(android, now + 10.minutes)
        val timeInterval = clockOut(android, now + 20.minutes)
        val expected = listOf(
            timeInterval
        )

        val actual = timeIntervals.findAll(android, now)

        assertEquals(expected, actual)
    }

    // Find by id

    @Test
    fun findById_withoutTimeInterval() {
        val actual = timeIntervals.findById(TimeIntervalId(1))

        assertNull(actual)
    }

    @Test
    fun findById_withTimeInterval() = runBlocking {
        val now = Milliseconds.now
        val expected = clockIn(android, now)

        val actual = timeIntervals.findById(expected.id)

        assertEquals(expected, actual)
    }

    // Find active by project id

    @Test
    fun findActiveByProjectId_withoutTimeIntervals() {
        val actual = timeIntervals.findActiveByProjectId(android.id)

        assertNull(actual)
    }

    @Test
    fun findActiveByProjectId_withoutActiveTimeInterval() = runBlocking {
        val now = Milliseconds.now
        clockIn(android, now)
        clockOut(android, now + 10.minutes)

        val actual = timeIntervals.findActiveByProjectId(android.id)

        assertNull(actual)
    }

    @Test
    fun findActiveByProjectId_withTimeInterval() = runBlocking {
        val now = Milliseconds.now
        val expected = clockIn(android, now)

        val actual = timeIntervals.findActiveByProjectId(android.id)

        assertEquals(expected, actual)
    }

    // Add

    @Test
    fun add() = runBlocking {
        val now = Milliseconds.now
        val newTimeInterval = newTimeInterval(android) {
            start = now
        }
        val expected = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = now
            builder.stop = null
        }

        val actual = timeIntervals.add(newTimeInterval)

        assertEquals(expected, actual)
    }

    // Update

    @Test
    fun update_withoutTimeInterval() {
        val now = Milliseconds.now
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = now
            builder.stop = now + 10.minutes
        }

        val actual = timeIntervals.update(timeInterval)

        assertNull(actual)
    }

    @Test
    fun update_withTimeInterval() = runBlocking {
        val now = Milliseconds.now
        val timeInterval = clockIn(android, now)
        val expected = timeInterval(timeInterval) { builder ->
            builder.stop = now + 10.minutes
        }

        val actual = timeIntervals.update(expected)

        assertEquals(expected, actual)
    }

    @Test
    fun update_withoutTimeIntervals() {
        val now = Milliseconds.now
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = now
            builder.stop = now + 10.minutes
        }
        val expected = emptyList<TimeInterval>()

        val actual = timeIntervals.update(
            listOf(
                timeInterval
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun update_withTimeIntervals() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        val nightTimeInterval = clockOut(android, startOfDay + 4.hours)
        clockIn(android, startOfDay + 5.hours)
        val morningTimeInterval = clockOut(android, startOfDay + 9.hours)
        val expected = listOf(
            timeInterval(nightTimeInterval) { builder ->
                builder.isRegistered = true
            },
            timeInterval(morningTimeInterval) { builder ->
                builder.isRegistered = true
            }
        )

        val actual = timeIntervals.update(expected)

        assertEquals(expected, actual)
    }

    // Remove

    @Test
    fun remove_withoutTimeInterval() {
        val expected = emptyList<TimeInterval>()

        timeIntervals.remove(TimeIntervalId(1))

        val actual = timeIntervals.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun remove_withTimeInterval() = runBlocking {
        val timeInterval = clockIn(android, Milliseconds.now)
        val expected = emptyList<TimeInterval>()

        timeIntervals.remove(timeInterval.id)

        val actual = timeIntervals.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun remove_withoutTimeIntervals() {
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(2)
        }
        val expected = emptyList<TimeInterval>()

        timeIntervals.remove(
            listOf(
                timeInterval
            )
        )

        val actual = timeIntervals.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun remove_withTimeIntervals() = runBlocking {
        val timeInterval = clockIn(android, Milliseconds.now)
        val expected = emptyList<TimeInterval>()

        timeIntervals.remove(
            listOf(
                timeInterval
            )
        )

        val actual = timeIntervals.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }
}
