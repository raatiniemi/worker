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
import me.raatiniemi.worker.domain.model.timeInterval
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
    fun `count without time intervals`() {
        val expected = 0
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.count(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `count with time interval`() {
        val expected = 1
        val repository = TimeReportInMemoryRepository(
                listOf(
                        timeInterval { id = 1 }
                )
        )

        val actual = repository.count(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `count with time intervals on same day`() {
        val expected = 1
        val repository = TimeReportInMemoryRepository(
                listOf(
                        timeInterval { id = 1 },
                        timeInterval { id = 2 }
                )
        )

        val actual = repository.count(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `count with time intervals on different days`() {
        val expected = 2
        val repository = TimeReportInMemoryRepository(
                listOf(
                        timeInterval {
                            id = 1
                            isRegistered = true
                        },
                        timeInterval {
                            id = 2
                            startInMilliseconds = Date().time
                        }
                )
        )

        val actual = repository.count(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered without time intervals`() {
        val expected = 0
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.countNotRegistered(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with registered time interval`() {
        val expected = 0
        val repository = TimeReportInMemoryRepository(
                listOf(
                        timeInterval {
                            id = 1
                            isRegistered = true
                        }
                )
        )

        val actual = repository.countNotRegistered(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with time interval`() {
        val expected = 1
        val repository = TimeReportInMemoryRepository(
                listOf(
                        timeInterval { id = 1 }
                )
        )

        val actual = repository.countNotRegistered(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with time intervals on same day`() {
        val expected = 1
        val repository = TimeReportInMemoryRepository(
                listOf(
                        timeInterval { id = 1 },
                        timeInterval { id = 2 }
                )
        )

        val actual = repository.countNotRegistered(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with registered time interval on different days`() {
        val expected = 1
        val repository = TimeReportInMemoryRepository(
                listOf(
                        timeInterval { id = 1 },
                        timeInterval {
                            id = 2
                            startInMilliseconds = Date().time
                            isRegistered = true
                        }
                )
        )

        val actual = repository.countNotRegistered(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with time intervals on different days`() {
        val expected = 2
        val repository = TimeReportInMemoryRepository(
                listOf(
                        timeInterval { id = 1 },
                        timeInterval {
                            id = 2
                            startInMilliseconds = Date().time
                        }
                )
        )

        val actual = repository.countNotRegistered(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without time intervals`() {
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.findAll(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun `find all without time interval for project`() {
        val timeInterval = TimeInterval.builder(2)
                .id(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(10)
                .build()
        val repository = TimeReportInMemoryRepository(listOf(timeInterval))

        val actual = repository.findAll(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun `find all with time intervals for same day`() {
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

        val actual = repository.findAll(1, PageRequest.withOffset(0))

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time intervals for different days`() {
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

        val actual = repository.findAll(1, PageRequest.withOffset(0))

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered without time intervals`() {
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.findNotRegistered(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun `find not registered without time intervals for project`() {
        val timeInterval = TimeInterval.builder(2)
                .id(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(10)
                .build()
        val repository = TimeReportInMemoryRepository(listOf(timeInterval))

        val actual = repository.findNotRegistered(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun `find not registered with time intervals for same day`() {
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

        val actual = repository.findNotRegistered(1, PageRequest.withOffset(0))

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with time interval for different days`() {
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

        val actual = repository.findNotRegistered(1, PageRequest.withOffset(0))

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with registered time intervals`() {
        val timeInterval = TimeInterval.builder(1)
                .id(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(10)
                .register()
                .build()
        val repository = TimeReportInMemoryRepository(listOf(timeInterval))

        val actual = repository.findNotRegistered(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }
}
