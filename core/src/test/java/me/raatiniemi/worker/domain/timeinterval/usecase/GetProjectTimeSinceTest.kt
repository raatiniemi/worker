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
import me.raatiniemi.worker.domain.time.minutes
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.timeinterval.model.after
import me.raatiniemi.worker.domain.timeinterval.model.before
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetProjectTimeSinceTest {
    private lateinit var timeIntervals: TimeIntervalRepository
    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut

    private lateinit var getProjectTimeSince: GetProjectTimeSince

    @Before
    fun setUp() {
        timeIntervals = TimeIntervalInMemoryRepository()
        clockIn = ClockIn(timeIntervals)
        clockOut = ClockOut(timeIntervals)

        getProjectTimeSince = GetProjectTimeSince(timeIntervals)
    }

    @Test
    fun `get project time since day`() = runBlocking {
        val startingPoint = TimeIntervalStartingPoint.DAY
        val yesterday = before(startingPoint)
        clockIn(android, yesterday)
        clockOut(android, before(startingPoint, 30.minutes))
        clockIn(android, after(startingPoint))
        val timeInterval = clockOut(android, after(startingPoint, 2.hours))
        val expected = listOf(
            timeInterval
        )

        val actual = getProjectTimeSince(android, startingPoint)

        assertEquals(expected, actual)
    }

    @Test
    fun `get project time since week`() = runBlocking {
        val startingPoint = TimeIntervalStartingPoint.WEEK
        val yesterday = before(startingPoint)
        clockIn(android, yesterday)
        clockOut(android, before(startingPoint, 30.minutes))
        clockIn(android, after(startingPoint))
        val timeInterval = clockOut(android, after(startingPoint, 2.hours))
        val expected = listOf(
            timeInterval
        )

        val actual = getProjectTimeSince(android, startingPoint)

        assertEquals(expected, actual)
    }

    @Test
    fun `get project time since month`() = runBlocking {
        val startingPoint = TimeIntervalStartingPoint.MONTH
        val yesterday = before(startingPoint)
        clockIn(android, yesterday)
        clockOut(android, before(startingPoint, 30.minutes))
        clockIn(android, after(startingPoint))
        val timeInterval = clockOut(android, after(startingPoint, 2.hours))
        val expected = listOf(
            timeInterval
        )

        val actual = getProjectTimeSince(android, startingPoint)

        assertEquals(expected, actual)
    }
}
