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
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import me.raatiniemi.worker.R

internal class ProjectsItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name: AppCompatTextView = view.findViewById(R.id.tvName)
    val time: AppCompatTextView = view.findViewById(R.id.tvTimeSummary)

    val clockActivityToggle: AppCompatImageButton = view.findViewById(R.id.ibClockActivityToggle)
    val clockActivityAt: AppCompatImageButton = view.findViewById(R.id.ibClockActivityAt)
    val delete: AppCompatImageButton = view.findViewById(R.id.ibDelete)

    val clockedInSince: AppCompatTextView = view.findViewById(R.id.tvClockedInSince)

    fun clearValues() {
        name.text = null
        time.text = null

        clockedInSince.text = null
    }
}
