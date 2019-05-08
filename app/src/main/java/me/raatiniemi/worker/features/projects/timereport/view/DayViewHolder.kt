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

package me.raatiniemi.worker.features.projects.timereport.view

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import me.raatiniemi.worker.R

internal class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val header: ConstraintLayout = view.findViewById(R.id.clHeader)
    val letter: AppCompatImageView = view.findViewById(R.id.ivLetter)
    val title: AppCompatTextView = view.findViewById(R.id.tvTitle)
    val timeSummary: AppCompatTextView = view.findViewById(R.id.tvTimeSummary)
    val items: LinearLayoutCompat = view.findViewById(R.id.llItems)

    fun clearValues() {
        title.text = null
        timeSummary.text = null

        items.removeAllViews()
    }
}