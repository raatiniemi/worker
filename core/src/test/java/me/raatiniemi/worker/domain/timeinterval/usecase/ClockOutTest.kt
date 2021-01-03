/*
 * Copyright (C) 2021 Tobias Raatiniemi
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
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.days
import me.raatiniemi.worker.domain.time.hours
import me.raatiniemi.worker.domain.time.minutes
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class ClockOutTest {
    private lateinit var timeIntervals: TimeIntervalRepository
    private lateinit var clockIn: ClockIn
    private lateinit var getProjectTimeSince: GetProjectTimeSince

    private lateinit var clockOut: ClockOut

    @Before
    fun setUp() {
        timeIntervals = TimeIntervalInMemoryRepository()
        clockIn = ClockIn(timeIntervals)
        getProjectTimeSince = GetProjectTimeSince(timeIntervals)

        clockOut = ClockOut(timeIntervals)
    }

    @Test(expected = InactiveProjectException::class)
    fun `clock out without active project`() {
        val startingPoint = TimeIntervalStartingPoint.DAY
        val startingPointInMilliseconds = startingPoint.calculateMilliseconds()

        runBlocking {
            clockOut(android, startingPointInMilliseconds)
        }
    }

    @Test(expected = ClockOutBeforeClockInException::class)
    fun `clock out with date before clock in`() {
        val startingPoint = TimeIntervalStartingPoint.DAY
        val startingPointInMilliseconds = startingPoint.calculateMilliseconds()

        runBlocking {
            clockIn(android, startingPointInMilliseconds)

            clockOut(android, startingPointInMilliseconds - 1.hours)
        }
    }

    @Test(expected = ElapsedTimePastAllowedException::class)
    fun `clock out when clocked in over one day ago`() {
        val startingPoint = TimeIntervalStartingPoint.DAY
        val startingPointInMilliseconds = startingPoint.calculateMilliseconds()

        runBlocking {
            clockIn(android, startingPointInMilliseconds)

            clockOut(android, startingPointInMilliseconds + 1.days + 3.minutes)
        }
    }

    @Test
    fun `clock out with active project`() {
        val startingPoint = TimeIntervalStartingPoint.DAY
        val startingPointInMilliseconds = startingPoint.calculateMilliseconds()

        runBlocking {
            val timeInterval = clockIn(android, startingPointInMilliseconds)
            val expected = listOf(
                timeInterval(timeInterval) { builder ->
                    builder.stop = startingPointInMilliseconds + 1.hours
                }
            )

            clockOut(android, startingPointInMilliseconds + 1.hours)

            val actual = getProjectTimeSince(android, startingPoint)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `clock out when start and stop is on different days`() {
        val defaultTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Stockholm"))
        runBlocking {
            val timeInterval = clockIn(android, Milliseconds(1589319000000))
            val expected = listOf(
                timeInterval(timeInterval) { builder ->
                    builder.stop = Milliseconds(1589320799999)
                },
                timeInterval(android.id) { builder ->
                    builder.id = TimeIntervalId(2)
                    builder.start = Milliseconds(1589320800000)
                    builder.stop = Milliseconds(1589322600000)
                }
            )

            clockOut(android, Milliseconds(1589322600000))

            val actual = timeIntervals.findAll(android, timeInterval.start)
            assertEquals(expected, actual)
        }
        TimeZone.setDefault(defaultTimeZone)
    }
}
