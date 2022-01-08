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
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import me.raatiniemi.worker.databinding.FragmentProjectTimeReportDayBinding
import me.raatiniemi.worker.databinding.FragmentProjectTimeReportItemBinding
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
    private val binding: FragmentProjectTimeReportDayBinding,
    private val stateManager: TimeReportStateManager,
    private val formatter: HoursMinutesFormat
) {
    fun bind(day: TimeReportDay?) {
        if (day == null) {
            clearValues()
            return
        }

        bindDay(day)
    }

    private fun clearValues() {
        with(binding) {
            tvTitle.text = null
            tvTimeSummary.text = null

            ivLetter.setOnLongClickListener(null)
            ivLetter.setOnClickListener(null)
            root.setOnClickListener(null)

            llItems.removeAllViews()
        }
    }

    private fun bindDay(day: TimeReportDay) {
        with(binding) {
            title(day).also {
                tvTitle.text = it
                ivLetter.setImageDrawable(letterDrawable(firstLetter(it)))
            }
            tvTimeSummary.text = timeSummaryWithDifference(day, formatter)

            apply(stateManager.state(day), clHeader)
            longClick(ivLetter) {
                stateManager.consume(TimeReportLongPressAction.LongPressDay(day))
                true
            }
            click(ivLetter) {
                stateManager.consume(TimeReportTapAction.TapDay(day))
            }
            click(root) {
                if (llItems.visibility == View.VISIBLE) {
                    stateManager.collapse(day)
                    return@click
                }
                stateManager.expand(day)
            }

            buildItemList(llItems, day.timeIntervals)
            visibleIf(llItems, View.GONE) { stateManager.expanded(day) }
        }
    }

    private fun buildItemList(items: LinearLayoutCompat, timeIntervals: List<TimeInterval>) {
        items.removeAllViews()

        val inflater = LayoutInflater.from(items.context)
        timeIntervals.forEach { timeInterval ->
            val binding = FragmentProjectTimeReportItemBinding.inflate(inflater, items, false)
            val viewHolder = ItemViewHolder(binding, stateManager, formatter)
            viewHolder.bind(timeInterval)

            items.addView(binding.root)
        }
    }
}
