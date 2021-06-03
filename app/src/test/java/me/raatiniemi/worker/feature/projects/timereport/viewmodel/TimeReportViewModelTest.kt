/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

    // Project name

    @Test
    fun `project name without project`() {
        vm.projectName.observeNoValue()
    }

    @Test
    fun `project name with project`() {
        projectHolder += android
        val expected = android.name.value

        vm.projectName.observeNonNull {
            assertEquals(expected, it)
        }
    }

    // Toggle registered state

    @Test
    fun `toggle registered state with selected item`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeInterval = runBlocking {
            clockIn(android, startOfDay)
            clockOut(android, startOfDay + 1.hours)
        }
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
        runBlocking {
            vm.toggleRegisteredStateForSelectedItems()
        }

        val actual = runBlocking {
            findTimeReportWeeks(
                android,
                LoadRange(LoadPosition(0), LoadSize(10))
            )
        }
        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle registered state for selected items`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val firstTimeInterval = runBlocking {
            clockIn(android, startOfDay)
            clockOut(android, startOfDay + 1.hours)
        }
        val secondTimeInterval = runBlocking {
            clockIn(android, startOfDay + 2.hours)
            clockOut(android, startOfDay + 3.hours)
        }
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
        runBlocking {
            vm.toggleRegisteredStateForSelectedItems()
        }

        val actual = runBlocking {
            findTimeReportWeeks(
                android,
                LoadRange(LoadPosition(0), LoadSize(10))
            )
        }
        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle registered state for active time interval`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeInterval = runBlocking {
            clockIn(android, startOfDay)
        }
        projectHolder += android
        val actual = mutableListOf<TimeReportViewActions>()
        vm.viewActions.observeForever(actual::add)
        val expected = listOf(
            TimeReportViewActions.ShowUnableToMarkActiveTimeIntervalsAsRegisteredErrorMessage
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(timeInterval))
        runBlocking {
            vm.toggleRegisteredStateForSelectedItems()
        }

        assertEquals(expected, actual)
    }

    // Remove

    @Test
    fun `remove with active item`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeInterval = runBlocking {
            clockIn(android, startOfDay)
        }
        projectHolder += android
        val expected = emptyList<TimeReportWeek>()
        val expectedEvents = listOf(
            Event.TimeReportRemove(1)
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(timeInterval))
        runBlocking {
            vm.removeSelectedItems()
        }

        val actual = runBlocking {
            findTimeReportWeeks(
                android,
                LoadRange(LoadPosition(0), LoadSize(10))
            )
        }
        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with inactive item`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeInterval = runBlocking {
            clockIn(android, startOfDay)
            clockOut(android, startOfDay + 1.hours)
        }
        projectHolder += android
        val expected = emptyList<TimeReportWeek>()
        val expectedEvents = listOf(
            Event.TimeReportRemove(1)
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(timeInterval))
        runBlocking {
            vm.removeSelectedItems()
        }

        val actual = runBlocking {
            findTimeReportWeeks(
                android,
                LoadRange(LoadPosition(0), LoadSize(10))
            )
        }
        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with multiple items`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val firstTimeInterval = runBlocking {
            clockIn(android, startOfDay)
            clockOut(android, startOfDay + 1.hours)
        }
        val secondTimeInterval = runBlocking {
            clockIn(android, startOfDay + 2.hours)
            clockOut(android, startOfDay + 3.hours)
        }
        projectHolder += android
        val expected = emptyList<TimeReportWeek>()
        val expectedEvents = listOf(
            Event.TimeReportRemove(2)
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(firstTimeInterval))
        vm.consume(TimeReportTapAction.TapItem(secondTimeInterval))
        runBlocking {
            vm.removeSelectedItems()
        }

        val actual = runBlocking {
            findTimeReportWeeks(
                android,
                LoadRange(LoadPosition(0), LoadSize(10))
            )
        }
        assertEquals(expectedEvents, usageAnalytics.events)
        assertEquals(expected, actual)
    }

    // Refresh active time report week

    @Test
    fun `refresh active time report week without weeks`() {
        val weeks = emptyList<TimeReportWeek>()
        projectHolder += android

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active time report week without active week`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeInterval = runBlocking {
            clockIn(android, startOfDay)
            clockOut(android, startOfDay + 1.hours)
        }
        val weeks = groupByWeek(timeInterval)
        projectHolder += android

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active time report week with week`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeInterval = runBlocking {
            clockIn(android, startOfDay)
        }
        val weeks = groupByWeek(timeInterval)
        projectHolder += android
        val actual = mutableListOf<TimeReportViewActions>()
        vm.viewActions.observeForever(actual::add)
        val expected = listOf(
            TimeReportViewActions.RefreshTimeReportWeek(0)
        )

        vm.refreshActiveTimeReportWeek(weeks)

        assertEquals(expected, actual)
    }

    @Test
    fun `refresh active time report week with days`() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextDay = startOfWeek + 1.days
        val firstTimeInterval = runBlocking {
            clockIn(android, startOfWeek)
            clockOut(android, startOfWeek + 1.hours)
        }
        val secondTimeInterval = runBlocking {
            clockIn(android, nextDay)
        }
        val weeks = groupByWeek(
            listOf(
                firstTimeInterval,
                secondTimeInterval
            )
        )
        projectHolder += android
        val actual = mutableListOf<TimeReportViewActions>()
        vm.viewActions.observeForever(actual::add)
        val expected = listOf(
            TimeReportViewActions.RefreshTimeReportWeek(0)
        )

        vm.refreshActiveTimeReportWeek(weeks)

        assertEquals(expected, actual)
    }

    @Test
    fun `refresh active time report week with weeks`() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        val secondTimeInterval = runBlocking {
            clockIn(android, nextWeek)
            clockOut(android, nextWeek + 1.hours)
        }
        val firstTimeInterval = runBlocking {
            clockIn(android, startOfWeek)
        }
        val weeks = groupByWeek(
            listOf(
                firstTimeInterval,
                secondTimeInterval
            )
        )
        projectHolder += android
        val actual = mutableListOf<TimeReportViewActions>()
        vm.viewActions.observeForever(actual::add)
        val expected = listOf(
            TimeReportViewActions.RefreshTimeReportWeek(1)
        )

        vm.refreshActiveTimeReportWeek(weeks)

        assertEquals(expected, actual)
    }
}
