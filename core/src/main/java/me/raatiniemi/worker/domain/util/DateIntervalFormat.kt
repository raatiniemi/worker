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

import me.raatiniemi.worker.domain.time.Milliseconds

/**
 * Interface for different date interval formatter implementations.
 */
@FunctionalInterface
interface DateIntervalFormat {
    /**
     * Format an interval in milliseconds.
     *
     * @param milliseconds Interval in milliseconds to format.
     * @return Formatted interval.
     */
    fun format(milliseconds: Milliseconds): String
}
