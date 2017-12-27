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

package me.raatiniemi.worker.domain.util;

import java.util.Locale;

import me.raatiniemi.worker.domain.model.CalculatedTime;

/**
 * Format a time interval into hours with fraction, i.e. 1.5 for one hour and 30 minutes.
 */
public class FractionIntervalFormat implements DateIntervalFormat {
    private static final String FRACTION_FORMAT = "%.2f";
    private static final float MINUTES_IN_HOUR = 60;

    @Override
    public String format(long milliseconds) {
        CalculatedTime calculatedTime = CalculateTime.calculateTime(milliseconds);

        return String.format(
                Locale.forLanguageTag("en_US"),
                FRACTION_FORMAT,
                calculateHoursWithFraction(calculatedTime)
        );
    }

    private static float calculateHoursWithFraction(CalculatedTime calculatedTime) {
        return calculatedTime.getHours() + calculateFraction(calculatedTime.getMinutes());
    }

    private static float calculateFraction(long minutes) {
        return (float) minutes / MINUTES_IN_HOUR;
    }
}
