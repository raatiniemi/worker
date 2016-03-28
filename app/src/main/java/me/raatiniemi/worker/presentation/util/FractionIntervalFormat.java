/*
 * Copyright (C) 2016 Worker Project
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

import me.raatiniemi.worker.domain.model.CalculatedTime;
import me.raatiniemi.worker.domain.util.CalculateTime;

/**
 * Format a time interval into hours with fraction, i.e. 1.5 for one hour and 30 minutes.
 */
public class FractionIntervalFormat implements DateIntervalFormat {
    private static final String sFractionFormat = "%.2f";
    private static final float sMinutesInHour = 60;

    /**
     * @inheritDoc
     */
    @Override
    public String format(long milliseconds) {
        CalculatedTime calculatedTime = CalculateTime.calculateTime(milliseconds);

        return String.format(
                sFractionFormat,
                calculateHoursWithFraction(calculatedTime)
        );
    }

    private float calculateHoursWithFraction(CalculatedTime calculatedTime) {
        return calculatedTime.getHours() + calculateFraction(calculatedTime.getMinutes());
    }

    private float calculateFraction(long minutes) {
        return (float) minutes / sMinutesInHour;
    }
}
