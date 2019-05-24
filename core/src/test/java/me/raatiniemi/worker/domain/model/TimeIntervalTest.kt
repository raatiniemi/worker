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

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

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
            stopInMilliseconds = 1
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
            stopInMilliseconds = 11
        }

        assertEquals(10L, timeInterval.time)
    }

    @Test
    fun getInterval_whenActive() {
        val timeInterval = timeInterval {
            start = Milliseconds(1)
        }

        // TODO: Fix better interval measurement when active.
        // Currently unable because of the instantiation within getInterval.
        assertTrue(1L < timeInterval.interval)
    }

    @Test
    fun getInterval_whenInactive() {
        val timeInterval = timeInterval {
            start = Milliseconds(1)
            stopInMilliseconds = 11
        }

        assertEquals(10L, timeInterval.interval)
    }

    @Test
    fun `calculate interval for active time interval`() {
        val expected = 10L
        val timeInterval = timeInterval {
            start = Milliseconds(0)
        }

        val actual = timeInterval.calculateInterval(stopForActive = Date(10))

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate interval for inactive time interval`() {
        val expected = 10L
        val timeInterval = timeInterval {
            start = Milliseconds(0)
            stopInMilliseconds = 10
        }

        val actual = timeInterval.calculateInterval()

        assertEquals(expected, actual)
    }
}
