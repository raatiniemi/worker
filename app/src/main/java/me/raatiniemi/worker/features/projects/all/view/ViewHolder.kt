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

package me.raatiniemi.worker.features.projects.all.view

import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.projects.all.model.ProjectsItem
import me.raatiniemi.worker.features.shared.view.visibleIf

internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name: AppCompatTextView = view.findViewById(R.id.tvName)
    private val time: AppCompatTextView = view.findViewById(R.id.tvTimeSummary)

    val clockActivityToggle: AppCompatImageButton = view.findViewById(R.id.ibClockActivityToggle)
    val clockActivityAt: AppCompatImageButton = view.findViewById(R.id.ibClockActivityAt)
    val delete: AppCompatImageButton = view.findViewById(R.id.ibDelete)

    private val clockedInSince: AppCompatTextView = view.findViewById(R.id.tvClockedInSince)

    fun bind(projectsItem: ProjectsItem) {
        val resources = itemView.resources

        name.text = projectsItem.title
        time.text = projectsItem.timeSummary

        with(clockActivityToggle) {
            isActivated = projectsItem.isActive
            contentDescription = if (projectsItem.isActive) {
                resources.getString(R.string.fragment_projects_item_clock_out, projectsItem.title)
            } else {
                resources.getString(R.string.fragment_projects_item_clock_in, projectsItem.title)
            }
        }

        with(clockActivityAt) {
            contentDescription = if (projectsItem.isActive) {
                resources.getString(R.string.fragment_projects_item_clock_out_at, projectsItem.title)
            } else {
                resources.getString(R.string.fragment_projects_item_clock_in_at, projectsItem.title)
            }
        }

        with(delete) {
            contentDescription = resources.getString(R.string.fragment_projects_item_delete, projectsItem.title)
        }

        clockedInSince.visibleIf { projectsItem.isActive }
        clockedInSince.text = projectsItem.getClockedInSince(resources)
    }

    fun clearValues() {
        name.text = null
        time.text = null

        clockedInSince.text = null
    }
}
