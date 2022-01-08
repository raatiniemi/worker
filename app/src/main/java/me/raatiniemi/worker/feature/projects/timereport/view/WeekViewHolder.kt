/*
 * Copyright (C) 2022 Tobias Raatiniemi
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
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import me.raatiniemi.worker.R
import me.raatiniemi.worker.databinding.FragmentProjectTimeReportDayBinding
import me.raatiniemi.worker.databinding.FragmentProjectTimeReportWeekBinding
import me.raatiniemi.worker.domain.date.HoursMinutesFormat
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.model.timeSummary
import me.raatiniemi.worker.feature.projects.timereport.viewmodel.TimeReportStateManager

internal class WeekViewHolder(
    private val binding: FragmentProjectTimeReportWeekBinding,
    private val stateManager: TimeReportStateManager,
    private val formatter: HoursMinutesFormat
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(week: TimeReportWeek?) {
        if (week == null) {
            clearValues()
            return
        }

        bindWeek(week)
    }

    private fun clearValues() {
        with(binding) {
            tvTitle.text = ""
            tvSummary.text = ""

            llDays.removeAllViews()
        }
    }

    private fun bindWeek(week: TimeReportWeek) {
        with(binding) {
            tvTitle.text = itemView.resources.getString(
                R.string.projects_time_report_week,
                week(week)
            )
            tvSummary.text = formatter.apply(timeSummary(week))

            buildItemList(llDays, week.days)
        }
    }

    private fun buildItemList(items: LinearLayoutCompat, days: List<TimeReportDay>) {
        items.removeAllViews()

        val inflater = LayoutInflater.from(items.context)
        days.forEach { day ->
            val binding = FragmentProjectTimeReportDayBinding.inflate(inflater, items, false)
            val viewHolder = DayViewHolder(binding, stateManager, formatter)
            viewHolder.bind(day)

            items.addView(binding.root)
        }
    }
}
