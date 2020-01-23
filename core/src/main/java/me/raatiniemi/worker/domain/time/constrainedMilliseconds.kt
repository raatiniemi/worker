/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.time

/**
 * Constrain [Milliseconds] between a min and max value.
 *
 * If [milliseconds] is equal or greater than [upperBound] then [upperBound] will be returned. If
 * [milliseconds] is equal or less than [lowerBound] then [lowerBound] will be returned. In any
 * other case [milliseconds] will be returned.
 *
 * If value for [upperBound] is less than [lowerBound] the function will be called with the
 * arguments swapped.
 *
 * @param milliseconds Milliseconds to be constrained.
 * @param lowerBound Lower bound for allowed values.
 * @param upperBound Upper bound for allowed values.
 */
fun constrainedMilliseconds(
    milliseconds: Milliseconds,
    lowerBound: Milliseconds,
    upperBound: Milliseconds
): Milliseconds {
    if (upperBound < lowerBound) {
        return constrainedMilliseconds(milliseconds, upperBound, lowerBound)
    }

    return when {
        milliseconds >= upperBound -> upperBound
        milliseconds <= lowerBound -> lowerBound
        else -> milliseconds
    }
}
