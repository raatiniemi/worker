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

import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetProjectTimeSinceTest {
    private val project = Project(1, "Name")

    private lateinit var repository: TimeIntervalRepository
    private lateinit var getProjectTimeSince: GetProjectTimeSince

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        getProjectTimeSince = GetProjectTimeSince(repository)
    }

    @Test
    fun execute_withDay() {
        repository.add(newTimeIntervalStartBefore(TimeIntervalStartingPoint.DAY) { })
        repository.add(newTimeIntervalStartAfter(TimeIntervalStartingPoint.DAY) { })
        val expected = listOf(
                timeIntervalStartAfter(TimeIntervalStartingPoint.DAY) {
                    id = 2
                }
        )

        val actual = getProjectTimeSince(project, TimeIntervalStartingPoint.DAY)

        assertEquals(expected, actual)
    }

    @Test
    fun execute_withWeek() {
        repository.add(newTimeIntervalStartBefore(TimeIntervalStartingPoint.WEEK) { })
        repository.add(newTimeIntervalStartAfter(TimeIntervalStartingPoint.WEEK) { })
        val expected = listOf(
                timeIntervalStartAfter(TimeIntervalStartingPoint.WEEK) {
                    id = 2
                }
        )

        val actual = getProjectTimeSince(project, TimeIntervalStartingPoint.WEEK)

        assertEquals(expected, actual)
    }

    @Test
    fun execute_withMonth() {
        repository.add(newTimeIntervalStartBefore(TimeIntervalStartingPoint.MONTH) { })
        repository.add(newTimeIntervalStartAfter(TimeIntervalStartingPoint.MONTH) { })
        val expected = listOf(
                timeIntervalStartAfter(TimeIntervalStartingPoint.MONTH) {
                    id = 2
                }
        )

        val actual = getProjectTimeSince(project, TimeIntervalStartingPoint.MONTH)

        assertEquals(expected, actual)
    }
}
