/*
 * Copyright (C) 2022 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.timereport.usecase

import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.InMemoryKeyValueStore
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.ios
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timeinterval.usecase.MarkRegisteredTime
import me.raatiniemi.worker.domain.timereport.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CountTimeReportWeeksTest {
    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut
    private lateinit var markRegisteredTime: MarkRegisteredTime

    private lateinit var keyValueStore: KeyValueStore
    private lateinit var timeReportRepository: TimeReportRepository

    private lateinit var countTimeReportWeeks: CountTimeReportWeeks

    @Before
    fun setUp() {
        val timeIntervalRepository = TimeIntervalInMemoryRepository()
        clockIn = ClockIn(timeIntervalRepository)
        clockOut = ClockOut(timeIntervalRepository)
        markRegisteredTime = MarkRegisteredTime(timeIntervalRepository)

        keyValueStore = InMemoryKeyValueStore()
        timeReportRepository = TimeReportInMemoryRepository(timeIntervalRepository)

        countTimeReportWeeks = CountTimeReportWeeks(keyValueStore, timeReportRepository)
    }

    // When not hiding registered time

    @Test
    fun `count time report weeks without time intervals`() = runBlocking {
        val expected = 0

        val actual = countTimeReportWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time report weeks without time interval for project`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        val expected = 0

        val actual = countTimeReportWeeks(ios)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time report weeks with time interval`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        val expected = 1

        val actual = countTimeReportWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time report weeks with time intervals`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, startOfDay + 30.minutes)
        val expected = 1

        val actual = countTimeReportWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time report weeks with time intervals within same week`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val endOfWeek = setToEndOfWeek(startOfDay) - 30.minutes
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        val expected = 1

        val actual = countTimeReportWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time report weeks with time intervals in different weeks`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = startOfWeek + 2.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        val expected = 2

        val actual = countTimeReportWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time report weeks with registered time interval`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        val expected = 1

        val actual = countTimeReportWeeks(android)

        assertEquals(expected, actual)
    }

    // When hiding registered time

    @Test
    fun `count time report weeks when hiding registered time without time intervals`() {
        runBlocking {
            keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
            val expected = 0

            val actual = countTimeReportWeeks(android)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `count time report weeks when hiding registered time without time interval for project`() =
        runBlocking {
            keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
            val startOfDay = setToStartOfDay(Milliseconds.now)
            clockIn(android, startOfDay)
            clockOut(android, startOfDay + 10.minutes)
            val expected = 0

            val actual = countTimeReportWeeks(ios)

            assertEquals(expected, actual)
        }

    @Test
    fun `count time report weeks when hiding registered time with time interval`() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        val expected = 1

        val actual = countTimeReportWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time report weeks when hiding registered time with time intervals`() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, startOfDay + 30.minutes)
        val expected = 1

        val actual = countTimeReportWeeks(android)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time report weeks when hiding registered time with time intervals within same week`() {
        runBlocking {
            keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
            val startOfDay = setToStartOfDay(Milliseconds.now)
            val startOfWeek = setToStartOfWeek(startOfDay)
            val endOfWeek = setToEndOfWeek(startOfDay) - 30.minutes
            clockIn(android, startOfWeek)
            clockOut(android, startOfWeek + 10.minutes)
            clockIn(android, endOfWeek)
            clockOut(android, endOfWeek + 10.minutes)
            val expected = 1

            val actual = countTimeReportWeeks(android)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `count time report weeks when hiding registered time with time intervals in different weeks`() {
        runBlocking {
            keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
            val startOfDay = setToStartOfDay(Milliseconds.now)
            val startOfWeek = setToStartOfWeek(startOfDay)
            val nextWeek = startOfWeek + 2.weeks
            clockIn(android, startOfWeek)
            clockOut(android, startOfWeek + 10.minutes)
            clockIn(android, nextWeek)
            clockOut(android, nextWeek + 10.minutes)
            val expected = 2

            val actual = countTimeReportWeeks(android)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `count time report weeks when hiding registered time with registered time interval`() {
        runBlocking {
            keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
            val startOfDay = setToStartOfDay(Milliseconds.now)
            val startOfWeek = setToStartOfWeek(startOfDay)
            clockIn(android, startOfWeek)
            clockOut(android, startOfWeek + 10.minutes)
                .also { timeInterval ->
                    markRegisteredTime(listOf(timeInterval))
                }
            val expected = 0

            val actual = countTimeReportWeeks(android)

            assertEquals(expected, actual)
        }
    }
}
