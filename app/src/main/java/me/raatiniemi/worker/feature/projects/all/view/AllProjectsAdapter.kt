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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import me.raatiniemi.worker.R
import me.raatiniemi.worker.databinding.FragmentAllProjectsItemBinding
import me.raatiniemi.worker.feature.projects.all.model.AllProjectsActions
import me.raatiniemi.worker.feature.projects.all.model.ProjectsItem
import me.raatiniemi.worker.feature.shared.view.click
import me.raatiniemi.worker.feature.shared.view.hintContentDescription
import me.raatiniemi.worker.feature.shared.view.longClick
import java.util.*

internal class AllProjectsAdapter(
    private val consumer: (AllProjectsActions) -> Unit
) : PagingDataAdapter<ProjectsItem, ViewHolder>(allProjectsDiffCallback) {
    operator fun get(position: Int) = getItem(position)

    override fun getItemViewType(position: Int): Int {
        return R.layout.fragment_all_projects_item
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = FragmentAllProjectsItemBinding.inflate(inflater, viewGroup, false)
        return ViewHolder(binding)
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
                consumer(AllProjectsActions.Open(item))
            }

            click(clockActivityToggle) {
                consumer(AllProjectsActions.Toggle(item, Date()))
            }
            longClick(clockActivityToggle, ::hintContentDescription)

            click(clockActivityAt) {
                consumer(AllProjectsActions.At(item))
            }
            longClick(clockActivityAt, ::hintContentDescription)

            click(delete) {
                consumer(AllProjectsActions.Remove(item))
            }
            longClick(delete, ::hintContentDescription)
        }
    }
}
