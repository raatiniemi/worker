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

package me.raatiniemi.worker.data.room.entity.timereport

import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.data.room.Database
import me.raatiniemi.worker.data.room.entity.timeinterval.TimeIntervalDao
import me.raatiniemi.worker.data.room.entity.timeinterval.timeIntervalEntity
import me.raatiniemi.worker.domain.project.model.NewProject
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.ios
import me.raatiniemi.worker.domain.project.repository.ProjectRepository
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.koin.androidTestKoinModules
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.koin.test.inject
import java.util.*

private val timeZone = TimeZone.getTimeZone("UTC")

@RunWith(AndroidJUnit4::class)
class TimeReportDaoTest : AutoCloseKoinTest() {
    private val database by inject<Database>()
    private val timeIntervals: TimeIntervalDao by lazy {
        database.timeIntervals()
    }

    private val timeReport: TimeReportDao
        get() = database.timeReport()

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            loadKoinModules(androidTestKoinModules)
        }

        val projects = get<ProjectRepository>()
        projects.add(NewProject(android.name))
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Count weeks

    @Test
    fun countWeeks_withoutTimeIntervals() {
        val expected = 0

        val actual = timeReport.countWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withoutTimeIntervalForProject() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        val expected = 0

        val actual = timeReport.countWeeks(ios.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        val expected = 1

        val actual = timeReport.countWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withTimeIntervals() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value + 20.minutes
                stopInMilliseconds = startOfDay.value + 30.minutes
            }
        )
        val expected = 1

        val actual = timeReport.countWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withTimeIntervalsWithinSameWeek() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val endOfWeek = setToEndOfWeek(startOfDay, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = endOfWeek.value
                stopInMilliseconds = endOfWeek.value + 10.minutes
            }
        )
        val expected = 1

        val actual = timeReport.countWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withTimeIntervalsInDifferentWeeks() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val nextWeek = setToEndOfWeek(startOfDay, timeZone) + 1.weeks
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = nextWeek.value
                startInMilliseconds = nextWeek.value + 10.minutes
            }
        )
        val expected = 2

        val actual = timeReport.countWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withRegisteredTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
                registered = true
            }
        )
        val expected = 1

        val actual = timeReport.countWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    // Count not registered weeks

    @Test
    fun countNotRegisteredWeeks_withoutTimeIntervals() {
        val expected = 0

        val actual = timeReport.countNotRegisteredWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withoutTimeIntervalForProject() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        val expected = 0

        val actual = timeReport.countNotRegisteredWeeks(ios.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        val expected = 1

        val actual = timeReport.countNotRegisteredWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withTimeIntervals() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value + 20.minutes
                stopInMilliseconds = startOfDay.value + 30.minutes
            }
        )
        val expected = 1

        val actual = timeReport.countNotRegisteredWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withTimeIntervalsWithinSameWeek() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val endOfWeek = setToEndOfWeek(startOfDay, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = endOfWeek.value
                stopInMilliseconds = endOfWeek.value + 10.minutes
            }
        )
        val expected = 1

        val actual = timeReport.countNotRegisteredWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withTimeIntervalsInDifferentWeeks() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val nextWeek = setToEndOfWeek(startOfDay, timeZone) + 1.weeks
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = nextWeek.value
                startInMilliseconds = nextWeek.value + 10.minutes
            }
        )
        val expected = 2

        val actual = timeReport.countNotRegisteredWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withRegisteredTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
                registered = true
            }
        )
        val expected = 0

        val actual = timeReport.countNotRegisteredWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    // Find weeks

    @Test
    fun findWeeks_withoutTimeIntervals() {
        val expected = emptyList<TimeReportQueryGroup>()

        val actual = timeReport.findWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findWeeks_withoutTimeIntervalForProject() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        val expected = emptyList<TimeReportQueryGroup>()

        val actual = timeReport.findWeeks(ios.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findWeeks_withTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(startOfDay.value, "1")
        )

        val actual = timeReport.findWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findWeeks_withTimeIntervals() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value + 20.minutes
                stopInMilliseconds = startOfDay.value + 30.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(startOfDay.value, "1,2")
        )

        val actual = timeReport.findWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findWeeks_withTimeIntervalWithinSameWeek() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val endOfWeek = setToEndOfWeek(startOfDay, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = endOfWeek.value
                stopInMilliseconds = endOfWeek.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(startOfWeek.value, "1,2")
        )

        val actual = timeReport.findWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findWeeks_withTimeIntervalInDifferentWeeks() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val nextWeek = startOfWeek + 1.weeks
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = nextWeek.value
                stopInMilliseconds = nextWeek.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(nextWeek.value, "2"),
            TimeReportQueryGroup(startOfWeek.value, "1")
        )

        val actual = timeReport.findWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findWeeks_withRegisteredTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
                registered = true
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(startOfDay.value, "1")
        )

        val actual = timeReport.findWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findWeeks_whenExcludingByLoadPosition() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val nextWeek = setToEndOfWeek(startOfDay, timeZone) + 1.weeks
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value + 20.minutes
                stopInMilliseconds = startOfWeek.value + 30.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = nextWeek.value
                stopInMilliseconds = nextWeek.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(startOfWeek.value, "1,2")
        )

        val actual = timeReport.findWeeks(android.id.value, 1, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findWeeks_whenExcludingByLoadSize() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val nextWeek = setToEndOfWeek(startOfDay, timeZone) + 1.weeks
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value + 20.minutes
                stopInMilliseconds = startOfWeek.value + 30.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = nextWeek.value
                stopInMilliseconds = nextWeek.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(nextWeek.value, "3")
        )

        val actual = timeReport.findWeeks(android.id.value, 0, 1)

        assertEquals(expected, actual)
    }

    // Find not registered weeks

    @Test
    fun findNotRegisteredWeeks_withoutTimeIntervals() {
        val expected = emptyList<TimeReportQueryGroup>()

        val actual = timeReport.findNotRegisteredWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegisteredWeeks_withoutTimeIntervalForProject() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        val expected = emptyList<TimeReportQueryGroup>()

        val actual = timeReport.findNotRegisteredWeeks(ios.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegisteredWeeks_withTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(startOfDay.value, "1")
        )

        val actual = timeReport.findNotRegisteredWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegisteredWeeks_withTimeIntervals() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value + 20.minutes
                stopInMilliseconds = startOfDay.value + 30.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(startOfDay.value, "1,2")
        )

        val actual = timeReport.findNotRegisteredWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegisteredWeeks_withTimeIntervalWithinSameWeek() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val endOfWeek = setToEndOfWeek(startOfDay, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = endOfWeek.value
                stopInMilliseconds = endOfWeek.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(startOfWeek.value, "1,2")
        )

        val actual = timeReport.findNotRegisteredWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegisteredWeeks_withTimeIntervalInDifferentWeeks() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val nextWeek = setToEndOfWeek(startOfDay, timeZone) + 1.weeks
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = nextWeek.value
                stopInMilliseconds = nextWeek.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(nextWeek.value, "2"),
            TimeReportQueryGroup(startOfWeek.value, "1")
        )

        val actual = timeReport.findNotRegisteredWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegisteredWeeks_withRegisteredTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfDay.value
                stopInMilliseconds = startOfDay.value + 10.minutes
                registered = true
            }
        )
        val expected = emptyList<TimeReportQueryGroup>()

        val actual = timeReport.findNotRegisteredWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegisteredWeeks_whenExcludingByLoadPosition() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val nextWeek = setToEndOfWeek(startOfDay, timeZone) + 1.weeks
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value + 20.minutes
                stopInMilliseconds = startOfWeek.value + 30.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = nextWeek.value
                stopInMilliseconds = nextWeek.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(startOfWeek.value, "1,2")
        )

        val actual = timeReport.findNotRegisteredWeeks(android.id.value, 1, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegisteredWeeks_whenExcludingByLoadSize() {
        val startOfDay = setToStartOfDay(Milliseconds.now, timeZone)
        val startOfWeek = setToStartOfWeek(startOfDay, timeZone)
        val nextWeek = setToEndOfWeek(startOfDay, timeZone) + 1.weeks
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value
                stopInMilliseconds = startOfWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfWeek.value + 20.minutes
                stopInMilliseconds = startOfWeek.value + 30.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = nextWeek.value
                stopInMilliseconds = nextWeek.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(nextWeek.value, "3")
        )

        val actual = timeReport.findNotRegisteredWeeks(android.id.value, 0, 1)

        assertEquals(expected, actual)
    }
}
