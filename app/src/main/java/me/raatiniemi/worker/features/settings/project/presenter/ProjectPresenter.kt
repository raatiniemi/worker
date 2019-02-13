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

package me.raatiniemi.worker.features.settings.project.presenter

import me.raatiniemi.worker.domain.exception.InvalidStartingPointException
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.features.settings.project.exception.InvalidTimeReportSummaryFormatException
import me.raatiniemi.worker.features.settings.project.model.TimeSummaryStartingPointChangeEvent
import me.raatiniemi.worker.features.settings.project.view.ProjectView
import me.raatiniemi.worker.features.shared.presenter.BasePresenter
import me.raatiniemi.worker.util.KeyValueStore
import me.raatiniemi.worker.util.TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK
import me.raatiniemi.worker.util.TIME_REPORT_SUMMARY_FORMAT_FRACTION
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class ProjectPresenter(
        private val keyValueStore: KeyValueStore,
        private val eventBus: EventBus
) : BasePresenter<ProjectView>() {
    fun changeTimeSummaryStartingPoint(newStartingPoint: Int) {
        try {
            val currentStartingPoint = keyValueStore.startingPointForTimeSummary()
            if (currentStartingPoint == newStartingPoint) {
                return
            }

            when (TimeIntervalStartingPoint.from(newStartingPoint)) {
                TimeIntervalStartingPoint.WEEK -> keyValueStore.useWeekForTimeSummaryStartingPoint()
                TimeIntervalStartingPoint.MONTH -> keyValueStore.useMonthForTimeSummaryStartingPoint()
                else -> throw InvalidStartingPointException(
                        "Starting point '$newStartingPoint' is not valid"
                )
            }

            eventBus.post(TimeSummaryStartingPointChangeEvent())

            performWithView { view ->
                if (TimeIntervalStartingPoint.WEEK.rawValue == newStartingPoint) {
                    view.showChangeTimeSummaryStartingPointToWeekSuccessMessage()
                    return@performWithView
                }

                view.showChangeTimeSummaryStartingPointToMonthSuccessMessage()
            }
        } catch (e: InvalidStartingPointException) {
            Timber.w(e, "Unable to set new starting point")

            performWithView { it.showChangeTimeSummaryStartingPointErrorMessage() }
        }

    }

    fun changeTimeReportSummaryFormat(newFormat: Int) {
        val currentFormat = keyValueStore.timeReportSummaryFormat()
        if (currentFormat == newFormat) {
            return
        }

        try {
            when (newFormat) {
                TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK -> keyValueStore.useDigitalClockAsTimeReportSummaryFormat()
                TIME_REPORT_SUMMARY_FORMAT_FRACTION -> keyValueStore.useFractionAsTimeReportSummaryFormat()
                else -> throw InvalidTimeReportSummaryFormatException(
                        "Summary format '$newFormat' is not valid"
                )
            }

            performWithView { view ->
                if (TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK == newFormat) {
                    view.showChangeTimeReportSummaryToDigitalClockSuccessMessage()
                    return@performWithView
                }

                view.showChangeTimeReportSummaryToFractionSuccessMessage()
            }
        } catch (e: InvalidTimeReportSummaryFormatException) {
            Timber.w(e, "Unable to set new format")

            performWithView { it.showChangeTimeReportSummaryFormatErrorMessage() }
        }

    }
}
