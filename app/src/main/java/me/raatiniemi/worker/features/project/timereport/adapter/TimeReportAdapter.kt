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

package me.raatiniemi.worker.features.project.timereport.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.paging.PagedListAdapter
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import me.raatiniemi.worker.features.project.timereport.model.*
import me.raatiniemi.worker.features.project.timereport.view.DayViewHolder
import me.raatiniemi.worker.features.project.timereport.view.ItemViewHolder
import me.raatiniemi.worker.features.project.timereport.viewmodel.TimeReportStateManager
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
        val view = inflater.inflate(R.layout.fragment_time_report_day, parent, false)

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
            items.visibleIf(View.GONE) { stateManager.expanded(day) }

            letter.setOnLongClickListener {
                stateManager.consume(TimeReportLongPressAction.LongPressDay(day))
            }

            letter.setOnClickListener {
                stateManager.consume(TimeReportTapAction.TapDay(day))
            }

            itemView.setOnClickListener {
                if (items.visibility == View.VISIBLE) {
                    stateManager.collapse(day)
                    return@setOnClickListener
                }
                stateManager.expand(day)
            }
        }
    }

    private fun buildTimeReportItemList(parent: LinearLayoutCompat, items: List<TimeReportItem>) {
        val layoutInflater = LayoutInflater.from(parent.context)

        parent.removeAllViews()
        items.forEach { item ->
            val view = layoutInflater.inflate(R.layout.fragment_time_report_item, parent, false)
            bindTimeReportItemViewHolder(view, item)

            parent.addView(view)
        }
    }

    private fun bindTimeReportItemViewHolder(view: View, item: TimeReportItem) {
        val vh = ItemViewHolder(view)
        with(vh) {
            timeInterval.text = item.title
            timeSummary.text = item.getTimeSummaryWithFormatter(formatter)

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
