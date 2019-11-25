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

package me.raatiniemi.worker.feature.projects.timereport.view

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.date.HoursMinutesFormat
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.model.timeSummary
import me.raatiniemi.worker.feature.projects.timereport.viewmodel.TimeReportStateManager

internal class WeekViewHolder(
    private val stateManager: TimeReportStateManager,
    private val formatter: HoursMinutesFormat,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    private val title: AppCompatTextView = itemView.findViewById(R.id.tvTitle)
    private val summary: AppCompatTextView = itemView.findViewById(R.id.tvSummary)
    private val days: LinearLayoutCompat = itemView.findViewById(R.id.llDays)

    fun bind(week: TimeReportWeek?) {
        if (week == null) {
            clearValues()
            return
        }

        bindWeek(week)
    }

    private fun clearValues() {
        title.text = ""
        summary.text = ""

        days.removeAllViews()
    }

    private fun bindWeek(week: TimeReportWeek) {
        title.text = itemView.resources.getString(R.string.projects_time_report_week, week(week))
        summary.text = formatter.apply(timeSummary(week))

        buildItemList(days, week.days)
    }

    private fun buildItemList(items: LinearLayoutCompat, days: List<TimeReportDay>) {
        items.removeAllViews()

        val layoutInflater = LayoutInflater.from(items.context)
        days.forEach { day ->
            layoutInflater.inflateDayView(items)
                .also {
                    bindItemView(it, day)
                    items.addView(it)
                }
        }
    }

    private fun bindItemView(view: View, day: TimeReportDay) {
        val viewHolder = DayViewHolder(stateManager, formatter, view)
        viewHolder.bind(day)
    }
}

private fun LayoutInflater.inflateDayView(items: LinearLayoutCompat): View {
    return inflate(R.layout.fragment_project_time_report_day, items, false)
}
