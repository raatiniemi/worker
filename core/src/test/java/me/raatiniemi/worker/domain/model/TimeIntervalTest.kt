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

package me.raatiniemi.worker.domain.model

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class TimeIntervalTest {
    @Test
    fun builder_withDefaultValues() {
        val timeInterval = TimeInterval.builder(1L, 1L)
            .build()

        assertEquals(1L, timeInterval.id)
        assertEquals(1L, timeInterval.projectId)
        assertEquals(0L, timeInterval.startInMilliseconds)
        assertEquals(0L, timeInterval.stopInMilliseconds)
        assertFalse(timeInterval.isRegistered)
    }

    @Test
    fun builder_withValues() {
        val timeInterval = TimeInterval.builder(2L, 1L)
            .startInMilliseconds(3L)
            .stopInMilliseconds(4L)
            .register()
            .build()

        assertEquals(2L, timeInterval.id)
        assertEquals(1L, timeInterval.projectId)
        assertEquals(3L, timeInterval.startInMilliseconds)
        assertEquals(4L, timeInterval.stopInMilliseconds)
        assertTrue(timeInterval.isRegistered)
    }

    @Test
    fun markAsRegistered() {
        val t1 = TimeInterval.builder(1L, 1L)
            .build()

        val t2 = t1.markAsRegistered()

        assertTrue(t2.isRegistered)
    }

    @Test
    fun markAsRegistered_alreadyRegistered() {
        val t1 = TimeInterval.builder(1L, 1L)
            .register()
            .build()

        val t2 = t1.markAsRegistered()

        assertTrue(t2.isRegistered)
    }

    @Test
    fun unmarkRegistered() {
        val t1 = TimeInterval.builder(1L, 1L)
            .register()
            .build()

        val t2 = t1.unmarkRegistered()

        assertFalse(t2.isRegistered)
    }

    @Test
    fun unmarkRegistered_notRegistered() {
        val t1 = TimeInterval.builder(1L, 1L).build()

        val t2 = t1.unmarkRegistered()

        assertFalse(t2.isRegistered)
    }

    @Test(expected = ClockOutBeforeClockInException::class)
    fun clockOutAt_clockOutBeforeClockIn() {
        val date = Date()
        val timeInterval = TimeInterval.builder(1L, 1L)
            .startInMilliseconds(date.time + 1)
            .build()

        timeInterval.clockOutAt(date)
    }

    @Test
    fun clockOutAt() {
        val date = Date()
        val t1 = TimeInterval.builder(1L, 1L)
            .build()

        val t2 = t1.clockOutAt(date)

        assertEquals(date.time, t2.stopInMilliseconds)
        assertFalse(t2.isRegistered)
    }

    @Test
    fun clockOutAt_withRegistered() {
        val date = Date()
        val t1 = TimeInterval.builder(1L, 1L)
            .register()
            .build()

        val t2 = t1.clockOutAt(date)

        assertEquals(date.time, t2.stopInMilliseconds)
        assertTrue(t2.isRegistered)
    }

    @Test
    fun isActive_whenActive() {
        val timeInterval = TimeInterval.builder(1L, 1L)
            .build()

        assertTrue(timeInterval.isActive)
    }

    @Test
    fun isActive_whenInactive() {
        val timeInterval = TimeInterval.builder(1L, 1L)
            .stopInMilliseconds(1L)
            .build()

        assertFalse(timeInterval.isActive)
    }

    @Test
    fun getTime_whenActive() {
        val timeInterval = TimeInterval.builder(1L, 1L)
            .startInMilliseconds(1L)
            .build()

        assertEquals(0L, timeInterval.time)
    }

    @Test
    fun getTime_whenInactive() {
        val timeInterval = TimeInterval.builder(1L, 1L)
            .startInMilliseconds(1L)
            .stopInMilliseconds(11L)
            .build()

        assertEquals(10L, timeInterval.time)
    }

    @Test
    fun getInterval_whenActive() {
        val timeInterval = TimeInterval.builder(1L, 1L)
            .startInMilliseconds(1L)
            .build()

        // TODO: Fix better interval measurement when active.
        // Currently unable because of the instantiation within getInterval.
        assertTrue(1L < timeInterval.interval)
    }

    @Test
    fun getInterval_whenInactive() {
        val timeInterval = TimeInterval.builder(1L, 1L)
            .startInMilliseconds(1L)
            .stopInMilliseconds(11L)
            .build()

        assertEquals(10L, timeInterval.interval)
    }

    @Test
    fun `calculate interval for active time interval`() {
        val expected = 10L
        val timeInterval = timeInterval {
            startInMilliseconds = 0
        }

        val actual = timeInterval.calculateInterval(stopForActive = Date(10))

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate interval for inactive time interval`() {
        val expected = 10L
        val timeInterval = timeInterval {
            startInMilliseconds = 0
            stopInMilliseconds = 10
        }

        val actual = timeInterval.calculateInterval()

        assertEquals(expected, actual)
    }
}
