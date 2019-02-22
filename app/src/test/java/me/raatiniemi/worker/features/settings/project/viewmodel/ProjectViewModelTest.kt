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

package me.raatiniemi.worker.features.settings.project.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.features.settings.project.model.ProjectViewActions
import me.raatiniemi.worker.util.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val keyValueStore: KeyValueStore = InMemoryKeyValueStore()

    private lateinit var vm: ProjectViewModel

    @Before
    fun setUp() {
        vm = ProjectViewModel(keyValueStore)
    }

    @Test
    fun `confirm clock out with default value`() {
        val actual = vm.confirmClockOut

        assertTrue(actual)
    }

    @Test
    fun `disable confirm clock out`() {
        vm.confirmClockOut = false

        val actual = vm.confirmClockOut
        assertFalse(actual)
    }

    @Test
    fun `enable confirm clock out`() {
        vm.confirmClockOut = false

        vm.confirmClockOut = true

        val actual = vm.confirmClockOut
        assertTrue(actual)
    }

    @Test
    fun `time summary with default value`() {
        val expected = TimeIntervalStartingPoint.MONTH.rawValue

        val actual = vm.timeSummary

        assertEquals(expected, actual)
    }

    @Test
    fun `time summary with month`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)
        val expected = TimeIntervalStartingPoint.MONTH.rawValue

        val actual = vm.timeSummary

        assertEquals(expected, actual)
    }

    @Test
    fun `time summary with week`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.WEEK.rawValue)
        val expected = TimeIntervalStartingPoint.WEEK.rawValue

        val actual = vm.timeSummary

        assertEquals(expected, actual)
    }

    @Test
    fun `is ongoing notification enabled with default value`() {
        val actual = vm.ongoingNotificationEnabled

        assertTrue(actual)
    }

    @Test
    fun `disable ongoing notification`() {
        vm.ongoingNotificationEnabled = false

        val actual = vm.ongoingNotificationEnabled
        assertFalse(actual)
    }

    @Test
    fun `enable ongoing notification`() {
        vm.ongoingNotificationEnabled = false

        vm.ongoingNotificationEnabled = true

        val actual = vm.ongoingNotificationEnabled
        assertTrue(actual)
    }

    @Test
    fun `is ongoing notification chronometer enabled with ongoing notification disabled`() {
        vm.ongoingNotificationEnabled = false

        vm.ongoingNotificationChronometerEnabled = true

        val actual = vm.ongoingNotificationChronometerEnabled
        assertFalse(actual)
    }

    @Test
    fun `disable ongoing notification chronometer`() {
        vm.ongoingNotificationEnabled = true

        vm.ongoingNotificationChronometerEnabled = false

        val actual = vm.ongoingNotificationChronometerEnabled
        assertFalse(actual)
    }

    @Test
    fun `enable ongoing notification chronometer`() {
        vm.ongoingNotificationEnabled = true

        vm.ongoingNotificationChronometerEnabled = true

        val actual = vm.ongoingNotificationChronometerEnabled
        assertTrue(actual)
    }

    @Test
    fun `change time summary starting point with current starting point`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)

        vm.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.MONTH.rawValue)

        assertEquals(TimeIntervalStartingPoint.MONTH.rawValue, keyValueStore.int(AppKeys.TIME_SUMMARY.rawValue))
    }

    @Test
    fun `change time summary starting point with invalid starting point`() {
        vm.changeTimeSummaryStartingPoint(-1)

        vm.viewActions.observeForever {
            assertEquals(ProjectViewActions.ShowUnableToChangeTimeSummaryStartingPointErrorMessage, it)
        }
    }

    @Test
    fun `change time summary starting point from month to week`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)

        vm.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.WEEK.rawValue)

        vm.viewActions.observeForever {
            assertEquals(ProjectViewActions.ShowTimeSummaryStartingPointChangedToWeek, it)
        }
    }

    @Test
    fun `change time summary starting point from week to month`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.WEEK.rawValue)

        vm.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.MONTH.rawValue)

        vm.viewActions.observeForever {
            assertEquals(ProjectViewActions.ShowTimeSummaryStartingPointChangedToMonth, it)
        }
    }

    @Test
    fun `change time report summary format with current format`() {
        keyValueStore.set(AppKeys.TIME_REPORT_SUMMARY_FORMAT.rawValue, TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK)

        vm.changeTimeReportSummaryFormat(TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK)

        assertEquals(
                TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK,
                keyValueStore.int(AppKeys.TIME_REPORT_SUMMARY_FORMAT.rawValue)
        )
    }

    @Test
    fun `change time report summary format with unknown format`() {
        vm.changeTimeReportSummaryFormat(-1)

        vm.viewActions.observeForever {
            assertEquals(ProjectViewActions.ShowUnableToChangeTimeReportSummaryErrorMessage, it)
        }
    }

    @Test
    fun `change time report summary format from fraction to digital clock`() {
        keyValueStore.set(AppKeys.TIME_REPORT_SUMMARY_FORMAT.rawValue, TIME_REPORT_SUMMARY_FORMAT_FRACTION)

        vm.changeTimeReportSummaryFormat(TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK)

        vm.viewActions.observeForever {
            assertEquals(ProjectViewActions.ShowTimeReportSummaryChangedToDigitalClock, it)
        }
    }

    @Test
    fun `change time report summary format from digital clock to fraction`() {
        keyValueStore.set(AppKeys.TIME_REPORT_SUMMARY_FORMAT.rawValue, TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK)

        vm.changeTimeReportSummaryFormat(TIME_REPORT_SUMMARY_FORMAT_FRACTION)

        vm.viewActions.observeForever {
            assertEquals(ProjectViewActions.ShowTimeReportSummaryChangedToFraction, it)
        }
    }
}
