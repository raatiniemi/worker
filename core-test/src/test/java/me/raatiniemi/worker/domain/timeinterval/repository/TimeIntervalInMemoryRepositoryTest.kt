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

package me.raatiniemi.worker.domain.timeinterval.repository

import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.cli
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.hours
import me.raatiniemi.worker.domain.time.minutes
import me.raatiniemi.worker.domain.time.setToStartOfDay
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.newTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeIntervalInMemoryRepositoryTest {
    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut

    private lateinit var timeIntervals: TimeIntervalRepository

    @Before
    fun setUp() {
        timeIntervals = TimeIntervalInMemoryRepository()

        clockIn = ClockIn(timeIntervals)
        clockOut = ClockOut(timeIntervals)
    }

    // Find all

    @Test
    fun `find all without time intervals`() {
        val expected = emptyList<TimeInterval>()

        val actual = timeIntervals.findAll(android, Milliseconds.empty)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without time interval for project`() = runBlocking {
        clockIn(android, Milliseconds.now)
        val expected = emptyList<TimeInterval>()

        val actual = timeIntervals.findAll(cli, Milliseconds.empty)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time interval`() = runBlocking {
        val timeInterval = clockIn(android, Milliseconds.now)
        val expected = listOf(
            timeInterval
        )

        val actual = timeIntervals.findAll(android, Milliseconds.empty)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time interval on starting point`() = runBlocking {
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
    fun `find all with time interval before starting point`() = runBlocking {
        val now = Milliseconds.now
        clockIn(android, now)
        clockOut(android, now + 10.minutes)
        val expected = emptyList<TimeInterval>()

        val actual = timeIntervals.findAll(android, now + 10.minutes)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with active time interval before starting point`() = runBlocking {
        val now = Milliseconds.now
        val timeInterval = clockIn(android, now)
        val expected = listOf(
            timeInterval
        )

        val actual = timeIntervals.findAll(android, now + 10.minutes)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time interval after starting point`() = runBlocking {
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
    fun `find by id without time interval`() = runBlocking {
        val actual = timeIntervals.findById(TimeIntervalId(1))

        assertNull(actual)
    }

    @Test
    fun `find by id with time interval`() = runBlocking {
        val now = Milliseconds.now
        val expected = clockIn(android, now)

        val actual = timeIntervals.findById(expected.id)

        assertEquals(expected, actual)
    }

    // Find active by project id

    @Test
    fun `find active by project id without time intervals`() = runBlocking {
        val actual = timeIntervals.findActiveByProjectId(android.id)

        assertNull(actual)
    }

    @Test
    fun `find active by project id without active time interval`() = runBlocking {
        val now = Milliseconds.now
        clockIn(android, now)
        clockOut(android, now + 10.minutes)

        val actual = timeIntervals.findActiveByProjectId(android.id)

        assertNull(actual)
    }

    @Test
    fun `find active by project id with time interval`() = runBlocking {
        val now = Milliseconds.now
        val expected = clockIn(android, now)

        val actual = timeIntervals.findActiveByProjectId(android.id)

        assertEquals(expected, actual)
    }

    // Add

    @Test
    fun add() = runBlocking {
        val newTimeInterval = newTimeInterval(android) {
            start = Milliseconds(1)
        }
        val expected = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = null
        }

        val actual = timeIntervals.add(newTimeInterval)

        assertEquals(expected, actual)
    }

    // Update

    @Test
    fun `update without time interval`() = runBlocking {
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
    fun `update with time interval`() = runBlocking {
        val now = Milliseconds.now
        val timeInterval = clockIn(android, now)
        val expected = timeInterval(timeInterval) { builder ->
            builder.stop = now + 10.minutes
        }

        val actual = timeIntervals.update(expected)

        assertEquals(expected, actual)
    }

    @Test
    fun `update without time intervals`() = runBlocking {
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
    fun `update with time intervals`() = runBlocking {
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
    fun `remove without time interval`() = runBlocking {
        val expected = emptyList<TimeInterval>()

        timeIntervals.remove(TimeIntervalId(1))

        val actual = timeIntervals.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with time interval`() = runBlocking {
        val timeInterval = clockIn(android, Milliseconds.now)
        val expected = emptyList<TimeInterval>()

        timeIntervals.remove(timeInterval.id)

        val actual = timeIntervals.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove without time intervals`() = runBlocking {
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
    fun `remove with time intervals`() = runBlocking {
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
