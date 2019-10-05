/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.timereport.repository

import me.raatiniemi.worker.domain.date.plus
import me.raatiniemi.worker.domain.model.LoadPosition
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.LoadSize
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.cli
import me.raatiniemi.worker.domain.project.model.ios
import me.raatiniemi.worker.domain.repository.resetToStartOfDay
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.domain.timeinterval.model.newTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timeinterval.usecase.MarkRegisteredTime
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class TimeReportInMemoryRepositoryTest {
    private lateinit var timeIntervalRepository: TimeIntervalRepository
    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut
    private lateinit var markRegisteredTime: MarkRegisteredTime

    private lateinit var repository: TimeReportRepository

    @Before
    fun setUp() {
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        clockIn = ClockIn(timeIntervalRepository)
        clockOut = ClockOut(timeIntervalRepository)
        markRegisteredTime = MarkRegisteredTime(timeIntervalRepository)

        repository = TimeReportInMemoryRepository(timeIntervalRepository)
    }

    // Count weeks

    @Test
    fun `count weeks without time intervals`() {
        val expected = 0

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks without time interval for project`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        val expected = 0

        val actual = repository.countWeeks(ios)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with time interval`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with time intervals`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, date = Date(startOfDay.value) + 20.minutes)
        clockOut(android, date = Date(startOfDay.value) + 30.minutes)
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with time intervals within same week`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val endOfWeek = setToEndOfWeek(startOfDay)
        clockIn(android, date = Date(startOfWeek.value))
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, date = Date(endOfWeek.value))
        clockOut(android, date = Date(endOfWeek.value) + 10.minutes)
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with time intervals in different weeks`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = startOfWeek + 2.weeks
        clockIn(android, date = Date(startOfWeek.value))
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, date = Date(nextWeek.value))
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        val expected = 2

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with registered time interval`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        clockIn(android, date = Date(startOfWeek.value))
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    // Count not registered weeks

    @Test
    fun `count not registered weeks without time intervals`() {
        val expected = 0

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks without time interval for project`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        val expected = 0

        val actual = repository.countNotRegisteredWeeks(ios)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with time interval`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        val expected = 1

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with time intervals`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, date = Date(startOfDay.value) + 20.minutes)
        clockOut(android, date = Date(startOfDay.value) + 30.minutes)
        val expected = 1

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with time intervals within same week`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val endOfWeek = setToEndOfWeek(startOfDay)
        clockIn(android, date = Date(startOfWeek.value))
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, date = Date(endOfWeek.value))
        clockOut(android, date = Date(endOfWeek.value) + 10.minutes)
        val expected = 1

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with time intervals in different weeks`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = startOfWeek + 2.weeks
        clockIn(android, date = Date(startOfWeek.value))
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, date = Date(nextWeek.value))
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        val expected = 2

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with registered time interval`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        clockIn(android, date = Date(startOfWeek.value))
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        val expected = 0

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
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
