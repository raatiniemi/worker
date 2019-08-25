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

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import me.raatiniemi.worker.domain.exception.InvalidStartingPointException
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.features.settings.project.model.ProjectViewActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore
import timber.log.Timber

class ProjectViewModel(private val keyValueStore: KeyValueStore) : ViewModel() {
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

    val viewActions = ConsumableLiveData<ProjectViewActions>()

    @MainThread
    fun changeTimeSummaryStartingPoint(newStartingPoint: Int) {
        val currentStartingPoint = keyValueStore.int(AppKeys.TIME_SUMMARY)
        if (currentStartingPoint == newStartingPoint) {
            Timber.d("New time summary starting point is same as current starting point")
            return
        }

        try {
            val startingPoint = TimeIntervalStartingPoint.from(newStartingPoint)
            val viewAction = when (startingPoint) {
                TimeIntervalStartingPoint.WEEK -> {
                    Timber.d("Changing time summary starting point to week")

                    keyValueStore.set(AppKeys.TIME_SUMMARY, TimeIntervalStartingPoint.WEEK.rawValue)
                    ProjectViewActions.ShowTimeSummaryStartingPointChangedToWeek
                }
                TimeIntervalStartingPoint.MONTH -> {
                    Timber.d("Changing time summary starting point to month")

                    keyValueStore.set(
                        AppKeys.TIME_SUMMARY,
                        TimeIntervalStartingPoint.MONTH.rawValue
                    )
                    ProjectViewActions.ShowTimeSummaryStartingPointChangedToMonth
                }
                else -> throw InvalidStartingPointException("Starting point '$newStartingPoint' is not valid")
            }

            viewActions.postValue(viewAction)
        } catch (e: InvalidStartingPointException) {
            Timber.e(e, "Unable to change time summary starting point")

            viewActions.postValue(ProjectViewActions.ShowUnableToChangeTimeSummaryStartingPointErrorMessage)
        }
    }
}
