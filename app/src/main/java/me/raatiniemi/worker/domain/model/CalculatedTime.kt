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

package me.raatiniemi.worker.domain.model

data class CalculatedTime(val hours: Long, val minutes: Long) {
    fun asMilliseconds(): Long {
        return calculateSeconds() * MILLISECONDS_IN_SECOND
    }

    private fun calculateSeconds(): Long {
        return calculateMinutes() * SECONDS_IN_MINUTE
    }

    private fun calculateMinutes(): Long {
        val hoursInMinutes = hours * MINUTES_IN_HOUR

        return hoursInMinutes + minutes
    }

    operator fun plus(value: CalculatedTime): CalculatedTime {
        var accumulatedHours = hours + value.hours
        var accumulatedMinutes = minutes + value.minutes

        if (accumulatedMinutes >= MINUTES_IN_HOUR) {
            accumulatedHours += accumulatedMinutes / MINUTES_IN_HOUR
            accumulatedMinutes %= MINUTES_IN_HOUR
        }

        return CalculatedTime(accumulatedHours, accumulatedMinutes)
    }

    companion object {
        private val MINUTES_IN_HOUR = 60
        private val SECONDS_IN_MINUTE = 60
        private val MILLISECONDS_IN_SECOND = 1000

        val empty = CalculatedTime(hours = 0, minutes = 0)
    }
}

fun Collection<CalculatedTime>.accumulated(): CalculatedTime {
    return fold(CalculatedTime.empty) { acc, value -> acc + value }
}
