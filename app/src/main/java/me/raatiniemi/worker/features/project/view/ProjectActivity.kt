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

package me.raatiniemi.worker.features.project.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.project.model.ProjectHolder
import me.raatiniemi.worker.features.project.timereport.view.TimeReportFragment
import me.raatiniemi.worker.features.shared.view.activity.BaseActivity
import me.raatiniemi.worker.util.KeyValueStore
import me.raatiniemi.worker.util.NullUtil.isNull
import me.raatiniemi.worker.util.NullUtil.nonNull
import org.koin.android.ext.android.inject

class ProjectActivity : BaseActivity() {
    private val keyValueStore: KeyValueStore by inject()
    private val projectHolder: ProjectHolder by inject()

    private lateinit var timeReportFragment: TimeReportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        val project = readProject(intent)
        projectHolder.project = project

        title = project.name

        timeReportFragment = TimeReportFragment.newInstance()

        if (isNull(savedInstanceState)) {
            supportFragmentManager.beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            timeReportFragment,
                            ProjectActivity.FRAGMENT_TIME_REPORT_TAG
                    )
                    .commit()
        }
    }

    private fun readProject(intent: Intent): Project {
        return intent.run {
            val id = getLongExtra(MESSAGE_PROJECT_ID, 0)
            val name = getStringExtra(MESSAGE_PROJECT_NAME)

            Project(id, name)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val project = readProject(intent)
        if (projectHolder.project != project) {
            projectHolder.project = project

            title = project.name
            timeReportFragment.reloadTimeReport()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actions_project, menu)

        // Set the selected value for the option, otherwise the value will be set to default each
        // time the activity is created.
        val hideRegistered = menu.findItem(R.id.actions_project_hide_registered)
        if (nonNull(hideRegistered)) {
            hideRegistered.isChecked = keyValueStore.hideRegisteredTime()
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.actions_project_hide_registered == item.itemId) {
            handleHideRegisteredTimeChange(item)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun handleHideRegisteredTimeChange(item: MenuItem) {
        item.isChecked = !item.isChecked

        keyValueStore.setHideRegisteredTime(item.isChecked)
        timeReportFragment.reloadTimeReport()
    }

    companion object {
        private const val MESSAGE_PROJECT_ID = "project id"
        private const val MESSAGE_PROJECT_NAME = "project name"

        /**
         * Tag for the time report fragment.
         */
        private const val FRAGMENT_TIME_REPORT_TAG = "time report"

        @JvmStatic
        fun newIntent(context: Context, project: Project): Intent {
            val intent = Intent(context, ProjectActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra(ProjectActivity.MESSAGE_PROJECT_ID, project.id)
            intent.putExtra(ProjectActivity.MESSAGE_PROJECT_NAME, project.name)

            return intent
        }
    }
}
