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
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.features.settings.project.model.ProjectViewActions
import me.raatiniemi.worker.features.shared.model.ConsumableLiveData
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.KeyValueStore
import me.raatiniemi.worker.util.TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK
import me.raatiniemi.worker.util.TIME_REPORT_SUMMARY_FORMAT_FRACTION
import timber.log.Timber

class ProjectViewModel(private val keyValueStore: KeyValueStore) : ViewModel() {
    val viewActions = ConsumableLiveData<ProjectViewActions>()

    @MainThread
    fun changeTimeSummaryStartingPoint(newStartingPoint: Int) {
        val currentStartingPoint = keyValueStore.int(AppKeys.TIME_SUMMARY.rawValue)
        if (currentStartingPoint == newStartingPoint) {
            Timber.d("New time summary starting point is same as current starting point")
            return
        }

        try {
            val startingPoint = TimeIntervalStartingPoint.from(newStartingPoint)
            val viewAction = when (startingPoint) {
                TimeIntervalStartingPoint.WEEK -> {
                    Timber.d("Changing time summary starting point to week")

                    keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.WEEK.rawValue)
                    ProjectViewActions.ShowTimeSummaryStartingPointChangedToWeek
                }
                TimeIntervalStartingPoint.MONTH -> {
                    Timber.d("Changing time summary starting point to month")

                    keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)
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
