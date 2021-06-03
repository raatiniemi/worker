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

package me.raatiniemi.worker.feature.projects.timereport.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import me.raatiniemi.worker.databinding.FragmentProjectTimeReportWeekBinding
import me.raatiniemi.worker.domain.date.HoursMinutesFormat
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportStateManagerAdapterDecorator
import me.raatiniemi.worker.feature.projects.timereport.viewmodel.TimeReportStateManager

internal class TimeReportAdapter(
    stateManager: TimeReportStateManager,
    private val formatter: HoursMinutesFormat
) : PagingDataAdapter<TimeReportWeek, WeekViewHolder>(timeReportDiffCallback) {
    private val stateManager: TimeReportStateManager =
        TimeReportStateManagerAdapterDecorator(this, stateManager)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentProjectTimeReportWeekBinding.inflate(inflater, parent, false)

        return WeekViewHolder(binding, stateManager, formatter)
    }

    override fun onBindViewHolder(vh: WeekViewHolder, position: Int) {
        vh.bind(getItem(position))
    }

    companion object {
        private val timeReportDiffCallback = object : DiffUtil.ItemCallback<TimeReportWeek>() {
            override fun areItemsTheSame(old: TimeReportWeek, new: TimeReportWeek) =
                old.start == new.start

            override fun areContentsTheSame(old: TimeReportWeek, new: TimeReportWeek) = old == new
        }
    }
}
