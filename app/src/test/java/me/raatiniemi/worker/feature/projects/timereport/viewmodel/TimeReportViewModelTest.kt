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

package me.raatiniemi.worker.feature.projects.timereport.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.model.LoadPosition
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.LoadSize
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.usecase.FindTimeReportWeeks
import me.raatiniemi.worker.domain.timereport.usecase.groupByWeek
import me.raatiniemi.worker.feature.projects.model.ProjectHolder
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportLongPressAction
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportTapAction
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.feature.shared.model.observeNoValue
import me.raatiniemi.worker.feature.shared.model.observeNonNull
import me.raatiniemi.worker.koin.testKoinModules
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.InMemoryUsageAnalytics
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@RunWith(JUnit4::class)
class TimeReportViewModelTest : AutoCloseKoinTest() {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val usageAnalytics by inject<InMemoryUsageAnalytics>()
    private val projectHolder by inject<ProjectHolder>()

    private val clockIn by inject<ClockIn>()
    private val clockOut by inject<ClockOut>()
    private val findTimeReportWeeks by inject<FindTimeReportWeeks>()

    private val vm by inject<TimeReportViewModel>()

    @Before
    fun setUp() {
        startKoin {
            modules(testKoinModules)
        }
    }

    // Toggle registered state

    @Test
    fun `toggle registered state with selected item`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        val timeInterval = clockOut(android, startOfDay + 1.hours)
        projectHolder += android
        val expected = groupByWeek(
            timeInterval(timeInterval) { builder ->
                builder.isRegistered = true
            }
        )
        val expectedEvents = listOf(
            Event.TimeReportToggle(1)
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(timeInterval))
        vm.toggleRegisteredStateForSelectedItems()

        val actual = findTimeReportWeeks(
            android,
            LoadRange(LoadPosition(0), LoadSize(10))
        )
        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle registered state for selected items`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        val firstTimeInterval = clockOut(android, startOfDay + 1.hours)
        clockIn(android, startOfDay + 2.hours)
        val secondTimeInterval = clockOut(android, startOfDay + 3.hours)
        projectHolder += android
        val expected = groupByWeek(listOf(
            timeInterval(firstTimeInterval) { builder ->
                builder.isRegistered = true
            },
            timeInterval(secondTimeInterval) { builder ->
                builder.isRegistered = true
            }
        )
        )
        val expectedEvents = listOf(Event.TimeReportToggle(2))

        vm.consume(TimeReportLongPressAction.LongPressItem(firstTimeInterval))
        vm.consume(TimeReportTapAction.TapItem(secondTimeInterval))
        vm.toggleRegisteredStateForSelectedItems()

        val actual = findTimeReportWeeks(
            android,
            LoadRange(LoadPosition(0), LoadSize(10))
        )
        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle registered state for active time interval`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeInterval = clockIn(android, startOfDay)
        projectHolder += android
        vm.consume(TimeReportLongPressAction.LongPressItem(timeInterval))

        vm.toggleRegisteredStateForSelectedItems()

        vm.viewActions.observeNonNull {
            assertEquals(
                TimeReportViewActions.ShowUnableToMarkActiveTimeIntervalsAsRegisteredErrorMessage,
                it
            )
        }
    }

    // Remove

    @Test
    fun `remove with active item`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeInterval = clockIn(android, startOfDay)
        projectHolder += android
        val expected = emptyList<TimeReportWeek>()
        val expectedEvents = listOf(
            Event.TimeReportRemove(1)
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(timeInterval))
        vm.removeSelectedItems()

        val actual = findTimeReportWeeks(
            android,
            LoadRange(LoadPosition(0), LoadSize(10))
        )
        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with inactive item`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        val timeInterval = clockOut(android, startOfDay + 1.hours)
        projectHolder += android
        val expected = emptyList<TimeReportWeek>()
        val expectedEvents = listOf(
            Event.TimeReportRemove(1)
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(timeInterval))
        vm.removeSelectedItems()

        val actual = findTimeReportWeeks(
            android,
            LoadRange(LoadPosition(0), LoadSize(10))
        )
        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with multiple items`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        val firstTimeInterval = clockOut(android, startOfDay + 1.hours)
        clockIn(android, startOfDay + 2.hours)
        val secondTimeInterval = clockOut(android, startOfDay + 3.hours)
        projectHolder += android
        val expected = emptyList<TimeReportWeek>()
        val expectedEvents = listOf(
            Event.TimeReportRemove(2)
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(firstTimeInterval))
        vm.consume(TimeReportTapAction.TapItem(secondTimeInterval))
        vm.removeSelectedItems()

        val actual = findTimeReportWeeks(
            android,
            LoadRange(LoadPosition(0), LoadSize(10))
        )
        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    // Refresh active time report week

    @Test
    fun `refresh active time report week without weeks`() = runBlocking {
        val weeks = emptyList<TimeReportWeek>()
        projectHolder += android

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active time report week without active week`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        val timeInterval = clockOut(android, startOfDay + 1.hours)
        val weeks = groupByWeek(timeInterval)
        projectHolder += android

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active time report week with week`() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeInterval = clockIn(android, startOfDay)
        val weeks = groupByWeek(timeInterval)
        projectHolder += android

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNonNull {
            assertEquals(TimeReportViewActions.RefreshTimeReportWeek(0), it)
        }
    }

    @Test
    fun `refresh active time report week with days`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextDay = startOfWeek + 1.days
        clockIn(android, startOfWeek)
        val firstTimeInterval = clockOut(android, startOfWeek + 1.hours)
        val secondTimeInterval = clockIn(android, nextDay)
        val weeks = groupByWeek(
            listOf(
                firstTimeInterval,
                secondTimeInterval
            )
        )
        projectHolder += android

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNonNull {
            assertEquals(TimeReportViewActions.RefreshTimeReportWeek(0), it)
        }
    }

    @Test
    fun `refresh active time report week with weeks`() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, nextWeek)
        val secondTimeInterval = clockOut(android, nextWeek + 1.hours)
        val firstTimeInterval = clockIn(android, startOfWeek)
        val weeks = groupByWeek(
            listOf(
                firstTimeInterval,
                secondTimeInterval
            )
        )
        projectHolder += android

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNonNull {
            assertEquals(TimeReportViewActions.RefreshTimeReportWeek(1), it)
        }
    }
}
