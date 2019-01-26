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
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.TimeReportInMemoryRepository
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

    private fun buildUseCase(timeIntervals: List<TimeInterval>): GetTimeReport {
        val repository = TimeReportInMemoryRepository(timeIntervals)

        return GetTimeReport(repository)
    }

    @Test
    fun execute_hideRegisteredTime() {
        val expected = emptyMap<Date, TimeReportItem>()
        val useCase = buildUseCase(emptyList())

        val actual = useCase.execute(1, 0, true)

        assertEquals(expected, actual)
    }

    @Test
    fun execute_withSortedDatesHidingRegisteredTime() {
        val timeInterval = timeInterval {
            id = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val useCase = buildUseCase(
                listOf(
                        timeInterval,
                        timeInterval {
                            id = 2
                            startInMilliseconds = 11
                            stopInMilliseconds = 30
                            isRegistered = true
                        }
                )
        )
        val expected = mapOf(
                resetToStartOfDay(timeInterval.startInMilliseconds) to sortedSetOf(
                        TimeReportItem(timeInterval)
                )
        )

        val actual = useCase.execute(1, 0, true)

        assertEquals(expected, actual)
    }

    @Test
    fun execute_withRegisteredTime() {
        val expected = emptyMap<Date, TimeReportItem>()
        val useCase = buildUseCase(emptyList())

        val actual = useCase.execute(1, 0, false)

        assertEquals(expected, actual)
    }

    @Test
    fun execute_withSortedDatesWithRegisteredTime() {
        val timeIntervals = listOf(
                timeInterval {
                    id = 1
                    startInMilliseconds = 1
                    stopInMilliseconds = 10
                    isRegistered = true
                },
                timeInterval {
                    id = 2
                    startInMilliseconds = 11
                    stopInMilliseconds = 30
                }
        )
        val useCase = buildUseCase(timeIntervals)
        val expected = mapOf(
                resetToStartOfDay(1) to timeIntervals
                        .map { TimeReportItem(it) }
                        .toSortedSet()
        )

        val actual = useCase.execute(1, 0, false)

        assertEquals(expected, actual)
    }
}
