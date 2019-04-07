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

package me.raatiniemi.worker.domain.interactor

import me.raatiniemi.worker.domain.date.hours
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.model.newTimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class CalculateTimeTodayTest {
    private val project = Project(1, "Project name")

    private lateinit var repository: TimeIntervalRepository
    private lateinit var calculateTimeToday: CalculateTimeToday

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        calculateTimeToday = CalculateTimeToday(repository)
    }

    @Test
    fun `calculate time today without registered time`() {
        val expected = 0L

        val actual = calculateTimeToday(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate time today with registered time`() {
        val startingPoint = TimeIntervalStartingPoint.DAY.calculateMilliseconds()
        repository.add(newTimeInterval {
            startInMilliseconds = startingPoint
            stopInMilliseconds = startingPoint + 1.hours
        })
        val expected = 1.hours

        val actual = calculateTimeToday(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate time today with active time interval`() {
        val startingPoint = TimeIntervalStartingPoint.DAY.calculateMilliseconds()
        repository.add(newTimeInterval {
            startInMilliseconds = startingPoint
        })
        val stopForActive = Date(startingPoint + 1.hours)
        val expected = 1.hours

        val actual = calculateTimeToday(project, stopForActive)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate time today with registered time and active time interval`() {
        val startingPoint = TimeIntervalStartingPoint.DAY.calculateMilliseconds()
        repository.add(newTimeInterval {
            startInMilliseconds = startingPoint
            stopInMilliseconds = startingPoint + 1.hours
        })
        repository.add(newTimeInterval {
            startInMilliseconds = startingPoint
        })
        val stopForActive = Date(startingPoint + 1.hours)
        val expected = 2.hours

        val actual = calculateTimeToday(project, stopForActive)

        assertEquals(expected, actual)
    }
}
