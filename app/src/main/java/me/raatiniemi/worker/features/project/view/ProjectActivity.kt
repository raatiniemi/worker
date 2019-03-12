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
import androidx.lifecycle.Observer
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.features.project.model.ProjectHolder
import me.raatiniemi.worker.features.project.timereport.view.TimeReportFragment
import me.raatiniemi.worker.features.shared.view.activity.BaseActivity
import me.raatiniemi.worker.util.NullUtil.isNull
import org.koin.android.ext.android.inject

class ProjectActivity : BaseActivity() {
    private val projectHolder: ProjectHolder by inject()

    private lateinit var timeReportFragment: TimeReportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        projectHolder += readProject(intent)
        projectHolder.value.observe(this, Observer {
            title = it.name
        })

        timeReportFragment = TimeReportFragment.newInstance()

        if (isNull(savedInstanceState)) {
            supportFragmentManager.beginTransaction()
                    .replace(
                            R.id.flFragmentContainer,
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

        val newProject = readProject(intent)
        projectHolder.value.run {
            val project = value ?: return@run

            if (project != newProject) {
                projectHolder += newProject
                timeReportFragment.reloadTimeReport()
            }
        }
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
