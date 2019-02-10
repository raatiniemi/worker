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
import me.raatiniemi.worker.features.project.timereport.model.TimeReportLongPressAction
import me.raatiniemi.worker.features.project.timereport.model.TimeReportTapAction
import me.raatiniemi.worker.features.project.timereport.model.getTimeSummaryWithDifference
import me.raatiniemi.worker.features.project.timereport.view.DayViewHolder
import me.raatiniemi.worker.features.project.timereport.view.ItemViewHolder
import me.raatiniemi.worker.features.project.timereport.viewmodel.TimeReportSelectionManager
import me.raatiniemi.worker.features.shared.view.shortDayMonthDayInMonth
import me.raatiniemi.worker.features.shared.view.widget.LetterDrawable

internal class TimeReportAdapter(
        private val formatter: HoursMinutesFormat,
        private val selectionManager: TimeReportSelectionManager
) : PagedListAdapter<TimeReportDay, DayViewHolder>(timeReportDiffCallback) {
    private val expandedItems = mutableSetOf<Int>()

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

            when (selectionManager.state(day)) {
                TimeReportState.SELECTED -> {
                    header.isSelected = true
                    header.isActivated = false
                }
                TimeReportState.REGISTERED -> {
                    header.isSelected = false
                    header.isActivated = true
                }
                TimeReportState.EMPTY -> {
                    header.isSelected = false
                    header.isActivated = false
                }
            }

            buildTimeReportItemList(items, day.items)

            letter.setOnLongClickListener {
                selectionManager.consume(TimeReportLongPressAction.LongPressDay(day))
            }

            letter.setOnClickListener {
                selectionManager.consume(TimeReportTapAction.TapDay(day))
            }

            items.visibility = if (expandedItems.contains(position)) {
                View.VISIBLE
            } else {
                View.GONE
            }
            itemView.setOnClickListener {
                if (items.visibility == View.VISIBLE) {
                    expandedItems.remove(position)
                } else {
                    expandedItems.add(position)
                }
                notifyItemChanged(position)
            }
        }
    }

    private fun buildTimeReportItemList(parent: LinearLayoutCompat, items: List<TimeReportItem>) {
        val layoutInflater = LayoutInflater.from(parent.context)

        parent.removeAllViews()
        items.forEach { item ->
            val view = layoutInflater.inflate(R.layout.fragment_time_report_item, parent, false)
            ItemViewHolder(view).apply {
                timeInterval.text = item.title
                timeSummary.text = item.getTimeSummaryWithFormatter(formatter)

                itemView.setOnLongClickListener {
                    selectionManager.consume(TimeReportLongPressAction.LongPressItem(item))
                }
                itemView.setOnClickListener {
                    selectionManager.consume(TimeReportTapAction.TapItem(item))
                }
            }

            parent.addView(view)
        }
    }
}
