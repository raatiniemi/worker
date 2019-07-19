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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeReportItemTest {
    @Test
    fun asTime() {
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
        }
        val item = TimeReportItem.with(timeInterval)

        assertSame(timeInterval, item.asTimeInterval())
    }

    @Test
    fun `time report item with active time interval`() {
        val expected = TimeReportItem.Default(
            TimeInterval.Active(
                id = TimeIntervalId(1),
                projectId = android.id,
                start = Milliseconds(1)

            )
        )
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
        }

        val actual = TimeReportItem.with(timeInterval)

        assertEquals(expected, actual)
    }

    @Test
    fun `time report item with inactive time interval`() {
        val expected = TimeReportItem.Inactive(
            TimeInterval.Inactive(
                id = TimeIntervalId(1),
                projectId = android.id,
                start = Milliseconds(1),
                stop = Milliseconds(10)

            )
        )
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(10)
        }

        val actual = TimeReportItem.with(timeInterval)

        assertEquals(expected, actual)
    }

    @Test
    fun `time report item with registered time interval`() {
        val expected = TimeReportItem.Registered(
            TimeInterval.Registered(
                id = TimeIntervalId(1),
                projectId = android.id,
                start = Milliseconds(1),
                stop = Milliseconds(10)

            )
        )
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(10)
            builder.isRegistered = true
        }

        val actual = TimeReportItem.with(timeInterval)

        assertEquals(expected, actual)
    }
}
