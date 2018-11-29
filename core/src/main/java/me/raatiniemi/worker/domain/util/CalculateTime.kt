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

package me.raatiniemi.worker.domain.util

import me.raatiniemi.worker.domain.model.HoursMinutes

class CalculateTime private constructor(private val milliseconds: Long) {
    private fun calculateHoursMinutes(): HoursMinutes {
        val minutes = calculateMinutes()
        val hours = calculateHours()

        if (MINUTES_IN_HOUR.toLong() > minutes) {
            return HoursMinutes(hours, minutes)
        }

        return HoursMinutes(hours + 1, 0)
    }

    private fun calculateSeconds(): Long {
        return milliseconds / 1000
    }

    private fun calculateMinutes(): Long {
        val seconds = calculateSeconds()
        val minutes = seconds / SECONDS_IN_MINUTE % MINUTES_IN_HOUR

        val secondsRemaining = seconds % SECONDS_IN_MINUTE
        if (secondsRemaining < 30) {
            return minutes
        }

        return minutes + 1
    }

    private fun calculateHours(): Long {
        val seconds = calculateSeconds()
        val hours = seconds / SECONDS_IN_HOUR % HOURS_IN_DAY

        val days = seconds / SECONDS_IN_DAY
        if (days == 0L) {
            return hours
        }

        return hours + (days * HOURS_IN_DAY)
    }

    companion object {
        private const val HOURS_IN_DAY = 24
        private const val MINUTES_IN_HOUR = 60
        private const val SECONDS_IN_MINUTE = 60
        private const val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR
        private const val SECONDS_IN_DAY = SECONDS_IN_HOUR * HOURS_IN_DAY

        fun calculateHoursMinutes(milliseconds: Long): HoursMinutes {
            val calculateTime = CalculateTime(milliseconds)
            return calculateTime.calculateHoursMinutes()
        }
    }
}
