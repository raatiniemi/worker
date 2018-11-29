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

package me.raatiniemi.worker.util

import android.content.Context
import android.content.SharedPreferences
import me.raatiniemi.worker.RobolectricTestCase
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import org.junit.Assert.*
import org.junit.Test
import org.robolectric.RuntimeEnvironment

class SharedKeyValueStoreTest : RobolectricTestCase() {
    private val key = "my_key"
    private val sharedPreferences: SharedPreferences = {
        RuntimeEnvironment.application
                .getSharedPreferences("preference_name", Context.MODE_PRIVATE)
    }()
    private val keyValueStore: KeyValueStore = SharedKeyValueStore(sharedPreferences)

    @Test
    fun bool_withFalseDefaultValue() {
        val actual = keyValueStore.bool(key, defaultValue = false)

        assertFalse(actual)
    }

    @Test
    fun bool_withTrueDefaultValue() {
        val actual = keyValueStore.bool(key, defaultValue = true)

        assertTrue(actual)
    }

    @Test
    fun bool_withFalseValue() {
        keyValueStore.set(key, false)

        val actual = keyValueStore.bool(key, defaultValue = true)

        assertFalse(actual)
    }

    @Test
    fun bool_withTrueValue() {
        keyValueStore.set(key, true)

        val actual = keyValueStore.bool(key, defaultValue = false)

        assertTrue(actual)
    }

    @Test
    fun hideRegisteredTime_withoutValue() {
        val actual = keyValueStore.hideRegisteredTime()

        assertFalse(actual)
    }

    @Test
    fun hideRegisteredTime_whenDisabled() {
        keyValueStore.setHideRegisteredTime(false)

        val actual = keyValueStore.hideRegisteredTime()

        assertFalse(actual)
    }

    @Test
    fun hideRegisteredTime_whenEnabled() {
        keyValueStore.setHideRegisteredTime(true)

        val actual = keyValueStore.hideRegisteredTime()

        assertTrue(actual)
    }

    @Test
    fun confirmClockOut_withoutValue() {
        val actual = keyValueStore.confirmClockOut()

        assertTrue(actual)
    }

    @Test
    fun confirmClockOut_whenDisabled() {
        keyValueStore.setConfirmClockOut(false)

        val actual = keyValueStore.confirmClockOut()

        assertFalse(actual)
    }

    @Test
    fun confirmClockOut_whenEnabled() {
        keyValueStore.setConfirmClockOut(true)

        val actual = keyValueStore.confirmClockOut()

        assertTrue(actual)
    }

    @Test
    fun ongoingNotification_withoutValue() {
        val actual = keyValueStore.ongoingNotification()

        assertTrue(actual)
    }

    @Test
    fun ongoingNotification_whenDisabled() {
        keyValueStore.disableOngoingNotification()

        val actual = keyValueStore.ongoingNotification()

        assertFalse(actual)
    }

    @Test
    fun ongoingNotification_whenEnabled() {
        keyValueStore.enableOngoingNotification()

        val actual = keyValueStore.ongoingNotification()

        assertTrue(actual)
    }

    @Test
    fun ongoingNotificationChronometer_withoutValue() {
        val actual = keyValueStore.ongoingNotificationChronometer()

        assertTrue(actual)
    }

    @Test
    fun ongoingNotificationChronometer_whenDisabled() {
        keyValueStore.disableOngoingNotificationChronometer()

        val actual = keyValueStore.ongoingNotificationChronometer()

        assertFalse(actual)
    }

    @Test
    fun ongoingNotificationChronometer_whenEnabled() {
        keyValueStore.enableOngoingNotificationChronometer()

        val actual = keyValueStore.ongoingNotificationChronometer()

        assertTrue(actual)
    }

    @Test
    fun startingPointForTimeSummary_withoutValue() {
        val actual = keyValueStore.startingPointForTimeSummary()

        assertEquals(TimeIntervalStartingPoint.MONTH.rawValue, actual)
    }

    @Test
    fun startingPointForTimeSummary_withWeek() {
        keyValueStore.useWeekForTimeSummaryStartingPoint()

        val actual = keyValueStore.startingPointForTimeSummary()

        assertEquals(TimeIntervalStartingPoint.WEEK.rawValue, actual)
    }

    @Test
    fun startingPointForTimeSummary_withMonth() {
        keyValueStore.useMonthForTimeSummaryStartingPoint()

        val actual = keyValueStore.startingPointForTimeSummary()

        assertEquals(TimeIntervalStartingPoint.MONTH.rawValue, actual)
    }

    @Test
    fun timeSheetSummaryFormat_withoutValue() {
        val actual = keyValueStore.timeSheetSummaryFormat()

        assertEquals(TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK, actual)
    }

    @Test
    fun timeSheetSummaryFormat_withFraction() {
        keyValueStore.useFractionAsTimeSheetSummaryFormat()

        val actual = keyValueStore.timeSheetSummaryFormat()

        assertEquals(TIME_SHEET_SUMMARY_FORMAT_FRACTION, actual)
    }

    @Test
    fun timeSheetSummaryFormat_withDigitalClock() {
        keyValueStore.useDigitalClockAsTimeSheetSummaryFormat()

        val actual = keyValueStore.timeSheetSummaryFormat()

        assertEquals(TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK, actual)
    }
}
