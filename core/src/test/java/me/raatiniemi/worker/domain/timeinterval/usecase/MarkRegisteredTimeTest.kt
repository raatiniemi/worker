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
import me.raatiniemi.worker.domain.time.minutes
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MarkRegisteredTimeTest {
    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut
    private lateinit var getProjectTimeSince: GetProjectTimeSince

    private lateinit var markRegisteredTime: MarkRegisteredTime

    @Before
    fun setUp() {
        val timeIntervals = TimeIntervalInMemoryRepository()

        clockIn = ClockIn(timeIntervals)
        clockOut = ClockOut(timeIntervals)
        getProjectTimeSince = GetProjectTimeSince(timeIntervals)

        markRegisteredTime = MarkRegisteredTime(timeIntervals)
    }

    @Test
    fun `mark registered time with multiple unregistered items`() = runBlocking {
        val startingPoint = TimeIntervalStartingPoint.DAY
        val startingPointInMilliseconds = startingPoint.calculateMilliseconds()
        val timeIntervals = (1..4).map {
            clockIn(android, startingPointInMilliseconds)
            clockOut(android, startingPointInMilliseconds + 30.minutes)
        }
        val expected = timeIntervals.map { timeInterval ->
            timeInterval(timeInterval) { builder ->
                builder.isRegistered = true
            }
        }

        markRegisteredTime(timeIntervals)

        val actual = getProjectTimeSince(android, startingPoint)
        assertEquals(expected, actual)
    }

    @Test
    fun `mark registered time with multiple registered items`() = runBlocking {
        val startingPoint = TimeIntervalStartingPoint.DAY
        val startingPointInMilliseconds = startingPoint.calculateMilliseconds()
        val timeIntervals = (1..4).map {
            clockIn(android, startingPointInMilliseconds)
            val timeInterval = clockOut(android, startingPointInMilliseconds + 30.minutes)

            timeInterval(timeInterval) { builder ->
                builder.isRegistered = true
            }
        }
        val expected = timeIntervals.map { timeInterval ->
            timeInterval(timeInterval) { builder ->
                builder.isRegistered = false
            }
        }

        markRegisteredTime(timeIntervals)

        val actual = getProjectTimeSince(android, startingPoint)
        assertEquals(expected, actual)
    }

    @Test
    fun `mark registered time with multiple items`() = runBlocking {
        val startingPoint = TimeIntervalStartingPoint.DAY
        val startingPointInMilliseconds = startingPoint.calculateMilliseconds()
        val timeIntervals = (1..4).map {
            clockIn(android, startingPointInMilliseconds)
            val timeInterval = clockOut(android, startingPointInMilliseconds + 30.minutes)

            when (it) {
                1 -> timeInterval
                else -> timeInterval(timeInterval) { builder ->
                    builder.isRegistered = true
                }
            }
        }
        val expected = timeIntervals.map { timeInterval ->
            timeInterval(timeInterval) { builder ->
                builder.isRegistered = true
            }
        }

        markRegisteredTime(timeIntervals)

        val actual = getProjectTimeSince(android, startingPoint)
        assertEquals(expected, actual)
    }

    @Test(expected = UnableToMarkActiveTimeIntervalAsRegisteredException::class)
    fun `mark registered time with active time interval`() = runBlocking<Unit> {
        val startingPoint = TimeIntervalStartingPoint.DAY
        val startingPointInMilliseconds = startingPoint.calculateMilliseconds()
        val timeIntervals = listOf(
            clockIn(android, startingPointInMilliseconds)
        )

        markRegisteredTime(timeIntervals)
    }
}
