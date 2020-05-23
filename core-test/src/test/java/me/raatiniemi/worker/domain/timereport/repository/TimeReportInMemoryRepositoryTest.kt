/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.model.LoadPosition
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.LoadSize
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.ios
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timeinterval.usecase.MarkRegisteredTime
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportWeek
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

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
    fun `count weeks without time intervals`() = runBlocking {
        val expected = 0

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks without time interval for project`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        val expected = 0

        val actual = repository.countWeeks(ios)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with time interval`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with time intervals`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, startOfDay + 30.minutes)
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with time intervals within same week`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val endOfWeek = setToEndOfWeek(startOfDay) - 30.minutes
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with time intervals in different weeks`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = startOfWeek + 2.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        val expected = 2

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with registered time interval`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with time intervals using fixed values within same week`() = runBlocking {
        val startOfWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val endOfWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count weeks with time intervals during three weeks over new year`() = runBlocking {
        val endOfFirstWeek = Milliseconds(1577606247000) // 2019-12-29 07:57:27
        val firstInSecondWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val secondInSecondWeek = Milliseconds(1577779099000) // 2019-12-31 07:58:19
        val thirdInSecondWeek = Milliseconds(1577985643000) // 2020-01-02 17:20:43
        val fourthInSecondWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
        val startOfThirdWeek = Milliseconds(1578297584000) // 2020-01-06 07:59:44
        clockIn(android, endOfFirstWeek)
        clockOut(android, endOfFirstWeek + 10.minutes)
        clockIn(android, firstInSecondWeek)
        clockOut(android, firstInSecondWeek + 10.minutes)
        clockIn(android, secondInSecondWeek)
        clockOut(android, secondInSecondWeek + 10.minutes)
        clockIn(android, thirdInSecondWeek)
        clockOut(android, thirdInSecondWeek + 10.minutes)
        clockIn(android, fourthInSecondWeek)
        clockOut(android, fourthInSecondWeek + 10.minutes)
        clockIn(android, startOfThirdWeek)
        clockOut(android, startOfThirdWeek + 10.minutes)
        val expected = 3

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    // Count not registered weeks

    @Test
    fun `count not registered weeks without time intervals`() = runBlocking {
        val expected = 0

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks without time interval for project`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        val expected = 0

        val actual = repository.countNotRegisteredWeeks(ios)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with time interval`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        val expected = 1

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with time intervals`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, startOfDay + 30.minutes)
        val expected = 1

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with time intervals within same week`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val endOfWeek = setToEndOfWeek(startOfDay) - 30.minutes
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        val expected = 1

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with time intervals in different weeks`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = startOfWeek + 2.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        val expected = 2

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with registered time interval`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        val expected = 0

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with time intervals using fixed values within same week`() =
        runBlocking {
        val startOfWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val endOfWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        val expected = 1

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count not registered weeks with time intervals during three weeks over new year`() {
        runBlocking {
            val endOfFirstWeek = Milliseconds(1577606247000) // 2019-12-29 07:57:27
            val firstInSecondWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
            val secondInSecondWeek = Milliseconds(1577779099000) // 2019-12-31 07:58:19
            val thirdInSecondWeek = Milliseconds(1577985643000) // 2020-01-02 17:20:43
            val fourthInSecondWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
            val startOfThirdWeek = Milliseconds(1578297584000) // 2020-01-06 07:59:44
            clockIn(android, endOfFirstWeek)
            clockOut(android, endOfFirstWeek + 10.minutes)
            clockIn(android, firstInSecondWeek)
            clockOut(android, firstInSecondWeek + 10.minutes)
            clockIn(android, secondInSecondWeek)
            clockOut(android, secondInSecondWeek + 10.minutes)
            clockIn(android, thirdInSecondWeek)
            clockOut(android, thirdInSecondWeek + 10.minutes)
            clockIn(android, fourthInSecondWeek)
            clockOut(android, fourthInSecondWeek + 10.minutes)
            clockIn(android, startOfThirdWeek)
            clockOut(android, startOfThirdWeek + 10.minutes)
            val expected = 3

            val actual = repository.countNotRegisteredWeeks(android)

            assertEquals(expected, actual)
        }
    }

    // Find weeks

    @Test
    fun `find weeks without time intervals`() = runBlocking {
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = emptyList<TimeReportWeek>()

        val actual = repository.findWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find weeks without time interval for project`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = emptyList<TimeReportWeek>()

        val actual = repository.findWeeks(ios, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find weeks with time interval`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        startOfDay,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfDay
                                builder.stop = startOfDay + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find weeks with time intervals`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, startOfDay + 30.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        startOfDay,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfDay + 20.minutes
                                builder.stop = startOfDay + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfDay
                                builder.stop = startOfDay + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find weeks with time intervals within same week`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val endOfWeek = setToEndOfWeek(startOfWeek) - 30.minutes
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        endOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = endOfWeek
                                builder.stop = endOfWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        startOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find weeks with time intervals in different weeks`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = nextWeek
                                builder.stop = nextWeek + 10.minutes
                            }
                        )
                    )
                )
            ),
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        startOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find weeks with registered time interval`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        startOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                                builder.isRegistered = true
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find weeks when excluding by load position`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        val loadRange = LoadRange(LoadPosition(1), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        startOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfWeek + 20.minutes
                                builder.stop = startOfWeek + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find weeks when excluding by load size`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(1))
        val expected = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
                                builder.start = nextWeek
                                builder.stop = nextWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find weeks with time intervals using fixed values within same week`() = runBlocking {
        val startOfWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val endOfWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        endOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = endOfWeek
                                builder.stop = endOfWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        startOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find weeks with time intervals during three weeks over new year`() = runBlocking {
        val endOfFirstWeek = Milliseconds(1577606247000) // 2019-12-29 07:57:27
        val firstInSecondWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val secondInSecondWeek = Milliseconds(1577779099000) // 2019-12-31 07:58:19
        val thirdInSecondWeek = Milliseconds(1577985643000) // 2020-01-02 17:20:43
        val fourthInSecondWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
        val startOfThirdWeek = Milliseconds(1578297584000) // 2020-01-06 07:59:44
        clockIn(android, endOfFirstWeek)
        clockOut(android, endOfFirstWeek + 10.minutes)
        clockIn(android, firstInSecondWeek)
        clockOut(android, firstInSecondWeek + 10.minutes)
        clockIn(android, secondInSecondWeek)
        clockOut(android, secondInSecondWeek + 10.minutes)
        clockIn(android, thirdInSecondWeek)
        clockOut(android, thirdInSecondWeek + 10.minutes)
        clockIn(android, fourthInSecondWeek)
        clockOut(android, fourthInSecondWeek + 10.minutes)
        clockIn(android, startOfThirdWeek)
        clockOut(android, startOfThirdWeek + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                startOfThirdWeek,
                listOf(
                    timeReportDay(
                        startOfThirdWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(6)
                                builder.start = startOfThirdWeek
                                builder.stop = startOfThirdWeek + 10.minutes
                            }
                        )
                    )
                )
            ),
            timeReportWeek(
                firstInSecondWeek,
                listOf(
                    timeReportDay(
                        fourthInSecondWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(5)
                                builder.start = fourthInSecondWeek
                                builder.stop = fourthInSecondWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        thirdInSecondWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(4)
                                builder.start = thirdInSecondWeek
                                builder.stop = thirdInSecondWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        secondInSecondWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
                                builder.start = secondInSecondWeek
                                builder.stop = secondInSecondWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        firstInSecondWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = firstInSecondWeek
                                builder.stop = firstInSecondWeek + 10.minutes
                            }
                        )
                    )
                )
            ),
            timeReportWeek(
                endOfFirstWeek,
                listOf(
                    timeReportDay(
                        endOfFirstWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = endOfFirstWeek
                                builder.stop = endOfFirstWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    // Find not registered weeks

    @Test
    fun `find not registered weeks without time intervals`() = runBlocking {
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = emptyList<TimeReportWeek>()

        val actual = repository.findNotRegisteredWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered weeks without time interval for project`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = emptyList<TimeReportWeek>()

        val actual = repository.findNotRegisteredWeeks(ios, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered weeks with time interval`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        startOfDay,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfDay
                                builder.stop = startOfDay + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findNotRegisteredWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered weeks with time intervals`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, startOfDay + 30.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        startOfDay,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfDay + 20.minutes
                                builder.stop = startOfDay + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfDay
                                builder.stop = startOfDay + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findNotRegisteredWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered weeks with time intervals within same week`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val endOfWeek = setToEndOfWeek(startOfWeek) - 30.minutes
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        endOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = endOfWeek
                                builder.stop = endOfWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        startOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findNotRegisteredWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered weeks with time intervals in different weeks`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = nextWeek
                                builder.stop = nextWeek + 10.minutes
                            }
                        )
                    )
                )
            ),
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        startOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findNotRegisteredWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered weeks with registered time interval`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
        val expected = emptyList<TimeReportWeek>()

        val actual = repository.findNotRegisteredWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered weeks when excluding by load position`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        val loadRange = LoadRange(LoadPosition(1), LoadSize(10))
        val expected = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        startOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfWeek + 20.minutes
                                builder.stop = startOfWeek + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findNotRegisteredWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered weeks when excluding by load size`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        val loadRange = LoadRange(LoadPosition(0), LoadSize(1))
        val expected = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
                                builder.start = nextWeek
                                builder.stop = nextWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = repository.findNotRegisteredWeeks(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find not registered weeks with time intervals using fixed values within same week`() {
        runBlocking {
            val startOfWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
            val endOfWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
            clockIn(android, startOfWeek)
            clockOut(android, startOfWeek + 10.minutes)
            clockIn(android, endOfWeek)
            clockOut(android, endOfWeek + 10.minutes)
            val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
            val expected = listOf(
                timeReportWeek(
                    startOfWeek,
                    listOf(
                        timeReportDay(
                            endOfWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(2)
                                    builder.start = endOfWeek
                                    builder.stop = endOfWeek + 10.minutes
                                }
                            )
                        ),
                        timeReportDay(
                            startOfWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(1)
                                    builder.start = startOfWeek
                                    builder.stop = startOfWeek + 10.minutes
                                }
                            )
                        )
                    )
                )
            )

            val actual = repository.findNotRegisteredWeeks(android, loadRange)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `find not registered weeks with time intervals during three weeks over new year`() {
        runBlocking {
            val endOfFirstWeek = Milliseconds(1577606247000) // 2019-12-29 07:57:27
            val firstInSecondWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
            val secondInSecondWeek = Milliseconds(1577779099000) // 2019-12-31 07:58:19
            val thirdInSecondWeek = Milliseconds(1577985643000) // 2020-01-02 17:20:43
            val fourthInSecondWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
            val startOfThirdWeek = Milliseconds(1578297584000) // 2020-01-06 07:59:44
            clockIn(android, endOfFirstWeek)
            clockOut(android, endOfFirstWeek + 10.minutes)
            clockIn(android, firstInSecondWeek)
            clockOut(android, firstInSecondWeek + 10.minutes)
            clockIn(android, secondInSecondWeek)
            clockOut(android, secondInSecondWeek + 10.minutes)
            clockIn(android, thirdInSecondWeek)
            clockOut(android, thirdInSecondWeek + 10.minutes)
            clockIn(android, fourthInSecondWeek)
            clockOut(android, fourthInSecondWeek + 10.minutes)
            clockIn(android, startOfThirdWeek)
            clockOut(android, startOfThirdWeek + 10.minutes)
            val loadRange = LoadRange(LoadPosition(0), LoadSize(10))
            val expected = listOf(
                timeReportWeek(
                    startOfThirdWeek,
                    listOf(
                        timeReportDay(
                            startOfThirdWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(6)
                                    builder.start = startOfThirdWeek
                                    builder.stop = startOfThirdWeek + 10.minutes
                                }
                            )
                        )
                    )
                ),
                timeReportWeek(
                    firstInSecondWeek,
                    listOf(
                        timeReportDay(
                            fourthInSecondWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(5)
                                    builder.start = fourthInSecondWeek
                                    builder.stop = fourthInSecondWeek + 10.minutes
                                }
                            )
                        ),
                        timeReportDay(
                            thirdInSecondWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(4)
                                    builder.start = thirdInSecondWeek
                                    builder.stop = thirdInSecondWeek + 10.minutes
                                }
                            )
                        ),
                        timeReportDay(
                            secondInSecondWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(3)
                                    builder.start = secondInSecondWeek
                                    builder.stop = secondInSecondWeek + 10.minutes
                                }
                            )
                        ),
                        timeReportDay(
                            firstInSecondWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(2)
                                    builder.start = firstInSecondWeek
                                    builder.stop = firstInSecondWeek + 10.minutes
                                }
                            )
                        )
                    )
                ),
                timeReportWeek(
                    endOfFirstWeek,
                    listOf(
                        timeReportDay(
                            endOfFirstWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(1)
                                    builder.start = endOfFirstWeek
                                    builder.stop = endOfFirstWeek + 10.minutes
                                }
                            )
                        )
                    )
                )
            )

            val actual = repository.findNotRegisteredWeeks(android, loadRange)

            assertEquals(expected, actual)
        }
    }
}
