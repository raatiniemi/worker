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
import org.junit.Test

@Suppress("FunctionName")
abstract class TimeIntervalRepositoryTest {
    protected val clockIn: ClockIn by lazy { ClockIn(timeIntervals) }
    protected val clockOut: ClockOut by lazy { ClockOut(timeIntervals) }

    protected abstract val timeIntervals: TimeIntervalRepository

    // Find all

    @Test
    fun findAll_withoutTimeIntervals() {
        val expected = emptyList<TimeInterval>()

        val actual = runBlocking {
            timeIntervals.findAll(android, Milliseconds.empty)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withoutTimeIntervalForProject() {
        runBlocking {
            clockIn(android, Milliseconds.now)
        }
        val expected = emptyList<TimeInterval>()

        val actual = runBlocking {
            timeIntervals.findAll(cli, Milliseconds.empty)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeInterval() {
        val timeInterval = runBlocking {
            clockIn(android, Milliseconds.now)
        }
        val expected = listOf(
            timeInterval
        )

        val actual = runBlocking {
            timeIntervals.findAll(android, Milliseconds.empty)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalOnStartingPoint() {
        val now = Milliseconds.now
        val timeInterval = runBlocking {
            clockIn(android, now)
            clockOut(android, now + 10.minutes)
        }
        val expected = listOf(
            timeInterval
        )

        val actual = runBlocking {
            timeIntervals.findAll(android, now)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalBeforeStartingPoint() {
        val now = Milliseconds.now
        runBlocking {
            clockIn(android, now)
            clockOut(android, now + 10.minutes)
        }
        val expected = emptyList<TimeInterval>()

        val actual = runBlocking {
            timeIntervals.findAll(android, now + 10.minutes)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withActiveTimeIntervalBeforeStartingPoint() {
        val now = Milliseconds.now
        val timeInterval = runBlocking {
            clockIn(android, now)
        }
        val expected = listOf(
            timeInterval
        )

        val actual = runBlocking {
            timeIntervals.findAll(android, now + 10.minutes)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalAfterStartingPoint() {
        val now = Milliseconds.now
        val timeInterval = runBlocking {
            clockIn(android, now + 10.minutes)
            clockOut(android, now + 20.minutes)
        }
        val expected = listOf(
            timeInterval
        )

        val actual = runBlocking {
            timeIntervals.findAll(android, now)
        }

        assertEquals(expected, actual)
    }

    // Find by id

    @Test
    fun findById_withoutTimeInterval() {
        val actual = runBlocking {
            timeIntervals.findById(TimeIntervalId(1))
        }

        assertNull(actual)
    }

    @Test
    fun findById_withTimeInterval() {
        val now = Milliseconds.now
        val expected = runBlocking {
            clockIn(android, now)
        }

        val actual = runBlocking {
            timeIntervals.findById(expected.id)
        }

        assertEquals(expected, actual)
    }

    // Find active by project id

    @Test
    fun findActiveByProjectId_withoutTimeIntervals() {
        val actual = runBlocking {
            timeIntervals.findActiveByProjectId(android.id)
        }

        assertNull(actual)
    }

    @Test
    fun findActiveByProjectId_withoutActiveTimeInterval() {
        val now = Milliseconds.now
        runBlocking {
            clockIn(android, now)
            clockOut(android, now + 10.minutes)
        }

        val actual = runBlocking {
            timeIntervals.findActiveByProjectId(android.id)
        }

        assertNull(actual)
    }

    @Test
    fun findActiveByProjectId_withTimeInterval() {
        val now = Milliseconds.now
        val expected = runBlocking {
            clockIn(android, now)
        }

        val actual = runBlocking {
            timeIntervals.findActiveByProjectId(android.id)
        }

        assertEquals(expected, actual)
    }

    // Add

    @Test
    fun add() {
        val now = Milliseconds.now
        val newTimeInterval = newTimeInterval(android) {
            start = now
        }
        val expected = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = now
            builder.stop = null
        }

        val actual = runBlocking {
            timeIntervals.add(newTimeInterval)
        }

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

        val actual = runBlocking {
            timeIntervals.update(timeInterval)
        }

        assertNull(actual)
    }

    @Test
    fun update_withTimeInterval() {
        val now = Milliseconds.now
        val timeInterval = runBlocking {
            clockIn(android, now)
        }
        val expected = timeInterval(timeInterval) { builder ->
            builder.stop = now + 10.minutes
        }

        val actual = runBlocking {
            timeIntervals.update(expected)
        }

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

        val actual = runBlocking {
            timeIntervals.update(
                listOf(
                    timeInterval
                )
            )
        }

        assertEquals(expected, actual)
    }

    @Test
    fun update_withTimeIntervals() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val nightTimeInterval = runBlocking {
            clockIn(android, startOfDay)
            clockOut(android, startOfDay + 4.hours)
        }
        val morningTimeInterval = runBlocking {
            clockIn(android, startOfDay + 5.hours)
            clockOut(android, startOfDay + 9.hours)
        }
        val expected = listOf(
            timeInterval(nightTimeInterval) { builder ->
                builder.isRegistered = true
            },
            timeInterval(morningTimeInterval) { builder ->
                builder.isRegistered = true
            }
        )

        val actual = runBlocking {
            timeIntervals.update(expected)
        }

        assertEquals(expected, actual)
    }

    // Remove

    @Test
    fun remove_withoutTimeInterval() {
        val expected = emptyList<TimeInterval>()

        val actual = runBlocking {
            timeIntervals.remove(TimeIntervalId(1))

            timeIntervals.findAll(android, Milliseconds.empty)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun remove_withTimeInterval() {
        val timeInterval = runBlocking {
            clockIn(android, Milliseconds.now)
        }
        val expected = emptyList<TimeInterval>()

        val actual = runBlocking {
            timeIntervals.remove(timeInterval.id)

            timeIntervals.findAll(android, Milliseconds.empty)
        }

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

        val actual = runBlocking {
            timeIntervals.remove(
                listOf(
                    timeInterval
                )
            )

            timeIntervals.findAll(android, Milliseconds.empty)
        }

        assertEquals(expected, actual)
    }

    @Test
    fun remove_withTimeIntervals() {
        val timeInterval = runBlocking {
            clockIn(android, Milliseconds.now)
        }
        val expected = emptyList<TimeInterval>()

        val actual = runBlocking {
            timeIntervals.remove(
                listOf(
                    timeInterval
                )
            )

            timeIntervals.findAll(android, Milliseconds.empty)
        }

        assertEquals(expected, actual)
    }
}
