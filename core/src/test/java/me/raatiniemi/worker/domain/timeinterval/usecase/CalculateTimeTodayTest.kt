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

package me.raatiniemi.worker.domain.timeinterval.usecase

import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.hours
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CalculateTimeTodayTest {
    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut

    private lateinit var timeIntervals: TimeIntervalRepository

    private lateinit var calculateTimeToday: CalculateTimeToday

    @Before
    fun setUp() {
        timeIntervals = TimeIntervalInMemoryRepository()

        clockIn = ClockIn(timeIntervals)
        clockOut = ClockOut(timeIntervals)

        calculateTimeToday = CalculateTimeToday(timeIntervals)
    }

    @Test
    fun `calculate time today without registered time`() = runBlocking {
        val expected = 0L

        val actual = calculateTimeToday(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate time today with registered time`() = runBlocking {
        val startingPoint = TimeIntervalStartingPoint.DAY.calculateMilliseconds()
        clockIn(android, startingPoint)
        clockOut(android, startingPoint + 1.hours)
        val expected = 1.hours

        val actual = calculateTimeToday(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate time today with active time interval`() = runBlocking {
        val startingPoint = TimeIntervalStartingPoint.DAY.calculateMilliseconds()
        clockIn(android, startingPoint)
        val expected = 1.hours

        val actual = calculateTimeToday(android, startingPoint + 1.hours)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate time today with registered time and active time interval`() = runBlocking {
        val startingPoint = TimeIntervalStartingPoint.DAY.calculateMilliseconds()
        clockIn(android, startingPoint)
        clockOut(android, startingPoint + 1.hours)
        clockIn(android, startingPoint + 2.hours)
        val expected = 2.hours

        val actual = calculateTimeToday(android, startingPoint + 3.hours)

        assertEquals(expected, actual)
    }
}
