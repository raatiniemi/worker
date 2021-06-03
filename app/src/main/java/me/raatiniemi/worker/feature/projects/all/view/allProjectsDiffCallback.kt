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

import androidx.recyclerview.widget.DiffUtil
import me.raatiniemi.worker.feature.projects.all.model.ProjectsItem

internal val allProjectsDiffCallback = object : DiffUtil.ItemCallback<ProjectsItem>() {
    override fun areItemsTheSame(old: ProjectsItem, new: ProjectsItem) = old.title == new.title

    override fun areContentsTheSame(old: ProjectsItem, new: ProjectsItem) = old == new
}
