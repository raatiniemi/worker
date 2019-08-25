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

package me.raatiniemi.worker.domain.usecase

import me.raatiniemi.worker.domain.exception.ActiveProjectException
import me.raatiniemi.worker.domain.model.Milliseconds
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class ClockInTest {
    private lateinit var repository: TimeIntervalRepository
    private lateinit var clockIn: ClockIn

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        clockIn = ClockIn(repository)
    }

    @Test(expected = ActiveProjectException::class)
    fun `clock in with active project`() {
        clockIn(android, Date())
        clockIn(android, Date())
    }

    @Test
    fun `clock in`() {
        val date = Date()
        val expected = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(date.time)
        }

        val actual = clockIn(android, date)

        val timeIntervals = repository.findAll(android, Milliseconds.empty)
        assertEquals(listOf(expected), timeIntervals)
        assertEquals(expected, actual)
    }
}
