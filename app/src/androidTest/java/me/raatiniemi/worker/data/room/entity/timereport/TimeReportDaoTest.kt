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
import kotlinx.coroutines.runBlocking
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

        runBlocking {
            val projects = get<ProjectRepository>()
            projects.add(NewProject(android.name))
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Count weeks

    @Test
    fun countWeeks_withoutTimeIntervals() = runBlocking {
        val expected = 0

        val actual = timeReport.countWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countWeeks_withoutTimeIntervalForProject() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun countWeeks_withTimeInterval() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun countWeeks_withTimeIntervals() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun countWeeks_withTimeIntervalsWithinSameWeek() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val endOfWeek = setToEndOfWeek(startOfDay)
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
    fun countWeeks_withTimeIntervalsInDifferentWeeks() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = setToEndOfWeek(startOfDay) + 1.weeks
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
    fun countWeeks_withRegisteredTimeInterval() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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

    @Test
    fun countWeeks_withTimeIntervalsUsingFixedValuesWithinSameWeek() = runBlocking {
        val startOfWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val endOfWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
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
    fun countWeeks_withTimeIntervalsDuringThreeWeeksOverNewYear() = runBlocking {
        val endOfFirstWeek = Milliseconds(1577606247000) // 2019-12-29 07:57:27
        val firstInSecondWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val secondInSecondWeek = Milliseconds(1577779099000) // 2019-12-31 07:58:19
        val thirdInSecondWeek = Milliseconds(1577985643000) // 2020-01-02 17:20:43
        val fourthInSecondWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
        val startOfThirdWeek = Milliseconds(1578297584000) // 2020-01-06 07:59:44
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = endOfFirstWeek.value
                stopInMilliseconds = endOfFirstWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = firstInSecondWeek.value
                stopInMilliseconds = firstInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = secondInSecondWeek.value
                stopInMilliseconds = secondInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = thirdInSecondWeek.value
                stopInMilliseconds = thirdInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = fourthInSecondWeek.value
                stopInMilliseconds = fourthInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfThirdWeek.value
                stopInMilliseconds = startOfThirdWeek.value + 10.minutes
            }
        )
        val expected = 3

        val actual = timeReport.countWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    // Count not registered weeks

    @Test
    fun countNotRegisteredWeeks_withoutTimeIntervals() = runBlocking {
        val expected = 0

        val actual = timeReport.countNotRegisteredWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegisteredWeeks_withoutTimeIntervalForProject() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun countNotRegisteredWeeks_withTimeInterval() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun countNotRegisteredWeeks_withTimeIntervals() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun countNotRegisteredWeeks_withTimeIntervalsWithinSameWeek() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val endOfWeek = setToEndOfWeek(startOfDay)
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
    fun countNotRegisteredWeeks_withTimeIntervalsInDifferentWeeks() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = setToEndOfWeek(startOfDay) + 1.weeks
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
    fun countNotRegisteredWeeks_withRegisteredTimeInterval() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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

    @Test
    fun countNotRegisteredWeeks_withTimeIntervalsUsingFixedValuesWithinSameWeek() = runBlocking {
        val startOfWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val endOfWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
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
    fun countNotRegisteredWeeks_withTimeIntervalsDuringThreeWeeksOverNewYear() = runBlocking {
        val endOfFirstWeek = Milliseconds(1577606247000) // 2019-12-29 07:57:27
        val firstInSecondWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val secondInSecondWeek = Milliseconds(1577779099000) // 2019-12-31 07:58:19
        val thirdInSecondWeek = Milliseconds(1577985643000) // 2020-01-02 17:20:43
        val fourthInSecondWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
        val startOfThirdWeek = Milliseconds(1578297584000) // 2020-01-06 07:59:44
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = endOfFirstWeek.value
                stopInMilliseconds = endOfFirstWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = firstInSecondWeek.value
                stopInMilliseconds = firstInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = secondInSecondWeek.value
                stopInMilliseconds = secondInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = thirdInSecondWeek.value
                stopInMilliseconds = thirdInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = fourthInSecondWeek.value
                stopInMilliseconds = fourthInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfThirdWeek.value
                stopInMilliseconds = startOfThirdWeek.value + 10.minutes
            }
        )
        val expected = 3

        val actual = timeReport.countNotRegisteredWeeks(android.id.value)

        assertEquals(expected, actual)
    }

    // Find weeks

    @Test
    fun findWeeks_withoutTimeIntervals() = runBlocking {
        val expected = emptyList<TimeReportQueryGroup>()

        val actual = timeReport.findWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findWeeks_withoutTimeIntervalForProject() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun findWeeks_withTimeInterval() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun findWeeks_withTimeIntervals() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun findWeeks_withTimeIntervalWithinSameWeek() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val endOfWeek = setToEndOfWeek(startOfDay)
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
    fun findWeeks_withTimeIntervalInDifferentWeeks() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
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
    fun findWeeks_withRegisteredTimeInterval() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun findWeeks_whenExcludingByLoadPosition() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = setToEndOfWeek(startOfDay) + 1.weeks
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
    fun findWeeks_whenExcludingByLoadSize() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = setToEndOfWeek(startOfDay) + 1.weeks
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

    @Test
    fun findWeeks_withTimeIntervalsUsingFixedValuesWithinSameWeek() = runBlocking {
        val startOfWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val endOfWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
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
    fun findWeeks_withTimeIntervalsDuringThreeWeeksOverNewYear() = runBlocking {
        val endOfFirstWeek = Milliseconds(1577606247000) // 2019-12-29 07:57:27
        val firstInSecondWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val secondInSecondWeek = Milliseconds(1577779099000) // 2019-12-31 07:58:19
        val thirdInSecondWeek = Milliseconds(1577985643000) // 2020-01-02 17:20:43
        val fourthInSecondWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
        val startOfThirdWeek = Milliseconds(1578297584000) // 2020-01-06 07:59:44
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = endOfFirstWeek.value
                stopInMilliseconds = endOfFirstWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = firstInSecondWeek.value
                stopInMilliseconds = firstInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = secondInSecondWeek.value
                stopInMilliseconds = secondInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = thirdInSecondWeek.value
                stopInMilliseconds = thirdInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = fourthInSecondWeek.value
                stopInMilliseconds = fourthInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfThirdWeek.value
                stopInMilliseconds = startOfThirdWeek.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(startOfThirdWeek.value, "6"),
            TimeReportQueryGroup(firstInSecondWeek.value, "2,3,4,5"),
            TimeReportQueryGroup(endOfFirstWeek.value, "1")
        )

        val actual = timeReport.findWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    // Find not registered weeks

    @Test
    fun findNotRegisteredWeeks_withoutTimeIntervals() = runBlocking {
        val expected = emptyList<TimeReportQueryGroup>()

        val actual = timeReport.findNotRegisteredWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegisteredWeeks_withoutTimeIntervalForProject() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun findNotRegisteredWeeks_withTimeInterval() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun findNotRegisteredWeeks_withTimeIntervals() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun findNotRegisteredWeeks_withTimeIntervalWithinSameWeek() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val endOfWeek = setToEndOfWeek(startOfDay)
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
    fun findNotRegisteredWeeks_withTimeIntervalInDifferentWeeks() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = setToEndOfWeek(startOfDay) + 1.weeks
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
    fun findNotRegisteredWeeks_withRegisteredTimeInterval() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
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
    fun findNotRegisteredWeeks_whenExcludingByLoadPosition() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = setToEndOfWeek(startOfDay) + 1.weeks
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
    fun findNotRegisteredWeeks_whenExcludingByLoadSize() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = setToEndOfWeek(startOfDay) + 1.weeks
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

    @Test
    fun findNotRegisteredWeeks_withTimeIntervalsUsingFixedValuesWithinSameWeek() = runBlocking {
        val startOfWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val endOfWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
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
    fun findNotRegisteredWeeks_withTimeIntervalsDuringThreeWeeksOverNewYear() = runBlocking {
        val endOfFirstWeek = Milliseconds(1577606247000) // 2019-12-29 07:57:27
        val firstInSecondWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val secondInSecondWeek = Milliseconds(1577779099000) // 2019-12-31 07:58:19
        val thirdInSecondWeek = Milliseconds(1577985643000) // 2020-01-02 17:20:43
        val fourthInSecondWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
        val startOfThirdWeek = Milliseconds(1578297584000) // 2020-01-06 07:59:44
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = endOfFirstWeek.value
                stopInMilliseconds = endOfFirstWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = firstInSecondWeek.value
                stopInMilliseconds = firstInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = secondInSecondWeek.value
                stopInMilliseconds = secondInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = thirdInSecondWeek.value
                stopInMilliseconds = thirdInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = fourthInSecondWeek.value
                stopInMilliseconds = fourthInSecondWeek.value + 10.minutes
            }
        )
        timeIntervals.add(
            timeIntervalEntity {
                startInMilliseconds = startOfThirdWeek.value
                stopInMilliseconds = startOfThirdWeek.value + 10.minutes
            }
        )
        val expected = listOf(
            TimeReportQueryGroup(startOfThirdWeek.value, "6"),
            TimeReportQueryGroup(firstInSecondWeek.value, "2,3,4,5"),
            TimeReportQueryGroup(endOfFirstWeek.value, "1")
        )

        val actual = timeReport.findNotRegisteredWeeks(android.id.value, 0, 10)

        assertEquals(expected, actual)
    }
}
