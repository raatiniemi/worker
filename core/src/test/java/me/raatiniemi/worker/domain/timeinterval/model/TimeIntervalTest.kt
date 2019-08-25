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

package me.raatiniemi.worker.domain.timeinterval.model

import me.raatiniemi.worker.domain.date.hours
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException
import me.raatiniemi.worker.domain.model.Milliseconds
import me.raatiniemi.worker.domain.project.model.android
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeIntervalTest {
    @Test(expected = MissingIdForTimeIntervalException::class)
    fun `time interval without id`() {
        timeInterval(android.id) { }
    }

    @Test(expected = MissingStartForTimeIntervalException::class)
    fun `time interval without start`() {
        timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
        }
    }

    @Test(expected = ClockOutBeforeClockInException::class)
    fun `time interval with stop before start`() {
        timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(2)
            builder.stop = Milliseconds(1)
        }
    }

    @Test
    fun `time interval for active`() {
        val expected = TimeInterval.Active(
            id = TimeIntervalId(1),
            projectId = android.id,
            start = Milliseconds(1)
        )

        val actual = timeInterval(expected.projectId) { builder ->
            builder.id = expected.id
            builder.start = expected.start
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `time interval for inactive`() {
        val expected = TimeInterval.Inactive(
            id = TimeIntervalId(1),
            projectId = android.id,
            start = Milliseconds(1),
            stop = Milliseconds(10)
        )

        val actual = timeInterval(expected.projectId) { builder ->
            builder.id = expected.id
            builder.start = expected.start
            builder.stop = expected.stop
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `time interval for registered`() {
        val expected = TimeInterval.Registered(
            id = TimeIntervalId(1),
            projectId = android.id,
            start = Milliseconds(1),
            stop = Milliseconds(10)
        )

        val actual = timeInterval(expected.projectId) { builder ->
            builder.id = expected.id
            builder.start = expected.start
            builder.stop = expected.stop
            builder.isRegistered = true
        }

        assertEquals(expected, actual)
    }

    @Test(expected = ClockOutBeforeClockInException::class)
    fun `clock out with stop before start`() {
        val timeInterval = TimeInterval.Active(
            id = TimeIntervalId(1),
            projectId = android.id,
            start = Milliseconds(10)
        )

        timeInterval.clockOut(stop = Milliseconds(1))
    }

    @Test
    fun `clock out with start before stop`() {
        val expected = TimeInterval.Inactive(
            id = TimeIntervalId(1),
            projectId = android.id,
            start = Milliseconds(1),
            stop = Milliseconds(10)
        )
        val timeInterval = TimeInterval.Active(
            id = TimeIntervalId(1),
            projectId = android.id,
            start = Milliseconds(1)
        )

        val actual = timeInterval.clockOut(stop = Milliseconds(10))

        assertEquals(expected, actual)
    }

    @Test
    fun `is active for active`() {
        val expected = true
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds.now
        }

        val actual = isActive(timeInterval)

        assertEquals(expected, actual)
    }

    @Test
    fun `is active for inactive`() {
        val expected = false
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds.now - 1.hours
            builder.stop = Milliseconds.now
        }

        val actual = isActive(timeInterval)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate time for active`() {
        val expected = Milliseconds.empty
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds.now
        }

        val actual = calculateTime(timeInterval)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate time for inactive`() {
        val now = Milliseconds.now
        val expected = Milliseconds(1.hours)
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = now - 1.hours
            builder.stop = now
        }

        val actual = calculateTime(timeInterval)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate interval for active time interval`() {
        val now = Milliseconds.now
        val expected = Milliseconds(1.hours)
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = now - 1.hours
        }

        val actual = calculateInterval(timeInterval, now)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate interval for inactive time interval`() {
        val now = Milliseconds.now
        val expected = Milliseconds(1.hours)
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = now - 1.hours
            builder.stop = now
        }

        val actual = calculateInterval(timeInterval)

        assertEquals(expected, actual)
    }
}
