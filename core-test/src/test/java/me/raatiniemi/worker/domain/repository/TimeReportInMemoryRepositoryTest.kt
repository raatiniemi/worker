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

import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.model.timeInterval
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class TimeReportInMemoryRepositoryTest {
    private val project = Project(1, "Project #1")

    @Test
    fun `count without time intervals`() {
        val expected = 0
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.count(project)

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

        val actual = repository.count(project)

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

        val actual = repository.count(project)

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

        val actual = repository.count(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered without time intervals`() {
        val expected = 0
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.countNotRegistered(project)

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

        val actual = repository.countNotRegistered(project)

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

        val actual = repository.countNotRegistered(project)

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

        val actual = repository.countNotRegistered(project)

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

        val actual = repository.countNotRegistered(project)

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

        val actual = repository.countNotRegistered(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without time intervals`() {
        val expected = emptyList<TimeReportDay>()
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.findAll(project, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without time interval for project`() {
        val expected = emptyList<TimeReportDay>()
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

        val actual = repository.findAll(project, 0, 10)

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
            TimeReportDay(
                resetToStartOfDay(1),
                timeIntervals.reversed()
                    .map { TimeReportItem(it) }
            )
        )

        val actual = repository.findAll(project, 0, 10)

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
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.startInMilliseconds),
                listOf(
                    TimeReportItem(secondTimeInterval)
                )
            ),
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.startInMilliseconds),
                listOf(
                    TimeReportItem(firstTimeInterval)
                )
            )
        )

        val actual = repository.findAll(project, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered without time intervals`() {
        val expected = emptyList<TimeReportDay>()
        val repository = TimeReportInMemoryRepository(emptyList())

        val actual = repository.findNotRegistered(project, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered without time intervals for project`() {
        val expected = emptyList<TimeReportDay>()
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

        val actual = repository.findNotRegistered(project, 0, 10)

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
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.startInMilliseconds),
                listOf(
                    TimeReportItem(secondTimeInterval),
                    TimeReportItem(firstTimeInterval)
                )
            )
        )

        val actual = repository.findNotRegistered(project, 0, 10)

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
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.startInMilliseconds),
                listOf(
                    TimeReportItem(secondTimeInterval)
                )
            ),
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.startInMilliseconds),
                listOf(
                    TimeReportItem(firstTimeInterval)
                )
            )
        )

        val actual = repository.findNotRegistered(project, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with registered time intervals`() {
        val expected = emptyList<TimeReportDay>()
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

        val actual = repository.findNotRegistered(project, 0, 10)

        assertEquals(expected, actual)
    }
}
