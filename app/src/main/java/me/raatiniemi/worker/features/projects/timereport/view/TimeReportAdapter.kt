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
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.date.HoursMinutesFormat
import me.raatiniemi.worker.domain.time.calculateHoursMinutes
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.calculateInterval
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportLongPressAction
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportStateManagerAdapterDecorator
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportTapAction
import me.raatiniemi.worker.features.projects.timereport.viewmodel.TimeReportStateManager
import me.raatiniemi.worker.features.shared.view.click
import me.raatiniemi.worker.features.shared.view.longClick
import me.raatiniemi.worker.features.shared.view.visibleIf
import me.raatiniemi.worker.features.shared.view.widget.letterDrawable

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

        vh.bind(day, position)
    }

    private fun DayViewHolder.bind(day: TimeReportDay, position: Int) {
        title(day).also {
            title.text = it
            letter.setImageDrawable(letterDrawable(firstLetter(it)))
        }
        timeSummary.text = timeSummaryWithDifference(day, formatter)

        apply(stateManager.state(day), header)

        buildTimeReportItemList(items, day.timeIntervals)
        items.visibleIf(View.GONE) { stateManager.expanded(position) }

        longClick(letter) {
            stateManager.consume(TimeReportLongPressAction.LongPressDay(day))
        }

        click(letter) {
            stateManager.consume(TimeReportTapAction.TapDay(day))
        }

        click(itemView) {
            if (items.visibility == View.VISIBLE) {
                stateManager.collapse(position)
                return@click
            }
            stateManager.expand(position)
        }
    }

    private fun buildTimeReportItemList(
        parent: LinearLayoutCompat,
        timeIntervals: List<TimeInterval>
    ) {
        val layoutInflater = LayoutInflater.from(parent.context)

        parent.removeAllViews()
        timeIntervals.forEach { timeInterval ->
            val view =
                layoutInflater.inflate(R.layout.fragment_project_time_report_item, parent, false)
            bindTimeReportItemViewHolder(view, timeInterval)

            parent.addView(view)
        }
    }

    private fun bindTimeReportItemViewHolder(view: View, timeInterval: TimeInterval) {
        ItemViewHolder(view)
            .also(bind(timeInterval))
    }

    private fun bind(timeInterval: TimeInterval): (ItemViewHolder) -> Unit = { vh ->
        val hoursMinutes = calculateHoursMinutes(calculateInterval(timeInterval))
        vh.timeInterval.text = title(timeInterval)
        vh.timeSummary.text = formatter.apply(hoursMinutes)

        apply(stateManager.state(timeInterval), vh.itemView)

        longClick(vh.itemView) {
            stateManager.consume(TimeReportLongPressAction.LongPressItem(timeInterval))
        }
        click(vh.itemView) {
            stateManager.consume(TimeReportTapAction.TapItem(timeInterval))
        }
    }

    companion object {
        private val timeReportDiffCallback = object : DiffUtil.ItemCallback<TimeReportDay>() {
            override fun areItemsTheSame(old: TimeReportDay, new: TimeReportDay) =
                old.date == new.date

            override fun areContentsTheSame(old: TimeReportDay, new: TimeReportDay) = old == new
        }
    }
}
