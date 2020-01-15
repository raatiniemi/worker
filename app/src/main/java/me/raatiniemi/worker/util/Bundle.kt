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

import android.os.Bundle

/**
 * Builds [Bundle] from [Map] where both key and value is [String].
 *
 * @param v Key and value pairs to add to the [Bundle].
 * @return [Bundle] with included strings from argument.
 */
internal fun bundleOf(v: Map<String, String>): Bundle {
    return Bundle().apply {
        v.forEach { (key, value) ->
            putString(key, value)
        }
    }
}
