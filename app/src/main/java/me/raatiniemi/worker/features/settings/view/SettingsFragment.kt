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

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import me.raatiniemi.worker.BuildConfig
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.shared.view.configurePreference

class SettingsFragment : BasePreferenceFragment() {
    public override val title = R.string.activity_settings_title

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurePreference<Preference>("settings_about_version") {
            isSelectable = false
            summary = getString(
                    R.string.activity_settings_about_version_format,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE
            )
        }
    }
}