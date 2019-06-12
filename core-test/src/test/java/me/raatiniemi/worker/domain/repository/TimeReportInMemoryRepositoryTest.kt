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

import me.raatiniemi.worker.domain.date.hours
import me.raatiniemi.worker.domain.model.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeReportInMemoryRepositoryTest {
    private val project = Project(1, ProjectName("Project #1"))

    private lateinit var timeIntervalRepository: TimeIntervalRepository
    private lateinit var repository: TimeReportRepository

    @Before
    fun setUp() {
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        repository = TimeReportInMemoryRepository(timeIntervalRepository)
    }

    @Test
    fun `count without time intervals`() {
        val expected = 0

        val actual = repository.count(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count with time interval`() {
        val expected = 1
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
            }
        )

        val actual = repository.count(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count with time intervals on same day`() {
        val expected = 1
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
            }
        )

        val actual = repository.count(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count with time intervals on different days`() {
        val expected = 2
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now - 25.hours
                isRegistered = true
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )

        val actual = repository.count(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered without time intervals`() {
        val expected = 0

        val actual = repository.countNotRegistered(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with registered time interval`() {
        val expected = 0
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
                isRegistered = true
            }
        )

        val actual = repository.countNotRegistered(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with time interval`() {
        val expected = 1
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
            }
        )

        val actual = repository.countNotRegistered(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with time intervals on same day`() {
        val expected = 1
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )

        val actual = repository.countNotRegistered(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with registered time interval on different days`() {
        val expected = 1
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
                isRegistered = true
            }
        )

        val actual = repository.countNotRegistered(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with time intervals on different days`() {
        val expected = 2
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now - 25.hours
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )

        val actual = repository.countNotRegistered(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without time intervals`() {
        val expected = emptyList<TimeReportDay>()
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = repository.findAll(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without time interval for project`() {
        val expected = emptyList<TimeReportDay>()
        timeIntervalRepository.add(
            newTimeInterval {
                projectId = 2
                start = Milliseconds(1)
                stop = Milliseconds(20)
            }
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = repository.findAll(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time intervals for same day`() {
        val timeIntervals = listOf(
            timeIntervalRepository.add(
                newTimeInterval {
                    start = Milliseconds(1)
                    stop = Milliseconds(10)
                }
            ),
            timeIntervalRepository.add(
                newTimeInterval {
                    start = Milliseconds(11)
                    stop = Milliseconds(30)
                }
            )
        )
        val expected = listOf(
            TimeReportDay(
                resetToStartOfDay(Milliseconds(1)),
                timeIntervals.reversed()
                    .map { TimeReportItem(it) }
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = repository.findAll(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time intervals for different days`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
                stop = Milliseconds(10)
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(90000000)
                stop = Milliseconds(93000000)
            }
        )
        val expected = listOf(
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(
                    TimeReportItem(secondTimeInterval)
                )
            ),
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    TimeReportItem(firstTimeInterval)
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = repository.findAll(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with position`() {
        val firstTimeIntervals = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
                stop = Milliseconds(10)
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(90000000)
                stop = Milliseconds(93000000)
            }
        )
        val expected = listOf(
            TimeReportDay(
                resetToStartOfDay(firstTimeIntervals.start),
                listOf(
                    TimeReportItem(firstTimeIntervals)
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(1), LoadSize(10))

        val actual = repository.findAll(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with load size`() {
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
                stop = Milliseconds(10)
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(90000000)
                stop = Milliseconds(93000000)
            }
        )
        val expected = listOf(
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(
                    TimeReportItem(secondTimeInterval)
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(1))

        val actual = repository.findAll(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered without time intervals`() {
        val expected = emptyList<TimeReportDay>()
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = repository.findNotRegistered(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered without time intervals for project`() {
        val expected = emptyList<TimeReportDay>()
        timeIntervalRepository.add(
            newTimeInterval {
                projectId = 2
                start = Milliseconds(1)
                stop = Milliseconds(10)
            }
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = repository.findNotRegistered(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with time intervals for same day`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
                stop = Milliseconds(10)
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(11)
                stop = Milliseconds(30)
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(30)
                stop = Milliseconds(45)
                isRegistered = true
            }
        )
        val expected = listOf(
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    TimeReportItem(secondTimeInterval),
                    TimeReportItem(firstTimeInterval)
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = repository.findNotRegistered(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with time interval for different days`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
                stop = Milliseconds(10)
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(90000000)
                stop = Milliseconds(93000000)
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(180000000)
                stop = Milliseconds(183000000)
                isRegistered = true
            }
        )
        val expected = listOf(
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(
                    TimeReportItem(secondTimeInterval)
                )
            ),
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    TimeReportItem(firstTimeInterval)
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = repository.findNotRegistered(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with position`() {
        val firstTimeIntervals = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
                stop = Milliseconds(10)
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(90000000)
                stop = Milliseconds(93000000)
            }
        )
        val expected = listOf(
            TimeReportDay(
                resetToStartOfDay(firstTimeIntervals.start),
                listOf(
                    TimeReportItem(firstTimeIntervals)
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(1), LoadSize(10))

        val actual = repository.findNotRegistered(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with load size`() {
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
                stop = Milliseconds(10)
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(90000000)
                stop = Milliseconds(93000000)
            }
        )
        val expected = listOf(
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(
                    TimeReportItem(secondTimeInterval)
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(1))

        val actual = repository.findNotRegistered(project, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with registered time intervals`() {
        val expected = emptyList<TimeReportDay>()
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
                stop = Milliseconds(10)
                isRegistered = true
            }
        )

        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = repository.findNotRegistered(project, loadRange)

        assertEquals(expected, actual)
    }
}
