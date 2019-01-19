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
import androidx.paging.PagedListAdapter
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.projects.model.ProjectsAction
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import me.raatiniemi.worker.features.projects.view.ProjectsActionConsumer
import me.raatiniemi.worker.features.projects.view.ProjectsItemViewHolder
import me.raatiniemi.worker.features.shared.view.visibleIf
import me.raatiniemi.worker.util.HintedImageButtonListener

internal class ProjectsAdapter(
        private val consumer: ProjectsActionConsumer,
        private val hintedImageButtonListener: HintedImageButtonListener
) : PagedListAdapter<ProjectsItem, ProjectsItemViewHolder>(projectsDiffCallback) {
    operator fun get(position: Int) = getItem(position)

    override fun getItemViewType(position: Int): Int {
        return R.layout.fragment_projects_item
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ProjectsItemViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(viewType, viewGroup, false)

        return ProjectsItemViewHolder(view)
    }

    override fun onBindViewHolder(vh: ProjectsItemViewHolder, index: Int) {
        val item = getItem(index)
        if (item == null) {
            vh.clearValues()
            return
        }

        val result = ProjectsItemAdapterResult(index, item)

        vh.apply {
            name.text = item.title
            time.text = item.timeSummary

            val resources = vh.itemView.resources
            clockedInSince.text = item.getClockedInSince(resources)
            clockedInSince.visibleIf { item.isActive }

            with(clockActivityToggle) {
                contentDescription = if (item.isActive) {
                    resources.getString(R.string.fragment_projects_item_clock_out, item.title)
                } else {
                    resources.getString(R.string.fragment_projects_item_clock_in, item.title)
                }

                setOnLongClickListener(hintedImageButtonListener)
                isActivated = item.isActive
            }

            with(clockActivityAt) {
                contentDescription = if (item.isActive) {
                    resources.getString(R.string.fragment_projects_item_clock_out_at, item.title)
                } else {
                    resources.getString(R.string.fragment_projects_item_clock_in_at, item.title)
                }

                setOnLongClickListener(hintedImageButtonListener)
            }

            with(delete) {
                contentDescription = resources.getString(R.string.fragment_projects_item_delete, item.title)

                setOnLongClickListener(hintedImageButtonListener)
            }

            itemView.setOnClickListener {
                consumer.accept(ProjectsAction.Open(result))
            }
            clockActivityToggle.setOnClickListener {
                consumer.accept(ProjectsAction.Toggle(result))
            }
            clockActivityAt.setOnClickListener {
                consumer.accept(ProjectsAction.At(result))
            }
            delete.setOnClickListener {
                consumer.accept(ProjectsAction.Remove(result))
            }
        }
    }
}
