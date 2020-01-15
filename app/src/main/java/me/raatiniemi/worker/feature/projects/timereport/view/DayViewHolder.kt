/*
 * Copyright (C) 2020 Tobias Raatiniemi
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
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.date.HoursMinutesFormat
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportLongPressAction
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportTapAction
import me.raatiniemi.worker.feature.projects.timereport.viewmodel.TimeReportStateManager
import me.raatiniemi.worker.feature.shared.view.click
import me.raatiniemi.worker.feature.shared.view.longClick
import me.raatiniemi.worker.feature.shared.view.visibleIf
import me.raatiniemi.worker.feature.shared.view.widget.letterDrawable

internal class DayViewHolder(
    private val stateManager: TimeReportStateManager,
    private val formatter: HoursMinutesFormat,
    private val itemView: View
) {
    private val header: ConstraintLayout = itemView.findViewById(R.id.clHeader)
    private val letter: AppCompatImageView = itemView.findViewById(R.id.ivLetter)
    private val title: AppCompatTextView = itemView.findViewById(R.id.tvTitle)
    private val timeSummary: AppCompatTextView = itemView.findViewById(R.id.tvTimeSummary)
    private val items: LinearLayoutCompat = itemView.findViewById(R.id.llItems)

    fun bind(day: TimeReportDay?) {
        if (day == null) {
            clearValues()
            return
        }

        bindDay(day)
    }

    private fun clearValues() {
        title.text = null
        timeSummary.text = null

        letter.setOnLongClickListener(null)
        letter.setOnClickListener(null)
        itemView.setOnClickListener(null)

        items.removeAllViews()
    }

    private fun bindDay(day: TimeReportDay) {
        title(day).also {
            title.text = it
            letter.setImageDrawable(letterDrawable(firstLetter(it)))
        }
        timeSummary.text = timeSummaryWithDifference(day, formatter)

        apply(stateManager.state(day), header)
        longClick(letter) {
            stateManager.consume(TimeReportLongPressAction.LongPressDay(day))
            true
        }
        click(letter) {
            stateManager.consume(TimeReportTapAction.TapDay(day))
        }
        click(itemView) {
            if (items.visibility == View.VISIBLE) {
                stateManager.collapse(day)
                return@click
            }
            stateManager.expand(day)
        }

        buildItemList(items, day.timeIntervals)
        items.visibleIf(View.GONE) { stateManager.expanded(day) }
    }

    private fun buildItemList(items: LinearLayoutCompat, timeIntervals: List<TimeInterval>) {
        items.removeAllViews()

        val layoutInflater = LayoutInflater.from(items.context)
        timeIntervals.forEach { timeInterval ->
            layoutInflater.inflateItemView(items)
                .also {
                    bindItemView(it, timeInterval)
                    items.addView(it)
                }
        }
    }

    private fun bindItemView(view: View, timeInterval: TimeInterval) {
        val viewHolder = ItemViewHolder(stateManager, formatter, view)
        viewHolder.bind(timeInterval)
    }
}

private fun LayoutInflater.inflateItemView(items: LinearLayoutCompat): View {
    return inflate(R.layout.fragment_project_time_report_item, items, false)
}
