/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.project.timereport.view

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAction
import timber.log.Timber

internal class TimeReportActionModeCallback(private val consumer: TimeReportActionConsumer) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.apply {
            setTitle(R.string.menu_title_actions)
            menuInflater?.inflate(R.menu.actions_project_time_report, menu)
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = when (item?.itemId) {
        R.id.actions_project_time_report_register -> {
            consumer.consume(TimeReportAction.TOGGLE_REGISTERED)
            true
        }
        R.id.actions_project_time_report_delete -> {
            consumer.consume(TimeReportAction.REMOVE)
            false
        }
        else -> {
            Timber.w("Unknown action with title: %s", item?.title)
            false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
    }
}
