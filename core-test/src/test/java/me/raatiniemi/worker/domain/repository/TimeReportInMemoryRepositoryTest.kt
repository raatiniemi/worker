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
import me.raatiniemi.worker.domain.date.minutes
import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.cli
import me.raatiniemi.worker.domain.timeinterval.model.newTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeReportInMemoryRepositoryTest {
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

        val actual = repository.count(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count with time interval`() {
        val expected = 1
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )

        val actual = repository.count(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count with time intervals on same day`() {
        val expected = 1
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )

        val actual = repository.count(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count with time intervals on different days`() {
        val expected = 2
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now - 25.hours
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds.now
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        )

        val actual = repository.count(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered without time intervals`() {
        val expected = 0

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with registered time interval`() {
        val expected = 0
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds(10)
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with time interval`() {
        val expected = 1
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with time intervals on same day`() {
        val expected = 1
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        )
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        )

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with registered time interval on different days`() {
        val expected = 1
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        )
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds.now + 5.minutes
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered with time intervals on different days`() {
        val expected = 2
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now - 25.hours
            }
        )
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        )

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without time intervals`() {
        val expected = emptyList<TimeReportDay>()
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without time interval for project`() {
        val expected = emptyList<TimeReportDay>()
        timeIntervalRepository.add(
            newTimeInterval(cli) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(20))
        }.also { timeIntervalRepository.update(it) }
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time intervals for same day`() {
        val timeIntervals = listOf(
            timeIntervalRepository.add(
                newTimeInterval(android) {
                    start = Milliseconds(1)
                }
            ).let {
                it.clockOut(stop = Milliseconds(10))
            }.also { timeIntervalRepository.update(it) },
            timeIntervalRepository.add(
                newTimeInterval(android) {
                    start = Milliseconds(11)
                }
            ).let {
                it.clockOut(stop = Milliseconds(30))
            }.also { timeIntervalRepository.update(it) }
        )
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(Milliseconds(1)),
                timeIntervals.reversed()
            )
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time intervals for different days`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(10))
        }.also { timeIntervalRepository.update(it) }
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(90000000)
            }
        ).let {
            it.clockOut(stop = Milliseconds(93000000))
        }.also { timeIntervalRepository.update(it) }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(secondTimeInterval)
            ),
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with position`() {
        val firstTimeIntervals = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(10))
        }.also { timeIntervalRepository.update(it) }
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(90000000)
            }
        ).let {
            it.clockOut(stop = Milliseconds(93000000))
        }.also { timeIntervalRepository.update(it) }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(firstTimeIntervals.start),
                listOf(firstTimeIntervals)
            )
        )
        val loadRange = LoadRange(
            LoadPosition(1),
            LoadSize(10)
        )

        val actual = repository.findAll(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with load size`() {
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(10))
        }.also { timeIntervalRepository.update(it) }
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(90000000)
            }
        ).let {
            it.clockOut(stop = Milliseconds(93000000))
        }.also { timeIntervalRepository.update(it) }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(secondTimeInterval)
            )
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(1)
        )

        val actual = repository.findAll(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered without time intervals`() {
        val expected = emptyList<TimeReportDay>()
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findNotRegistered(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered without time intervals for project`() {
        val expected = emptyList<TimeReportDay>()
        timeIntervalRepository.add(
            newTimeInterval(cli) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(10))
        }.also { timeIntervalRepository.update(it) }
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findNotRegistered(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with time intervals for same day`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(10))
        }.also { timeIntervalRepository.update(it) }
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(11)
            }
        ).let {
            it.clockOut(stop = Milliseconds(93000000))
        }.also { timeIntervalRepository.update(it) }
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(30)
            }
        ).let {
            it.clockOut(stop = Milliseconds(45))
        }.let {
            timeInterval(it) { builder ->
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    secondTimeInterval,
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findNotRegistered(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with time interval for different days`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(10))
        }.also { timeIntervalRepository.update(it) }
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(90000000)
            }
        ).let {
            it.clockOut(stop = Milliseconds(93000000))
        }.also { timeIntervalRepository.update(it) }
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(180000000)
            }
        ).let {
            it.clockOut(stop = Milliseconds(183000000))
        }.let {
            timeInterval(it) { builder ->
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(secondTimeInterval)
            ),
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findNotRegistered(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with position`() {
        val firstTimeIntervals = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(10))
        }.also { timeIntervalRepository.update(it) }
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(90000000)
            }
        ).let {
            it.clockOut(stop = Milliseconds(93000000))
        }.also { timeIntervalRepository.update(it) }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(firstTimeIntervals.start),
                listOf(firstTimeIntervals)
            )
        )
        val loadRange = LoadRange(
            LoadPosition(1),
            LoadSize(10)
        )

        val actual = repository.findNotRegistered(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with load size`() {
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(10))
        }.also { timeIntervalRepository.update(it) }
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(90000000)
            }
        ).let {
            it.clockOut(stop = Milliseconds(93000000))
        }.also { timeIntervalRepository.update(it) }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(secondTimeInterval)
            )
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(1)
        )

        val actual = repository.findNotRegistered(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered with registered time intervals`() {
        val expected = emptyList<TimeReportDay>()
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).let {
            it.clockOut(stop = Milliseconds(10))
        }.let {
            timeInterval(it) { builder ->
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findNotRegistered(android, loadRange)

        assertEquals(expected, actual)
    }
}
