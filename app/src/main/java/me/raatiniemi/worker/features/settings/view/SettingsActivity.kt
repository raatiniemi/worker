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
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.shared.view.activity.BaseActivity
import me.raatiniemi.worker.util.NullUtil.isNull

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (isNull(savedInstanceState)) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SettingsFragment())
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
            fragmentManager.popBackStack()
            return
        }

        super.onBackPressed()
    }

    private fun shouldPopBackStack(): Boolean {
        val fragment = fragmentManager.findFragmentById(R.id.fragment_container)

        val settings = SettingsFragment::class.java
        return settings != fragment.javaClass
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}
