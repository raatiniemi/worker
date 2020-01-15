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

package me.raatiniemi.worker.util

/**
 * Truncates [String] to conform to [maxLength].
 *
 * @param s String to truncate.
 * @param maxLength Allowed max length of string before truncating.
 *
 * @return Truncated string, or original value if length is less than or equal to [maxLength].
 */
internal fun truncate(s: String, maxLength: Int): String {
    if (s.length <= maxLength) {
        return s
    }

    return s.substring(0, maxLength)
}
