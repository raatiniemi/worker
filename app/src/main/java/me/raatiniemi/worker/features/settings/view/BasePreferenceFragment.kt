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

package me.raatiniemi.worker.features.settings.view

import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.util.NullUtil.isNull
import timber.log.Timber

abstract class BasePreferenceFragment : PreferenceFragment() {
    @get:StringRes
    protected abstract val title: Int

    override fun onResume() {
        super.onResume()

        // Set the title for the preference fragment.
        activity.setTitle(title)
    }

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen, preference: Preference): Boolean {
        super.onPreferenceTreeClick(preferenceScreen, preference)
        if (preference is PreferenceScreen) {
            switchPreferenceScreen(preference.getKey())
        } else {
            Timber.d("Preference '%s' is not implemented", preference.title)
            val snackBar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.error_message_preference_not_implemented,
                    Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
        return false
    }

    /**
     * Switch the currently displayed preference screen.
     *
     * @param key Key for the new preference screen.
     */
    internal open fun switchPreferenceScreen(key: String) {
        Timber.w("Switch to preference screen '%s' is not implemented", key)

        displayPreferenceScreenNotImplementedMessage()
    }

    private fun displayPreferenceScreenNotImplementedMessage() {
        val contentView = activity.findViewById<View>(android.R.id.content)
        if (isNull(contentView)) {
            return
        }

        val snackBar = Snackbar.make(
                contentView,
                R.string.error_message_preference_screen_not_implemented,
                Snackbar.LENGTH_SHORT
        )
        snackBar.show()
    }
}
