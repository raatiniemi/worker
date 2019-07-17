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

package me.raatiniemi.worker.domain.model

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeIntervalsTest {
    @Test(expected = MissingTimeIntervalIdException::class)
    fun `time interval without id`() {
        timeInterval(android.id) { }
    }

    @Test(expected = MissingTimeIntervalStartException::class)
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
        val expected = TimeInterval.Default(
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
            stop = Milliseconds(10),
            isRegistered = true
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
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(10)
        }

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
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
        }

        val actual = timeInterval.clockOut(stop = Milliseconds(10))

        assertEquals(expected, actual)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun `clock out with inactive`() {
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
        }

        timeInterval.clockOut(stop = Milliseconds(10))
            .clockOut(stop = Milliseconds(20))
    }

    @Test(expected = UnsupportedOperationException::class)
    fun `clock out with registered`() {
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(10)
            builder.isRegistered = true
        }

        timeInterval.clockOut(stop = Milliseconds(20))
    }
}
