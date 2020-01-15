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

package me.raatiniemi.worker.feature.shared.view

import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import timber.log.Timber

internal inline fun <reified T : Preference> PreferenceFragmentCompat.configurePreference(
    key: CharSequence,
    configure: T.() -> Unit
) {
    when (val preference = findPreference<Preference>(key)) {
        is T -> preference.configure()
        null -> Timber.w("Unable to find preference with key: $key")
        else -> Timber.w("Preference type (${preference.javaClass.name}) do not match expected ${T::class.java.name}")
    }
}
