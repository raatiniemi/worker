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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.activity_settings.*
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.settings.project.view.ProjectFragment
import me.raatiniemi.worker.features.shared.view.activity.BaseActivity
import me.raatiniemi.worker.util.NullUtil.isNull

class SettingsActivity : BaseActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        configureView()

        if (isNull(savedInstanceState)) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.flContainer, SettingsFragment())
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Override the toolbar back button to behave as the back button.
        if (android.R.id.home == item.itemId) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (shouldPopBackStack()) {
            supportFragmentManager.popBackStack()
            return
        }

        super.onBackPressed()
    }

    private fun shouldPopBackStack(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.flContainer)

        val settings = SettingsFragment::class.java
        return settings != fragment?.javaClass
    }

    private fun configureView() {
        setSupportActionBar(tbMain)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.activity_settings_title)
        }
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        val fragment: Fragment
        when (pref.key) {
            SETTINGS_PROJECT_KEY -> fragment = ProjectFragment()
            else -> return false
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment, pref.key)
                .addToBackStack(pref.key)
                .commit()

        return true
    }

    companion object {
        private const val SETTINGS_PROJECT_KEY = "settings_project"

        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}
