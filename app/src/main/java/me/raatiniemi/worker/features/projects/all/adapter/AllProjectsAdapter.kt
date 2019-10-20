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

package me.raatiniemi.worker.features.projects.all.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import me.raatiniemi.worker.R
import me.raatiniemi.worker.features.projects.all.model.ProjectsItem
import me.raatiniemi.worker.features.projects.all.view.AllProjectsActionListener
import me.raatiniemi.worker.features.projects.all.view.ViewHolder
import me.raatiniemi.worker.features.shared.view.click
import me.raatiniemi.worker.features.shared.view.hintContentDescription
import java.util.*

internal class AllProjectsAdapter(
    private val listener: AllProjectsActionListener
) : PagedListAdapter<ProjectsItem, ViewHolder>(allProjectsDiffCallback) {
    operator fun get(position: Int) = getItem(position)

    override fun getItemViewType(position: Int): Int {
        return R.layout.fragment_all_projects_item
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(viewType, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(vh: ViewHolder, index: Int) {
        val item = getItem(index)
        if (item == null) {
            vh.clearValues()
            return
        }

        vh.apply {
            bind(item)

            click(itemView) {
                listener.open(item)
            }

            click(clockActivityToggle) {
                listener.toggle(item, Date())
            }
            with(clockActivityToggle) {
                setOnLongClickListener(::hintContentDescription)
            }

            click(clockActivityAt) {
                listener.at(item)
            }
            with(clockActivityAt) {
                setOnLongClickListener(::hintContentDescription)
            }

            click(delete) {
                listener.remove(item)
            }
            with(delete) {
                setOnLongClickListener(::hintContentDescription)
            }
        }
    }
}
