/*
 * Copyright (C) 2022 Tobias Raatiniemi
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

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Helper function for showing [DialogFragment] with use of a [FragmentManager].
 *
 * @param fm Fragment manager to use when showing dialog fragment.
 * @param p Producer of dialog fragment to show.
 */
internal fun show(fm: FragmentManager, p: () -> DialogFragment) {
    val df = p()
    df.show(fm, tag(df::class))
}
