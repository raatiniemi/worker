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

package me.raatiniemi.worker.domain.util

import me.raatiniemi.worker.domain.model.CalculatedTime
import java.util.*

/**
 * Format a time interval into hours with fraction, i.e. 1.5 for one hour and 30 minutes.
 */
class FractionIntervalFormat : DateIntervalFormat, CalculatedTimeFormat {
    override fun format(milliseconds: Long): String {
        val calculatedTime = CalculateTime.calculateTime(milliseconds)

        return apply(calculatedTime)
    }

    override fun apply(calculatedTime: CalculatedTime): String {
        return String.format(
                Locale.forLanguageTag("en_US"),
                FRACTION_FORMAT,
                calculateHoursWithFraction(calculatedTime)
        )
    }

    companion object {
        private const val FRACTION_FORMAT = "%.2f"
        private const val MINUTES_IN_HOUR = 60f

        private fun calculateHoursWithFraction(calculatedTime: CalculatedTime): Float {
            return calculatedTime.hours + calculateFraction(calculatedTime.minutes)
        }

        private fun calculateFraction(minutes: Long): Float {
            return minutes.toFloat() / MINUTES_IN_HOUR
        }
    }
}
