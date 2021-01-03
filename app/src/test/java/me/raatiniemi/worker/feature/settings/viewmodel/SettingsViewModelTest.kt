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

package me.raatiniemi.worker.feature.settings.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.feature.settings.model.SettingsViewActions
import me.raatiniemi.worker.feature.shared.model.observeNonNull
import me.raatiniemi.worker.koin.testKoinModules
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@RunWith(JUnit4::class)
class SettingsViewModelTest : AutoCloseKoinTest() {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val keyValueStore by inject<KeyValueStore>()

    private val vm by inject<SettingsViewModel>()

    @Before
    fun setUp() {
        startKoin {
            modules(testKoinModules)
        }
    }

    // Confirm clock out

    @Test
    fun `confirm clock out with default value`() {
        val actual = vm.confirmClockOut

        assertTrue(actual)
    }

    @Test
    fun `confirm clock out when disabled`() {
        vm.confirmClockOut = false

        val actual = vm.confirmClockOut

        assertFalse(actual)
    }

    @Test
    fun `confirm clock out when enabled`() {
        vm.confirmClockOut = true

        val actual = vm.confirmClockOut

        assertTrue(actual)
    }

    // Time summary

    @Test
    fun `time summary with default value`() {
        val expected = TimeIntervalStartingPoint.MONTH.rawValue

        val actual = vm.timeSummary

        assertEquals(expected, actual)
    }

    @Test
    fun `time summary with month`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.MONTH.rawValue)
        val expected = TimeIntervalStartingPoint.MONTH.rawValue

        val actual = vm.timeSummary

        assertEquals(expected, actual)
    }

    @Test
    fun `time summary with week`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.WEEK.rawValue)
        val expected = TimeIntervalStartingPoint.WEEK.rawValue

        val actual = vm.timeSummary

        assertEquals(expected, actual)
    }

    // Ongoing notification enabled

    @Test
    fun `ongoing notification enabled with default value`() {
        val actual = vm.ongoingNotificationEnabled

        assertTrue(actual)
    }

    @Test
    fun `ongoing notification enabled when disabled`() {
        vm.ongoingNotificationEnabled = false

        val actual = vm.ongoingNotificationEnabled

        assertFalse(actual)
    }

    @Test
    fun `ongoing notification enabled when enabled`() {
        vm.ongoingNotificationEnabled = true

        val actual = vm.ongoingNotificationEnabled

        assertTrue(actual)
    }

    // Ongoing notification chronometer enabled

    @Test
    fun `ongoing notification chronometer enabled when ongoing notification is disabled`() {
        vm.ongoingNotificationEnabled = false
        vm.ongoingNotificationChronometerEnabled = true

        val actual = vm.ongoingNotificationChronometerEnabled

        assertFalse(actual)
    }

    @Test
    fun `ongoing notification chronometer enabled when disabled`() {
        vm.ongoingNotificationEnabled = true
        vm.ongoingNotificationChronometerEnabled = false

        val actual = vm.ongoingNotificationChronometerEnabled

        assertFalse(actual)
    }

    @Test
    fun `ongoing notification chronometer enabled when enabled`() {
        vm.ongoingNotificationEnabled = true
        vm.ongoingNotificationChronometerEnabled = true

        val actual = vm.ongoingNotificationChronometerEnabled

        assertTrue(actual)
    }

    // Change time summary starting point

    @Test
    fun `change time summary starting point with current starting point`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.MONTH.rawValue)

        vm.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.MONTH.rawValue)

        assertEquals(
            TimeIntervalStartingPoint.MONTH.rawValue,
            keyValueStore.int(AppKeys.TIME_SUMMARY)
        )
    }

    @Test
    fun `change time summary starting point with invalid starting point`() {
        vm.changeTimeSummaryStartingPoint(-1)

        vm.viewActions.observeNonNull {
            assertEquals(
                SettingsViewActions.ShowUnableToChangeTimeSummaryStartingPointErrorMessage,
                it
            )
        }
    }

    @Test
    fun `change time summary starting point from month to week`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.MONTH.rawValue)

        vm.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.WEEK.rawValue)

        vm.viewActions.observeNonNull {
            assertEquals(SettingsViewActions.ShowTimeSummaryStartingPointChangedToWeek, it)
        }
    }

    @Test
    fun `change time summary starting point from week to month`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.WEEK.rawValue)

        vm.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.MONTH.rawValue)

        vm.viewActions.observeNonNull {
            assertEquals(SettingsViewActions.ShowTimeSummaryStartingPointChangedToMonth, it)
        }
    }
}
