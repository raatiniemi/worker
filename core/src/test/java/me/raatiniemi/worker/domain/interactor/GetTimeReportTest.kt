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

import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimesheetItem
import me.raatiniemi.worker.domain.repository.TimesheetInMemoryRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class GetTimeReportTest {
    private fun resetToStartOfDay(timeInMilliseconds: Long): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMilliseconds
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }

    @Test
    fun execute_hideRegisteredTime() {
        val repository = TimesheetInMemoryRepository(emptyList())
        val useCase = GetTimeReport(repository)

        val actual = useCase.execute(1, 0, true)

        assertEquals(emptyMap<Date, TimesheetItem>(), actual)
    }

    @Test
    fun execute_withSortedDatesHidingRegisteredTime() {
        val timeInterval = TimeInterval.builder(1)
                .id(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(10)
                .build()
        val timeIntervals = listOf(
                timeInterval,
                TimeInterval.builder(1)
                        .id(2)
                        .startInMilliseconds(11)
                        .stopInMilliseconds(30)
                        .register()
                        .build()
        )
        val repository = TimesheetInMemoryRepository(timeIntervals)
        val useCase = GetTimeReport(repository)
        val expected = mapOf(
                resetToStartOfDay(timeInterval.startInMilliseconds) to sortedSetOf(
                        TimesheetItem(timeInterval)
                )
        )

        val actual = useCase.execute(1, 0, true)

        assertEquals(expected, actual)
    }

    @Test
    fun execute_withRegisteredTime() {
        val repository = TimesheetInMemoryRepository(emptyList())
        val useCase = GetTimeReport(repository)

        val actual = useCase.execute(1, 0, false)

        assertEquals(emptyMap<Date, TimesheetItem>(), actual)
    }

    @Test
    fun execute_withSortedDatesWithRegisteredTime() {
        val timeIntervals = listOf(
                TimeInterval.builder(1)
                        .id(1)
                        .startInMilliseconds(1)
                        .stopInMilliseconds(10)
                        .register()
                        .build(),
                TimeInterval.builder(1)
                        .id(2)
                        .startInMilliseconds(11)
                        .stopInMilliseconds(30)
                        .build()
        )
        val repository = TimesheetInMemoryRepository(timeIntervals)
        val useCase = GetTimeReport(repository)
        val expected = mapOf(
                resetToStartOfDay(1) to timeIntervals
                        .map { TimesheetItem(it) }
                        .toSortedSet()
        )

        val actual = useCase.execute(1, 0, false)

        assertEquals(expected, actual)
    }
}
