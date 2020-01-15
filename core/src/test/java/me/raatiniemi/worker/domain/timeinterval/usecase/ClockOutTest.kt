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

package me.raatiniemi.worker.domain.timeinterval.usecase

import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.Milliseconds
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
    private lateinit var repository: TimeIntervalRepository
    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        clockIn = ClockIn(repository)
        clockOut = ClockOut(repository)
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

    @Test
    fun `clock out with active project`() = runBlocking {
        val milliseconds = Milliseconds.now
        val timeInterval = clockIn(android, milliseconds - 1.hours)
        val expected = timeInterval(timeInterval) { builder ->
            builder.stop = milliseconds
        }

        val actual = clockOut(android, milliseconds)

        val timeIntervals = repository.findAll(android, Milliseconds(0))
        assertEquals(listOf(expected), timeIntervals)
        assertEquals(expected, actual)
    }
}
