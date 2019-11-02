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

package me.raatiniemi.worker.features.projects.timereport.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.date.HoursMinutesFormat
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportStateManagerAdapterDecorator
import me.raatiniemi.worker.features.projects.timereport.viewmodel.TimeReportStateManager

internal class TimeReportAdapter(
    stateManager: TimeReportStateManager,
    private val formatter: HoursMinutesFormat
) : PagedListAdapter<TimeReportDay, DayViewHolder>(timeReportDiffCallback) {
    private val stateManager: TimeReportStateManager =
        TimeReportStateManagerAdapterDecorator(this, stateManager)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fragment_project_time_report_day, parent, false)

        return DayViewHolder(stateManager, formatter, view)
    }

    override fun onBindViewHolder(vh: DayViewHolder, position: Int) {
        val day = getItem(position)
        vh.bind(day)
    }

    companion object {
        private val timeReportDiffCallback = object : DiffUtil.ItemCallback<TimeReportDay>() {
            override fun areItemsTheSame(old: TimeReportDay, new: TimeReportDay) =
                old.date == new.date

            override fun areContentsTheSame(old: TimeReportDay, new: TimeReportDay) = old == new
        }
    }
}
