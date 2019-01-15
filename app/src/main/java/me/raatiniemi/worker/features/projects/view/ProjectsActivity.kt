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

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import me.raatiniemi.worker.R
import me.raatiniemi.worker.WorkerApplication
import me.raatiniemi.worker.features.projects.createproject.model.CreateProjectEvent
import me.raatiniemi.worker.features.projects.createproject.view.CreateProjectFragment
import me.raatiniemi.worker.features.settings.view.SettingsActivity
import me.raatiniemi.worker.features.shared.view.activity.BaseActivity
import me.raatiniemi.worker.util.NullUtil.isNull
import me.raatiniemi.worker.util.NullUtil.nonNull
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class ProjectsActivity : BaseActivity() {
    private val eventBus = EventBus.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)

        if (shouldRestartApplication()) {
            restart()
            return
        }

        if (isNull(savedInstanceState)) {
            val fragment = ProjectsFragment()

            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, FRAGMENT_PROJECT_LIST_TAG)
                    .commit()
        }

        eventBus.register(this)
    }

    private fun shouldRestartApplication(): Boolean {
        val action = intent.action ?: false

        return WorkerApplication.INTENT_ACTION_RESTART == action
    }

    private fun restart() {
        try {
            // The AlarmManager will allow us to send the start intent after
            // we have stopped the application, i.e. it will restart.
            val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(this, ProjectsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val pendingIntent = PendingIntent.getActivity(
                    this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT
            )

            manager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
        } catch (e: ClassCastException) {
            Timber.w(e, "Unable to cast the AlarmManager")
        }

        finish()
    }

    public override fun onDestroy() {
        super.onDestroy()

        if (nonNull(eventBus) && eventBus.isRegistered(this)) {
            eventBus.unregister(this)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: CreateProjectEvent) {
        val snackBar = Snackbar.make(
                findViewById(android.R.id.content),
                R.string.message_project_created,
                Snackbar.LENGTH_SHORT
        )
        snackBar.show()
    }

    companion object {
        private const val FRAGMENT_PROJECT_LIST_TAG = "project list"
        private const val FRAGMENT_CREATE_PROJECT_TAG = "create project"
    }
}
