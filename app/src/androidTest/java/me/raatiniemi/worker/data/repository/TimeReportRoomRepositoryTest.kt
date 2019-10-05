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

package me.raatiniemi.worker.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.data.Database
import me.raatiniemi.worker.data.projects.*
import me.raatiniemi.worker.domain.date.plus
import me.raatiniemi.worker.domain.model.LoadPosition
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.LoadSize
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.ios
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timeinterval.usecase.MarkRegisteredTime
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

private val timeZone = TimeZone.getTimeZone("UTC")

@RunWith(AndroidJUnit4::class)
class TimeReportRoomRepositoryTest {
    private lateinit var database: Database
    private lateinit var timeReport: TimeReportDao
    private lateinit var timeIntervals: TimeIntervalDao

    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut
    private lateinit var markRegisteredTime: MarkRegisteredTime

    private lateinit var repository: TimeReportRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, Database::class.java)
            .allowMainThreadQueries()
            .build()

        database.projects()
            .add(
                projectEntity {
                    id = android.id.value
                    name = android.name.value
                }
            )
        timeReport = database.timeReport()
        timeIntervals = database.timeIntervals()

        val timeIntervalRepository = TimeIntervalRoomRepository(database.timeIntervals())
        clockIn = ClockIn(timeIntervalRepository)
        clockOut = ClockOut(timeIntervalRepository)
        markRegisteredTime = MarkRegisteredTime(timeIntervalRepository)

        repository = TimeReportRoomRepository(timeReport, timeIntervals)
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Count weeks

    @Test
    fun countWeeks_withoutTimeIntervals() {
        val expected = 0

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withoutTimeIntervalForProject() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        val expected = 0

        val actual = repository.countWeeks(ios)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withTimeIntervals() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, date = Date(startOfDay.value) + 20.minutes)
        clockOut(android, date = Date(startOfDay.value) + 30.minutes)
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withTimeIntervalsWithinSameWeek() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val endOfWeek = setToEndOfWeek(startOfDay, timeZone)
        clockIn(android, date = Date(startOfWeek.value))
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, date = Date(endOfWeek.value))
        clockOut(android, date = Date(endOfWeek.value) + 10.minutes)
        val expected = 1

        val actual = repository.countWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withTimeIntervalsInDifferentWeeks() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
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
    fun countWeeks_withRegisteredTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
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
    fun countNotRegisteredWeeks_withoutTimeIntervals() {
        val expected = 0

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withoutTimeIntervalForProject() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        val expected = 0

        val actual = repository.countNotRegisteredWeeks(ios)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        val expected = 1

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withTimeIntervals() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, date = Date(startOfDay.value) + 20.minutes)
        clockOut(android, date = Date(startOfDay.value) + 30.minutes)
        val expected = 1

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withTimeIntervalsWithinSameWeek() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val endOfWeek = setToEndOfWeek(startOfDay, timeZone)
        clockIn(android, date = Date(startOfWeek.value))
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, date = Date(endOfWeek.value))
        clockOut(android, date = Date(endOfWeek.value) + 10.minutes)
        val expected = 1

        val actual = repository.countNotRegisteredWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withTimeIntervalsInDifferentWeeks() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
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
    fun countNotRegisteredWeeks_withRegisteredTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
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
    fun count_withoutTimeIntervals() {
        val expected = 0

        val actual = repository.count(android)

        assertEquals(expected, actual)
    }

    @Test
    fun count_withTimeInterval() {
        val expected = 1
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )

        val actual = repository.count(android)

        assertEquals(expected, actual)
    }

    @Test
    fun count_withTimeIntervalsOnSameDay() {
        val expected = 1
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )

        val actual = repository.count(android)

        assertEquals(expected, actual)
    }

    @Test
    fun count_withTimeIntervalsOnDifferentDays() {
        val expected = 2
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = Date().time
            }
        )

        val actual = repository.count(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegistered_withoutTimeIntervals() {
        val expected = 0

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegistered_withRegisteredTimeInterval() {
        val expected = 0
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
                registered = true
            }
        )

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegistered_withTimeInterval() {
        val expected = 1
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegistered_withTimeIntervalsOnSameDay() {
        val expected = 1
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegistered_withRegisteredTimeIntervalOnDifferentDays() {
        val expected = 1
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = Date().time
                registered = true
            }
        )

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegistered_withTimeIntervalsOnDifferentDays() {
        val expected = 2
        timeIntervals.add(
            timeIntervalEntity {
                projectId = android.id.value
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = Date().time
            }
        )

        val actual = repository.countNotRegistered(android)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withoutTimeIntervals() {
        val expected = emptyList<TimeReportDay>()
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withoutTimeIntervalForProject() {
        database.projects().add(
            projectEntity {
                id = ios.id.value
                name = ios.name.value
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                projectId = ios.id.value
                startInMilliseconds = 1
                stopInMilliseconds = 10
            }
        )
        val expected = emptyList<TimeReportDay>()
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findAll(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalsForSameDay() {
        val firstTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val secondTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 11
            stopInMilliseconds = 30
        }
        timeIntervals.add(firstTimeInterval)
        timeIntervals.add(secondTimeInterval)
        val timeIntervals = listOf(
            timeInterval(secondTimeInterval.copy(id = 2)),
            timeInterval(firstTimeInterval.copy(id = 1))
        )
        val expected = listOf(
            timeReportDay(
                Date(firstTimeInterval.startInMilliseconds),
                timeIntervals
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
    fun findAll_withTimeIntervalsForDifferentDays() {
        val firstTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val secondTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        timeIntervals.add(firstTimeInterval)
        timeIntervals.add(secondTimeInterval)
        val expected = listOf(
            timeReportDay(
                Date(secondTimeInterval.startInMilliseconds),
                listOf(timeInterval(secondTimeInterval.copy(id = 2)))
            ),
            timeReportDay(
                Date(firstTimeInterval.startInMilliseconds),
                listOf(timeInterval(firstTimeInterval.copy(id = 1)))
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
    fun findAll_withTimeIntervalsWithPosition() {
        val firstTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val secondTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        timeIntervals.add(firstTimeInterval)
        timeIntervals.add(secondTimeInterval)
        val expected = listOf(
            timeReportDay(
                Date(firstTimeInterval.startInMilliseconds),
                listOf(timeInterval(firstTimeInterval.copy(id = 1)))
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
    fun findAll_withTimeIntervalsWithPageSize() {
        val firstTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val secondTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        timeIntervals.add(firstTimeInterval)
        timeIntervals.add(secondTimeInterval)
        val expected = listOf(
            timeReportDay(
                Date(secondTimeInterval.startInMilliseconds),
                listOf(timeInterval(secondTimeInterval.copy(id = 2)))
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
    fun findNotRegistered_withoutTimeIntervals() {
        val expected = emptyList<TimeReportDay>()
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findNotRegistered(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegistered_withoutTimeIntervalForProject() {
        database.projects().add(
            projectEntity {
                id = ios.id.value
                name = ios.name.value
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                projectId = ios.id.value
                startInMilliseconds = 1
                stopInMilliseconds = 10
            }
        )
        val expected = emptyList<TimeReportDay>()
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findNotRegistered(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegistered_withTimeIntervalsForSameDay() {
        val firstTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val secondTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 11
            stopInMilliseconds = 30
        }
        timeIntervals.add(firstTimeInterval)
        timeIntervals.add(secondTimeInterval)
        val expected = listOf(
            timeReportDay(
                Date(firstTimeInterval.startInMilliseconds),
                listOf(
                    timeInterval(secondTimeInterval.copy(id = 2)),
                    timeInterval(firstTimeInterval.copy(id = 1))
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
    fun findNotRegistered_withTimeIntervalsForDifferentDays() {
        val firstTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val secondTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        timeIntervals.add(firstTimeInterval)
        timeIntervals.add(secondTimeInterval)
        val expected = listOf(
            timeReportDay(
                Date(secondTimeInterval.startInMilliseconds),
                listOf(
                    timeInterval(secondTimeInterval.copy(id = 2))
                )
            ),
            timeReportDay(
                Date(firstTimeInterval.startInMilliseconds),
                listOf(
                    timeInterval(firstTimeInterval.copy(id = 1))
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
    fun findNotRegistered_withTimeIntervalsWithPosition() {
        val firstTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val secondTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        timeIntervals.add(firstTimeInterval)
        timeIntervals.add(secondTimeInterval)
        val expected = listOf(
            timeReportDay(
                Date(firstTimeInterval.startInMilliseconds),
                listOf(timeInterval(firstTimeInterval.copy(id = 1)))
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
    fun findNotRegistered_withTimeIntervalsWithPageSize() {
        val firstTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val secondTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        timeIntervals.add(firstTimeInterval)
        timeIntervals.add(secondTimeInterval)
        val expected = listOf(
            timeReportDay(
                Date(secondTimeInterval.startInMilliseconds),
                listOf(timeInterval(secondTimeInterval.copy(id = 2)))
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
    fun findNotRegistered_withRegisteredTimeInterval() {
        val firstTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 1
            stopInMilliseconds = 10
            registered = true
        }
        val secondTimeInterval = timeIntervalEntity {
            projectId = android.id.value
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        timeIntervals.add(firstTimeInterval)
        timeIntervals.add(secondTimeInterval)
        val expected = listOf(
            timeReportDay(
                Date(secondTimeInterval.startInMilliseconds),
                listOf(timeInterval(secondTimeInterval.copy(id = 2)))
            )
        )
        val loadRange = LoadRange(
            LoadPosition(0),
            LoadSize(10)
        )

        val actual = repository.findNotRegistered(android, loadRange)

        assertEquals(expected, actual)
    }
}
