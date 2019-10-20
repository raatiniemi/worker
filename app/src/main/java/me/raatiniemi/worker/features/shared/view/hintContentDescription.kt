/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.shared.view

import android.view.Gravity
import android.view.View
import android.widget.Toast

/**
 * Display the content description of the view in a toast message.
 *
 * @param view View pressed by the user.
 * @return True if the toast was shown, otherwise false.
 */
internal fun hintContentDescription(view: View): Boolean {
    val description = view.contentDescription
    if (description.isNullOrBlank()) {
        return false
    }

    return Toast.makeText(view.context, description, Toast.LENGTH_SHORT)
        .apply {
            val location = view.getLocationInWindow()
            setGravity(
                Gravity.TOP or Gravity.START,
                location.x,
                location.y
            )
        }
        .run {
            show()
            true
        }
}

private data class Location(val x: Int, val y: Int)

private fun View.getLocationInWindow(): Location {
    val position = IntArray(2)
    getLocationInWindow(position)

    return Location(position[0], position[1])
}
