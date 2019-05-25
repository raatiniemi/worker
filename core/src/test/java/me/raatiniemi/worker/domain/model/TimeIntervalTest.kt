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

import me.raatiniemi.worker.domain.date.hours
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeIntervalTest {
    @Test
    fun isActive_whenActive() {
        val timeInterval = timeInterval {
            start = Milliseconds(1)
        }

        assertTrue(timeInterval.isActive)
    }

    @Test
    fun isActive_whenInactive() {
        val timeInterval = timeInterval {
            start = Milliseconds(1)
            stop = Milliseconds(1)
        }

        assertFalse(timeInterval.isActive)
    }

    @Test
    fun getTime_whenActive() {
        val timeInterval = timeInterval {
            start = Milliseconds(1)
        }

        assertEquals(0L, timeInterval.time)
    }

    @Test
    fun getTime_whenInactive() {
        val timeInterval = timeInterval {
            start = Milliseconds(1)
            stop = Milliseconds(11)
        }

        assertEquals(10L, timeInterval.time)
    }

    @Test
    fun `calculate interval for active time interval`() {
        val now = Milliseconds.now
        val expected = Milliseconds(1.hours)
        val timeInterval = timeInterval {
            start = now - 1.hours
        }

        val actual = calculateInterval(timeInterval, now)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate interval for inactive time interval`() {
        val now = Milliseconds.now
        val expected = Milliseconds(1.hours)
        val timeInterval = timeInterval {
            start = now - 1.hours
            stop = now
        }

        val actual = calculateInterval(timeInterval)

        assertEquals(expected, actual)
    }
}
