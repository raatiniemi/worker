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

package me.raatiniemi.worker.feature.settings.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.timeinterval.model.timeIntervalStartingPoint
import me.raatiniemi.worker.domain.timeinterval.usecase.InvalidStartingPointException
import me.raatiniemi.worker.feature.settings.model.SettingsViewActions
import me.raatiniemi.worker.feature.shared.model.ConsumableLiveData
import me.raatiniemi.worker.feature.shared.model.plusAssign
import timber.log.Timber

internal class SettingsViewModel(private val keyValueStore: KeyValueStore) : ViewModel() {
    var confirmClockOut: Boolean
        @MainThread
        get() = keyValueStore.bool(AppKeys.CONFIRM_CLOCK_OUT, true)
        @MainThread
        set(value) {
            keyValueStore.set(AppKeys.CONFIRM_CLOCK_OUT, value)
        }

    val timeSummary: Int
        @MainThread
        get() = keyValueStore.int(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.MONTH.rawValue)

    var ongoingNotificationEnabled: Boolean
        @MainThread
        get() = keyValueStore.bool(AppKeys.ONGOING_NOTIFICATION_ENABLED, true)
        @MainThread
        set(value) {
            keyValueStore.set(AppKeys.ONGOING_NOTIFICATION_ENABLED, value)
        }

    var ongoingNotificationChronometerEnabled: Boolean
        @MainThread
        get() {
            return if (ongoingNotificationEnabled) {
                keyValueStore.bool(AppKeys.ONGOING_NOTIFICATION_CHRONOMETER_ENABLED, true)
            } else {
                false
            }
        }
        @MainThread
        set(value) {
            keyValueStore.set(AppKeys.ONGOING_NOTIFICATION_CHRONOMETER_ENABLED, value)
        }

    val viewActions = ConsumableLiveData<SettingsViewActions>()

    @MainThread
    fun changeTimeSummaryStartingPoint(newStartingPoint: Int) {
        val currentStartingPoint = keyValueStore.int(AppKeys.TIME_SUMMARY)
        if (currentStartingPoint == newStartingPoint) {
            Timber.d("New time summary starting point is same as current starting point")
            return
        }

        try {
            val viewAction = when (timeIntervalStartingPoint(newStartingPoint)) {
                TimeIntervalStartingPoint.WEEK -> {
                    Timber.d("Changing time summary starting point to week")

                    keyValueStore.set(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.WEEK.rawValue)
                    SettingsViewActions.ShowTimeSummaryStartingPointChangedToWeek
                }
                TimeIntervalStartingPoint.MONTH -> {
                    Timber.d("Changing time summary starting point to month")

                    keyValueStore.set(
                        AppKeys.TIME_SUMMARY,
                        TimeIntervalStartingPoint.MONTH.rawValue
                    )
                    SettingsViewActions.ShowTimeSummaryStartingPointChangedToMonth
                }
                else -> throw InvalidStartingPointException("Starting point '$newStartingPoint' is not valid")
            }

            viewActions += viewAction
        } catch (e: InvalidStartingPointException) {
            Timber.e(e, "Unable to change time summary starting point")

            viewActions += SettingsViewActions.ShowUnableToChangeTimeSummaryStartingPointErrorMessage
        }
    }
}
