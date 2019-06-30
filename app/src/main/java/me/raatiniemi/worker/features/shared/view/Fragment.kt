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

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import timber.log.Timber
import kotlin.reflect.KClass

fun Fragment.setTitle(title: String) {
    try {
        val activity = requireActivity()
        if (activity !is AppCompatActivity) {
            Timber.w("Unable to set title from fragment, activity is not of type `AppCompatActivity`")
            return
        }

        val supportActionBar = activity.supportActionBar
        if (supportActionBar == null) {
            Timber.w("Unable to set title from fragment, no support action bar is configured")
            return
        }

        supportActionBar.title = title
    } catch (e: IllegalStateException) {
        Timber.w(e, "Unable to set title from fragment without activity")
    }
}

internal fun Fragment.show(dialogFragment: DialogFragment) {
    dialogFragment.show(childFragmentManager, tag(dialogFragment::class))
}

private fun <T : Fragment> tag(reference: KClass<T>): String {
    return reference.java.simpleName
}
