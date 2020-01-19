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
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.days
import me.raatiniemi.worker.domain.time.hours
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ClockOutTest {
    private lateinit var timeIntervals: TimeIntervalRepository
    private lateinit var clockIn: ClockIn

    private lateinit var clockOut: ClockOut

    @Before
    fun setUp() {
        timeIntervals = TimeIntervalInMemoryRepository()
        clockIn = ClockIn(timeIntervals)

        clockOut = ClockOut(timeIntervals)
    }

    @Test(expected = InactiveProjectException::class)
    fun `clock out with inactive project`() = runBlocking<Unit> {
        clockOut(android, Milliseconds.now)
    }

    @Test(expected = ClockOutBeforeClockInException::class)
    fun `clock out with date before clock in`() = runBlocking<Unit> {
        val milliseconds = Milliseconds.now
        clockIn(android, milliseconds + 1.hours)

        clockOut(android, milliseconds)
    }

    @Test(expected = ElapsedTimePastAllowedException::class)
    fun `clock out when clocked in one day ago`() = runBlocking<Unit> {
        val milliseconds = Milliseconds.now
        clockIn(android, milliseconds - 1.days)

        clockOut(android, milliseconds)
    }

    @Test
    fun `clock out with active project`() = runBlocking {
        val milliseconds = Milliseconds.now
        val timeInterval = clockIn(android, milliseconds - 1.hours)
        val expected = timeInterval(timeInterval) { builder ->
            builder.stop = milliseconds
        }

        val actual = clockOut(android, milliseconds)

        val timeIntervals = timeIntervals.findAll(android, Milliseconds(0))
        assertEquals(listOf(expected), timeIntervals)
        assertEquals(expected, actual)
    }
}
