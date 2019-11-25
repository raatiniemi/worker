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

package me.raatiniemi.worker.feature.shared.view

import android.view.View

internal inline fun <T : View> click(view: T, crossinline block: (T) -> Unit) {
    view.setOnClickListener {
        block(view)
    }
}

internal inline fun <T : View> longClick(view: T, crossinline block: (T) -> Boolean) {
    view.setOnLongClickListener {
        block(view)
    }
}

fun View.visibleIf(defaultVisibility: Int = View.INVISIBLE, predicate: () -> Boolean) {
    if (defaultVisibility != View.INVISIBLE && defaultVisibility != View.GONE) {
        throw IllegalArgumentException("defaultVisibility needs to be either `View.GONE` or `View.INVISIBLE`")
    }

    visibility = if (predicate()) {
        View.VISIBLE
    } else {
        defaultVisibility
    }
}

internal fun show(view: View) {
    view.visibility = View.VISIBLE
}

internal fun hide(view: View, visibility: Int = View.INVISIBLE) {
    require(!(visibility != View.INVISIBLE && visibility != View.GONE)) {
        "visibility needs to be either `View.GONE` or `View.INVISIBLE`"
    }

    view.visibility = visibility
}
