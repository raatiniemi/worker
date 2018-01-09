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
import java.lang.Math.abs
import java.util.*

/**
 * Format a time interval into digital hours and minutes, i.e. 1:30.
 */
class DigitalHoursMinutesIntervalFormat : CalculatedTimeFormat {
    override fun apply(calculatedTime: CalculatedTime): String {
        return String.format(
                Locale.forLanguageTag("en_US"),
                formatFor(calculatedTime),
                abs(calculatedTime.hours),
                padWithZeroes(abs(calculatedTime.minutes))
        )
    }

    private fun formatFor(calculatedTime: CalculatedTime): String {
        // TODO: use `calculatedTime.positive` instead of checking both values.
        // when using `calculatedTime.positive` the kotlin compiler renames the
        // method to `getPositive` instead of `isPositive`.
        if (calculatedTime.hours >= 0 && calculatedTime.minutes >= 0) {
            return FORMAT
        }

        return NEGATIVE_FORMAT
    }

    companion object {
        private val FORMAT = "%1\$s:%2\$s"
        private val NEGATIVE_FORMAT = "-%1\$s:%2\$s"

        private fun padWithZeroes(value: Long): String {
            return replaceSpacesWithZeroes(padWithSpaces(value.toString()))
        }

        private fun padWithSpaces(value: String): String {
            return String.format(Locale.forLanguageTag("en_US"), "%2s", value)
        }

        private fun replaceSpacesWithZeroes(value: String): String {
            return value.replace(' ', '0')
        }
    }
}
