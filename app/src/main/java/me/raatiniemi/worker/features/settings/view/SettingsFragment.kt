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

import android.app.Fragment
import android.os.Bundle

import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.settings.data.view.DataFragment
import me.raatiniemi.worker.features.settings.project.view.ProjectFragment

class SettingsFragment : BasePreferenceFragment() {
    public override val title = R.string.activity_settings_title

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.settings)
    }

    override fun switchPreferenceScreen(key: String) {
        val fragment: Fragment
        when (key) {
            SETTINGS_PROJECT_KEY -> fragment = ProjectFragment()
            SETTINGS_DATA_KEY -> fragment = DataFragment()
            else -> {
                super.switchPreferenceScreen(key)
                return
            }
        }

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, key)
                .addToBackStack(key)
                .commit()
    }

    companion object {
        private const val SETTINGS_PROJECT_KEY = "settings_project"
        private const val SETTINGS_DATA_KEY = "settings_data"
    }
}
