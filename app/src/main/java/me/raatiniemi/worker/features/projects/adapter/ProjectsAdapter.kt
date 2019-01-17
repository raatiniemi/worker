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

package me.raatiniemi.worker.features.projects.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import me.raatiniemi.worker.features.projects.view.OnProjectActionListener
import me.raatiniemi.worker.features.projects.view.ProjectsItemViewHolder
import me.raatiniemi.worker.features.shared.view.adapter.SimpleListAdapter
import me.raatiniemi.worker.features.shared.view.visibleIf
import me.raatiniemi.worker.util.HintedImageButtonListener

internal class ProjectsAdapter(
        private val onProjectActionListener: OnProjectActionListener,
        private val hintedImageButtonListener: HintedImageButtonListener
) : SimpleListAdapter<ProjectsItem, ProjectsItemViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return R.layout.fragment_projects_item
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ProjectsItemViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(viewType, viewGroup, false)

        return ProjectsItemViewHolder(view)
    }

    override fun onBindViewHolder(vh: ProjectsItemViewHolder, index: Int) {
        val item = get(index)
        val result = ProjectsItemAdapterResult(index, item)

        vh.apply {
            name.text = item.title
            time.text = item.timeSummary

            val resources = vh.itemView.resources
            clockedInSince.text = item.getClockedInSince(resources)
            clockedInSince.visibleIf { item.isActive }

            itemView.setOnClickListener(onClickListener)

            with(clockActivityToggle) {
                contentDescription = item.getHelpTextForClockActivityToggle(resources)
                setOnClickListener { onProjectActionListener.onClockActivityToggle(result) }
                setOnLongClickListener(hintedImageButtonListener)
                isActivated = item.isActive
            }

            with(clockActivityAt) {
                contentDescription = item.getHelpTextForClockActivityAt(resources)
                setOnClickListener { onProjectActionListener.onClockActivityAt(result) }
                setOnLongClickListener(hintedImageButtonListener)
            }

            with(delete) {
                contentDescription = item.getHelpTextForDelete(resources)
                setOnClickListener { onProjectActionListener.onDelete(result) }
                setOnLongClickListener(hintedImageButtonListener)
            }
        }
    }
}
