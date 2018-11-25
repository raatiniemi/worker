/*
 * Copyright (C) 2017 Worker Project
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

import me.raatiniemi.worker.domain.exception.InvalidStartingPointException
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class GetProjectTimeSinceTest {
    private val project = Project.from(1, "Name")

    private lateinit var repository: TimeIntervalRepository
    private lateinit var useCase: GetProjectTimeSince

    // TODO: Use `getMillisecondsForStartingPoint` from core module.
    private fun getMillisecondsForStartingPoint(startingPoint: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        when (startingPoint) {
            GetProjectTimeSince.DAY -> {
            }
            GetProjectTimeSince.WEEK -> {
                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
            GetProjectTimeSince.MONTH -> calendar.set(Calendar.DAY_OF_MONTH, 1)
            else -> throw InvalidStartingPointException(
                    "Starting point '$startingPoint' is not valid"
            )
        }

        return calendar.timeInMillis
    }

    private fun timeIntervalAfterStartingPoint(startingPoint: Int): TimeInterval {
        val startingPointInMilliseconds = getMillisecondsForStartingPoint(startingPoint)

        return TimeInterval.builder(1)
                .startInMilliseconds(startingPointInMilliseconds + 1)
                .stopInMilliseconds(startingPointInMilliseconds + 10)
                .build()
    }

    private fun timeIntervalBeforeStartingPoint(startingPoint: Int): TimeInterval {
        val startingPointInMilliseconds = getMillisecondsForStartingPoint(startingPoint)

        return TimeInterval.builder(1)
                .startInMilliseconds(startingPointInMilliseconds - 10)
                .stopInMilliseconds(startingPointInMilliseconds)
                .build()
    }

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        useCase = GetProjectTimeSince(repository)
    }

    @Test
    fun execute_withDay() {
        val timeIntervalBefore = timeIntervalBeforeStartingPoint(GetProjectTimeSince.DAY)
        val timeIntervalAfter = timeIntervalAfterStartingPoint(GetProjectTimeSince.DAY)
        repository.add(timeIntervalBefore)
        repository.add(timeIntervalAfter)
        val expected = listOf(timeIntervalAfter.copy(id = 2))

        val actual = useCase.execute(project, GetProjectTimeSince.DAY)

        assertEquals(expected, actual)
    }

    @Test
    fun execute_withWeek() {
        val timeIntervalBefore = timeIntervalBeforeStartingPoint(GetProjectTimeSince.WEEK)
        val timeIntervalAfter = timeIntervalAfterStartingPoint(GetProjectTimeSince.WEEK)
        repository.add(timeIntervalBefore)
        repository.add(timeIntervalAfter)
        val expected = listOf(timeIntervalAfter.copy(id = 2))

        val actual = useCase.execute(project, GetProjectTimeSince.WEEK)

        assertEquals(expected, actual)
    }

    @Test
    fun execute_withMonth() {
        val timeIntervalBefore = timeIntervalBeforeStartingPoint(GetProjectTimeSince.MONTH)
        val timeIntervalAfter = timeIntervalAfterStartingPoint(GetProjectTimeSince.MONTH)
        repository.add(timeIntervalBefore)
        repository.add(timeIntervalAfter)
        val expected = listOf(timeIntervalAfter.copy(id = 2))

        val actual = useCase.execute(project, GetProjectTimeSince.MONTH)

        assertEquals(expected, actual)
    }

    @Test(expected = InvalidStartingPointException::class)
    fun execute_withInvalidStartingPoint() {
        useCase.execute(project, -1)
    }
}
