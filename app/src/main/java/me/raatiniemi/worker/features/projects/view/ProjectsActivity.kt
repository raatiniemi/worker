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
import android.view.Menu
import android.view.MenuItem
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.projects.createproject.view.CreateProjectFragment
import me.raatiniemi.worker.features.settings.view.SettingsActivity
import me.raatiniemi.worker.features.shared.view.activity.BaseActivity
import me.raatiniemi.worker.util.NullUtil.isNull

class ProjectsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)

        title = getString(R.string.activity_projects_title)

        if (isNull(savedInstanceState)) {
            val fragment = ProjectsFragment()

            supportFragmentManager.beginTransaction()
                    .replace(R.id.flFragmentContainer, fragment, FRAGMENT_PROJECT_LIST_TAG)
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.actions_projects, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.actions_main_create_project -> {
            openCreateProject()
            true
        }
        R.id.actions_main_settings -> {
            openSettings()
            true
        }
        else -> super.onOptionsItemSelected(menuItem)
    }

    private fun openCreateProject() {
        val createProjectFragment = CreateProjectFragment.newInstance()

        supportFragmentManager.beginTransaction()
                .add(createProjectFragment, FRAGMENT_CREATE_PROJECT_TAG)
                .commit()
    }

    private fun openSettings() {
        val intent = SettingsActivity.newIntent(this)
        startActivity(intent)
    }

    companion object {
        private const val FRAGMENT_PROJECT_LIST_TAG = "project list"
        private const val FRAGMENT_CREATE_PROJECT_TAG = "create project"
    }
}
