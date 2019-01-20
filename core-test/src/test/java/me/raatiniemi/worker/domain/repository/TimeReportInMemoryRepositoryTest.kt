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

package me.raatiniemi.worker.domain.repository

import me.raatiniemi.worker.domain.comparator.TimeReportItemComparator
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportItem
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class TimeReportInMemoryRepositoryTest {
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
    fun `getTimeReport withoutTimeIntervals`() {
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.getTimeReport(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun `getTimeReport withoutProjectTimeInterval`() {
        val timeInterval = TimeInterval.builder(2)
                .id(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(10)
                .build()
        val repository = TimeReportInMemoryRepository(listOf(timeInterval))

        val actual = repository.getTimeReport(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun `getTimeReport withTimeIntervalsForSameDate`() {
        val timeIntervals = listOf(
                TimeInterval.builder(1)
                        .id(1)
                        .startInMilliseconds(1)
                        .stopInMilliseconds(10)
                        .build(),
                TimeInterval.builder(1)
                        .id(2)
                        .startInMilliseconds(11)
                        .stopInMilliseconds(30)
                        .build()
        )
        val repository = TimeReportInMemoryRepository(timeIntervals)
        val expected = mapOf(
                resetToStartOfDay(1) to timeIntervals
                        .map { TimeReportItem(it) }
                        .toSortedSet(TimeReportItemComparator())
        )

        val actual = repository.getTimeReport(1, PageRequest.withOffset(0))

        assertEquals(expected, actual)
    }

    @Test
    fun `getTimeReport withTimeIntervalsForDifferentDates`() {
        val ti1 = TimeInterval.builder(1)
                .id(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(10)
                .build()
        val ti2 = TimeInterval.builder(1)
                .id(2)
                .startInMilliseconds(90000000)
                .stopInMilliseconds(93000000)
                .build()
        val timeIntervals = listOf(ti1, ti2)
        val repository = TimeReportInMemoryRepository(timeIntervals)
        val expected = mapOf(
                resetToStartOfDay(ti1.startInMilliseconds) to setOf(TimeReportItem(ti1)),
                resetToStartOfDay(ti2.startInMilliseconds) to setOf(TimeReportItem(ti2))
        )

        val actual = repository.getTimeReport(1, PageRequest.withOffset(0))

        assertEquals(expected, actual)
    }

    @Test
    fun `getTimeReportWithoutRegisteredEntries withoutTimeIntervals`() {
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.getTimeReportWithoutRegisteredEntries(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun `getTimeReportWithoutRegisteredEntries withRegisteredTimeIntervals`() {
        val timeInterval = TimeInterval.builder(1)
                .id(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(10)
                .register()
                .build()
        val repository = TimeReportInMemoryRepository(listOf(timeInterval))

        val actual = repository.getTimeReportWithoutRegisteredEntries(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun `getTimeReportWithoutRegisteredEntries withoutProjectTimeIntervals`() {
        val timeInterval = TimeInterval.builder(2)
                .id(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(10)
                .build()
        val repository = TimeReportInMemoryRepository(listOf(timeInterval))

        val actual = repository.getTimeReportWithoutRegisteredEntries(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun `getTimeReportWithoutRegisteredEntries withTimeIntervalsForSameDate`() {
        val ti1 = TimeInterval.builder(1)
                .id(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(10)
                .build()
        val ti2 = TimeInterval.builder(1)
                .id(2)
                .startInMilliseconds(11)
                .stopInMilliseconds(30)
                .build()
        val ti3 = TimeInterval.builder(1)
                .id(3)
                .startInMilliseconds(30)
                .stopInMilliseconds(45)
                .register()
                .build()
        val timeIntervals = listOf(ti1, ti2, ti3)
        val repository = TimeReportInMemoryRepository(timeIntervals)
        val expected = mapOf(
                resetToStartOfDay(1) to listOf(ti1, ti2)
                        .map { TimeReportItem(it) }
                        .toSortedSet(TimeReportItemComparator())
        )

        val actual = repository.getTimeReportWithoutRegisteredEntries(1, PageRequest.withOffset(0))

        assertEquals(expected, actual)
    }

    @Test
    fun `getTimeReportWithoutRegisteredEntries withTimeIntervalsForDifferentDates`() {
        val ti1 = TimeInterval.builder(1)
                .id(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(10)
                .build()
        val ti2 = TimeInterval.builder(1)
                .id(2)
                .startInMilliseconds(90000000)
                .stopInMilliseconds(93000000)
                .build()
        val ti3 = TimeInterval.builder(1)
                .id(2)
                .startInMilliseconds(180000000)
                .stopInMilliseconds(183000000)
                .register()
                .build()
        val timeIntervals = listOf(ti1, ti2, ti3)
        val repository = TimeReportInMemoryRepository(timeIntervals)
        val expected = mapOf(
                resetToStartOfDay(ti1.startInMilliseconds) to setOf(TimeReportItem(ti1)),
                resetToStartOfDay(ti2.startInMilliseconds) to setOf(TimeReportItem(ti2))
        )

        val actual = repository.getTimeReportWithoutRegisteredEntries(1, PageRequest.withOffset(0))

        assertEquals(expected, actual)
    }
}
