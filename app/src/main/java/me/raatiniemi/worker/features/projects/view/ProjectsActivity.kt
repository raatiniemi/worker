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

package me.raatiniemi.worker.features.projects.view

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_projects.*
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.settings.view.SettingsActivity
import me.raatiniemi.worker.features.shared.view.activity.BaseActivity

class ProjectsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)

        configureView()
    }

    private fun configureView() {
        setSupportActionBar(tbMain)

        supportActionBar?.apply {
            title = getString(R.string.activity_projects_title)
        }

        nvProjects.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.actions_main_settings -> startActivity(SettingsActivity.newIntent(this))
            }
            true
        }
    }
}
