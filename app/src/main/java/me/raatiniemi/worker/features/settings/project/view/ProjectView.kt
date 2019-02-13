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

package me.raatiniemi.worker.features.settings.project.view

import me.raatiniemi.worker.features.shared.view.MvpView

interface ProjectView : MvpView {
    /**
     * Show message for successful time summary starting point change to week.
     */
    fun showChangeTimeSummaryStartingPointToWeekSuccessMessage()

    /**
     * Show message for successful time summary starting point change to month.
     */
    fun showChangeTimeSummaryStartingPointToMonthSuccessMessage()

    /**
     * Show message for failed time summary starting point change.
     */
    fun showChangeTimeSummaryStartingPointErrorMessage()

    /**
     * Show message for successful time report summary format change to digital clock.
     */
    fun showChangeTimeReportSummaryToDigitalClockSuccessMessage()

    /**
     * Show message for successful time report summary format change to fraction.
     */
    fun showChangeTimeReportSummaryToFractionSuccessMessage()

    /**
     * Show message for failed time report summary format change.
     */
    fun showChangeTimeReportSummaryFormatErrorMessage()
}
