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

import android.view.View
import android.widget.ImageButton
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import me.raatiniemi.worker.R

internal class ProjectsItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.findViewById(R.id.tvName)
    val time: TextView = view.findViewById(R.id.tvTimeSummary)
    val clockActivityToggle: ImageButton = view.findViewById(R.id.ibClockActivityToggle)
    val clockActivityAt: ImageButton = view.findViewById(R.id.ibClockActivityAt)
    val delete: ImageButton = view.findViewById(R.id.ibDelete)
    val clockedInSince: TextView = view.findViewById(R.id.tvClockedInSince)
}
