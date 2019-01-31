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

import me.raatiniemi.worker.domain.model.TimeReportGroup
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
        val expected = emptyList<TimeReportGroup>()
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.findAll(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without time interval for project`() {
        val expected = emptyList<TimeReportGroup>()
        val repository = TimeReportInMemoryRepository(
                listOf(
                        timeInterval {
                            id = 1
                            projectId = 2
                            startInMilliseconds = 1
                            stopInMilliseconds = 20
                        }
                )
        )

        val actual = repository.findAll(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time intervals for same day`() {
        val timeIntervals = listOf(
                timeInterval {
                    id = 1
                    startInMilliseconds = 1
                    stopInMilliseconds = 10
                },
                timeInterval {
                    id = 2
                    startInMilliseconds = 11
                    stopInMilliseconds = 30
                }
        )
        val repository = TimeReportInMemoryRepository(timeIntervals)
        val expected = listOf(
                TimeReportGroup(
                        resetToStartOfDay(1),
                        timeIntervals.map { TimeReportItem(it) }
                )
        )

        val actual = repository.findAll(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time intervals for different days`() {
        val firstTimeInterval = timeInterval {
            id = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val secondTimeInterval = timeInterval {
            id = 2
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        val repository = TimeReportInMemoryRepository(
                listOf(
                        firstTimeInterval,
                        secondTimeInterval
                )
        )
        val expected = listOf(
                TimeReportGroup(
                        resetToStartOfDay(secondTimeInterval.startInMilliseconds),
                        listOf(
                                TimeReportItem(secondTimeInterval)
                        )
                ),
                TimeReportGroup(
                        resetToStartOfDay(firstTimeInterval.startInMilliseconds),
                        listOf(
                                TimeReportItem(firstTimeInterval)
                        )
                )
        )

        val actual = repository.findAll(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered without time intervals`() {
        val expected = emptyList<TimeReportGroup>()
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.findNotRegistered(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered without time intervals for project`() {
        val expected = emptyList<TimeReportGroup>()
        val repository = TimeReportInMemoryRepository(
                listOf(
                        timeInterval {
                            id = 1
                            projectId = 2
                            startInMilliseconds = 1
                            stopInMilliseconds = 10
                        }
                )
        )

        val actual = repository.findNotRegistered(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with time intervals for same day`() {
        val firstTimeInterval = timeInterval {
            id = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val secondTimeInterval = timeInterval {
            id = 2
            startInMilliseconds = 11
            stopInMilliseconds = 30
        }
        val registeredTimeInterval = timeInterval {
            id = 3
            startInMilliseconds = 30
            stopInMilliseconds = 45
            isRegistered = true
        }
        val repository = TimeReportInMemoryRepository(
                listOf(
                        firstTimeInterval,
                        secondTimeInterval,
                        registeredTimeInterval
                )
        )
        val expected = listOf(
                TimeReportGroup(
                        resetToStartOfDay(firstTimeInterval.startInMilliseconds),
                        listOf(
                                TimeReportItem(firstTimeInterval),
                                TimeReportItem(secondTimeInterval)
                        )
                )
        )

        val actual = repository.findNotRegistered(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with time interval for different days`() {
        val firstTimeInterval = timeInterval {
            id = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val secondTimeInterval = timeInterval {
            id = 2
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        val registeredTimeInterval = timeInterval {
            id = 3
            startInMilliseconds = 180000000
            stopInMilliseconds = 183000000
            isRegistered = true
        }
        val repository = TimeReportInMemoryRepository(
                listOf(
                        firstTimeInterval,
                        secondTimeInterval,
                        registeredTimeInterval
                )
        )
        val expected = listOf(
                TimeReportGroup(
                        resetToStartOfDay(secondTimeInterval.startInMilliseconds),
                        listOf(
                                TimeReportItem(secondTimeInterval)
                        )
                ),
                TimeReportGroup(
                        resetToStartOfDay(firstTimeInterval.startInMilliseconds),
                        listOf(
                                TimeReportItem(firstTimeInterval)
                        )
                )
        )

        val actual = repository.findNotRegistered(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with registered time intervals`() {
        val expected = emptyList<TimeReportGroup>()
        val repository = TimeReportInMemoryRepository(
                listOf(
                        timeInterval {
                            id = 1
                            startInMilliseconds = 1
                            stopInMilliseconds = 10
                            isRegistered = true
                        }
                )
        )

        val actual = repository.findNotRegistered(1, 0, 10)

        assertEquals(expected, actual)
    }
}
