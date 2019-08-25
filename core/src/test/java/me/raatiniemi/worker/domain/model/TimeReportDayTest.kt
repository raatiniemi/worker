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

import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class TimeReportDayTest {
    @Test
    fun `time report day without time intervals`() {
        val date = Date()
        val expected = TimeReportDay.Inactive(date, emptyList())

        val actual = timeReportDay(date, emptyList())

        assertEquals(expected, actual)
    }

    @Test
    fun `time report day with time interval`() {
        val date = Date()
        val timeIntervals = listOf(
            timeInterval(android.id) { timeInterval ->
                timeInterval.id = TimeIntervalId(1)
                timeInterval.start = Milliseconds(1)
                timeInterval.stop = Milliseconds(10)
            }
        )
        val expected = TimeReportDay.Inactive(date, timeIntervals)

        val actual = timeReportDay(date, timeIntervals)

        assertEquals(expected, actual)
    }

    @Test
    fun `time report day with active time interval`() {
        val date = Date()
        val timeIntervals = listOf(
            timeInterval(android.id) { timeInterval ->
                timeInterval.id = TimeIntervalId(1)
                timeInterval.start = Milliseconds(1)
                timeInterval.stop = Milliseconds(10)
            },
            timeInterval(android.id) { timeInterval ->
                timeInterval.id = TimeIntervalId(2)
                timeInterval.start = Milliseconds(100)
            }
        )
        val expected = TimeReportDay.Active(date, timeIntervals)

        val actual = timeReportDay(date, timeIntervals)

        assertEquals(expected, actual)
    }
}
