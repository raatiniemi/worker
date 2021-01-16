/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.projects.all.view

import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import me.raatiniemi.worker.R
import me.raatiniemi.worker.databinding.FragmentAllProjectsItemBinding
import me.raatiniemi.worker.feature.projects.all.model.ProjectsItem
import me.raatiniemi.worker.feature.shared.view.visibleIf

internal class ViewHolder(private val binding: FragmentAllProjectsItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val clockActivityToggle: AppCompatImageButton = binding.ibClockActivityToggle
    val clockActivityAt: AppCompatImageButton = binding.ibClockActivityAt
    val delete: AppCompatImageButton = binding.ibDelete

    fun bind(projectsItem: ProjectsItem) {
        val resources = itemView.resources

        binding.tvName.text = projectsItem.title
        binding.tvTimeSummary.text = projectsItem.timeSummary

        with(binding.ibClockActivityToggle) {
            isActivated = projectsItem.isActive
            contentDescription = if (projectsItem.isActive) {
                resources.getString(R.string.projects_all_clock_out, projectsItem.title)
            } else {
                resources.getString(R.string.projects_all_clock_in, projectsItem.title)
            }
        }

        with(binding.ibClockActivityAt) {
            contentDescription = if (projectsItem.isActive) {
                resources.getString(R.string.projects_all_clock_out_at, projectsItem.title)
            } else {
                resources.getString(R.string.projects_all_clock_in_at, projectsItem.title)
            }
        }

        with(binding.ibDelete) {
            contentDescription =
                resources.getString(R.string.projects_all_delete, projectsItem.title)
        }

        binding.tvClockedInSince.visibleIf { projectsItem.isActive }
        binding.tvClockedInSince.text = projectsItem.getClockedInSince(resources)
    }

    fun clearValues() {
        binding.tvName.text = null
        binding.tvTimeSummary.text = null

        binding.tvClockedInSince.text = null
    }
}
