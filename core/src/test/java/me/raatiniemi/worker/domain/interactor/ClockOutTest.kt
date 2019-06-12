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

package me.raatiniemi.worker.domain.interactor

import me.raatiniemi.worker.domain.date.hours
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException
import me.raatiniemi.worker.domain.exception.InactiveProjectException
import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class ClockOutTest {
    private val project = Project(1, ProjectName("Project name"))

    private lateinit var repository: TimeIntervalRepository
    private lateinit var clockOut: ClockOut

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        clockOut = ClockOut(repository)
    }

    @Test(expected = InactiveProjectException::class)
    fun `clock out with inactive project`() {
        clockOut(1L, Date())
    }

    @Test(expected = ClockOutBeforeClockInException::class)
    fun `clock out with date before clock in`() {
        val date = Date()
        repository.add(
            newTimeInterval {
                start = Milliseconds(date.time) + 1.hours
            }
        )

        clockOut(1, date)
    }

    @Test
    fun `clock out with active project`() {
        val date = Date()
        repository.add(
            newTimeInterval {
                start = Milliseconds(1)
            }
        )
        val expected = listOf(
            timeInterval {
                id = 1
                start = Milliseconds(1)
                stop = Milliseconds(date.time)
            }
        )

        clockOut(1, date)

        val actual = repository.findAll(project, Milliseconds(0))
        assertEquals(expected, actual)
    }
}
