/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.features.settings.view;

import me.raatiniemi.worker.features.shared.view.MvpView;

public interface ProjectView extends MvpView {
    /**
     * Show message for successful time summary starting point change to week.
     */
    void showChangeTimeSummaryStartingPointToWeekSuccessMessage();

    /**
     * Show message for successful time summary starting point change to month.
     */
    void showChangeTimeSummaryStartingPointToMonthSuccessMessage();

    /**
     * Show message for failed time summary starting point change.
     */
    void showChangeTimeSummaryStartingPointErrorMessage();

    /**
     * Show message for successful time sheet summary format change to digital clock.
     */
    void showChangeTimeSheetSummaryToDigitalClockSuccessMessage();

    /**
     * Show message for successful time sheet summary format change to fraction.
     */
    void showChangeTimeSheetSummaryToFractionSuccessMessage();

    /**
     * Show message for failed time sheet summary format change.
     */
    void showChangeTimeSheetSummaryFormatErrorMessage();
}
