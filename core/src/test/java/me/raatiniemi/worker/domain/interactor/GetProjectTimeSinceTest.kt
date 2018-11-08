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
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.*

@RunWith(JUnit4::class)
class GetProjectTimeSinceTest {
    private val project = Project.from(1, "Name")

    private lateinit var repository: TimeIntervalRepository
    private lateinit var useCase: GetProjectTimeSince

    private fun getMillisecondsForStartingPoint(startingPoint: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        when (startingPoint) {
            GetProjectTimeSince.DAY -> {
            }
            GetProjectTimeSince.WEEK -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            GetProjectTimeSince.MONTH -> calendar.set(Calendar.DAY_OF_MONTH, 1)
            else -> throw InvalidStartingPointException(
                    "Starting point '$startingPoint' is not valid"
            )
        }

        return calendar.timeInMillis
    }

    @Before
    fun setUp() {
        repository = mock(TimeIntervalRepository::class.java)
        useCase = GetProjectTimeSince(repository)
    }

    @Test
    fun execute_withDay() {
        useCase.execute(project, GetProjectTimeSince.DAY)

        verify<TimeIntervalRepository>(repository)
                .findAll(
                        eq<Project>(project),
                        eq(getMillisecondsForStartingPoint(GetProjectTimeSince.DAY))
                )
    }

    @Test
    fun execute_withWeek() {
        useCase.execute(project, GetProjectTimeSince.WEEK)

        verify<TimeIntervalRepository>(repository)
                .findAll(
                        eq<Project>(project),
                        eq(getMillisecondsForStartingPoint(GetProjectTimeSince.WEEK))
                )
    }

    @Test
    fun execute_withMonth() {
        useCase.execute(project, GetProjectTimeSince.MONTH)

        verify<TimeIntervalRepository>(repository)
                .findAll(
                        eq<Project>(project),
                        eq(getMillisecondsForStartingPoint(GetProjectTimeSince.MONTH))
                )
    }

    @Test(expected = InvalidStartingPointException::class)
    fun execute_withInvalidStartingPoint() {
        useCase.execute(project, -1)
    }
}
