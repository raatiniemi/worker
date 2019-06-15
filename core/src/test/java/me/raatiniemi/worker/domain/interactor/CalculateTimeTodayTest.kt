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
import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CalculateTimeTodayTest {
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

        val actual = calculateTimeToday(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate time today with registered time`() {
        val startingPoint = TimeIntervalStartingPoint.DAY.calculateMilliseconds()
        repository.add(
            newTimeInterval(android) {
                start = startingPoint
                stop = startingPoint + 1.hours
            }
        )
        val expected = 1.hours

        val actual = calculateTimeToday(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate time today with active time interval`() {
        val startingPoint = TimeIntervalStartingPoint.DAY.calculateMilliseconds()
        repository.add(
            newTimeInterval(android) {
                start = startingPoint
            }
        )
        val stopForActive = startingPoint + 1.hours
        val expected = 1.hours

        val actual = calculateTimeToday(android, stopForActive)

        assertEquals(expected, actual)
    }

    @Test
    fun `calculate time today with registered time and active time interval`() {
        val startingPoint = TimeIntervalStartingPoint.DAY.calculateMilliseconds()
        repository.add(
            newTimeInterval(android) {
                start = startingPoint
                stop = startingPoint + 1.hours
            }
        )
        repository.add(
            newTimeInterval(android) {
                start = startingPoint
            }
        )
        val stopForActive = startingPoint + 1.hours
        val expected = 2.hours

        val actual = calculateTimeToday(android, stopForActive)

        assertEquals(expected, actual)
    }
}
