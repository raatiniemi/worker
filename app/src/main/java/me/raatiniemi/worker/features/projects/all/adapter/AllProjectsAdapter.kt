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
import me.raatiniemi.worker.util.HintedImageButtonListener
import java.util.*

internal class AllProjectsAdapter(
        private val listener: AllProjectsActionListener,
        private val hintedImageButtonListener: HintedImageButtonListener
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

            itemView.setOnClickListener {
                listener.open(item)
            }

            with(clockActivityToggle) {
                setOnClickListener {
                    listener.toggle(item, Date())
                }
                setOnLongClickListener(hintedImageButtonListener)
            }

            with(clockActivityAt) {
                setOnClickListener {
                    listener.at(item)
                }
                setOnLongClickListener(hintedImageButtonListener)
            }

            with(delete) {
                setOnClickListener {
                    listener.remove(item)
                }
                setOnLongClickListener(hintedImageButtonListener)
            }
        }
    }
}
