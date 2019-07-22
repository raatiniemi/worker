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

package me.raatiniemi.worker.features.projects.timereport.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.paging.PagedListAdapter
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.model.calculateInterval
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import me.raatiniemi.worker.domain.util.calculateHoursMinutes
import me.raatiniemi.worker.features.projects.timereport.model.*
import me.raatiniemi.worker.features.projects.timereport.view.DayViewHolder
import me.raatiniemi.worker.features.projects.timereport.view.ItemViewHolder
import me.raatiniemi.worker.features.projects.timereport.view.title
import me.raatiniemi.worker.features.projects.timereport.viewmodel.TimeReportStateManager
import me.raatiniemi.worker.features.shared.view.shortDayMonthDayInMonth
import me.raatiniemi.worker.features.shared.view.visibleIf
import me.raatiniemi.worker.features.shared.view.widget.LetterDrawable

internal class TimeReportAdapter(
    private val formatter: HoursMinutesFormat,
    stateManager: TimeReportStateManager
) : PagedListAdapter<TimeReportDay, DayViewHolder>(timeReportDiffCallback) {
    private val stateManager: TimeReportStateManager =
        TimeReportStateManagerAdapterDecorator(this, stateManager)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fragment_project_time_report_day, parent, false)

        return DayViewHolder(view)
    }

    override fun onBindViewHolder(vh: DayViewHolder, position: Int) {
        val day = getItem(position)
        if (day == null) {
            vh.clearValues()
            return
        }

        with(vh) {
            title.text = shortDayMonthDayInMonth(day.date).capitalize()
            timeSummary.text = day.getTimeSummaryWithDifference(formatter)

            val firstLetterInTitle = title.text.run { first().toString() }
            letter.setImageDrawable(LetterDrawable.build(firstLetterInTitle))

            header.apply(stateManager.state(day))

            buildTimeReportItemList(items, day.items)
            items.visibleIf(View.GONE) { stateManager.expanded(position) }

            letter.setOnLongClickListener {
                stateManager.consume(TimeReportLongPressAction.LongPressDay(day))
            }

            letter.setOnClickListener {
                stateManager.consume(TimeReportTapAction.TapDay(day))
            }

            itemView.setOnClickListener {
                if (items.visibility == View.VISIBLE) {
                    stateManager.collapse(position)
                    return@setOnClickListener
                }
                stateManager.expand(position)
            }
        }
    }

    private fun buildTimeReportItemList(parent: LinearLayoutCompat, items: List<TimeReportItem>) {
        val layoutInflater = LayoutInflater.from(parent.context)

        parent.removeAllViews()
        items.forEach { item ->
            val view =
                layoutInflater.inflate(R.layout.fragment_project_time_report_item, parent, false)
            bindTimeReportItemViewHolder(view, item)

            parent.addView(view)
        }
    }

    private fun bindTimeReportItemViewHolder(view: View, item: TimeReportItem) {
        val vh = ItemViewHolder(view)
        with(vh) {
            val hoursMinutes = calculateHoursMinutes(calculateInterval(item.asTimeInterval()))
            timeInterval.text = title(item.asTimeInterval())
            timeSummary.text = formatter.apply(hoursMinutes)

            itemView.apply(stateManager.state(item))

            itemView.setOnLongClickListener {
                stateManager.consume(TimeReportLongPressAction.LongPressItem(item))
            }
            itemView.setOnClickListener {
                stateManager.consume(TimeReportTapAction.TapItem(item))
            }
        }
    }
}

private fun View.apply(state: TimeReportState) = when (state) {
    TimeReportState.SELECTED -> {
        isSelected = true
        isActivated = false
    }
    TimeReportState.REGISTERED -> {
        isSelected = false
        isActivated = true
    }
    TimeReportState.EMPTY -> {
        isSelected = false
        isActivated = false
    }
}
