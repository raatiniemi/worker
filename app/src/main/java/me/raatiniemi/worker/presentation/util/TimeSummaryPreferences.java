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

package me.raatiniemi.worker.presentation.util;

import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;

public interface TimeSummaryPreferences {
    /**
     * Get the time summary starting point, default value is {@link GetProjectTimeSince#MONTH}.
     */
    int getStartingPointForTimeSummary();

    /**
     * Use week for time summary starting point, i.e. {@link GetProjectTimeSince#WEEK}.
     */
    void useWeekForTimeSummaryStartingPoint();

    /**
     * Use month for time summary starting point, i.e. {@link GetProjectTimeSince#MONTH}.
     */
    void useMonthForTimeSummaryStartingPoint();
}
