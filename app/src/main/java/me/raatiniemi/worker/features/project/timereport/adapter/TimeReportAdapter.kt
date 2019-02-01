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
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import me.raatiniemi.worker.features.project.timereport.model.getTimeSummaryWithDifference
import me.raatiniemi.worker.features.project.timereport.view.ViewHolder
import me.raatiniemi.worker.features.shared.view.shortDayMonthDayInMonth
import me.raatiniemi.worker.features.shared.view.widget.LetterDrawable
import me.raatiniemi.worker.util.SelectionListener
import me.raatiniemi.worker.util.SelectionManager
import me.raatiniemi.worker.util.SelectionManagerAdapterDecorator

internal class TimeReportAdapter(
        private val formatter: HoursMinutesFormat,
        selectionListener: SelectionListener
) : PagedListAdapter<TimeReportDay, ViewHolder>(timeReportDiffCallback) {
    private val selectionManager: SelectionManager<TimeReportItem>

    val selectedItems: List<TimeReportItem>
        get() = selectionManager.selectedItems

    init {
        selectionManager = SelectionManagerAdapterDecorator(this, selectionListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fragment_time_report_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val day = getItem(position)
        if (day == null) {
            vh.clearValues()
            return
        }

        with(vh) {
            title.text = shortDayMonthDayInMonth(day.date)
            timeSummary.text = day.getTimeSummaryWithDifference(formatter)

            val firstLetterInTitle = title.text.run { first().toString() }
            letter.setImageDrawable(LetterDrawable.build(firstLetterInTitle))

            letter.setOnLongClickListener {
                if (selectionManager.isSelectionActivated) {
                    return@setOnLongClickListener false
                }

                selectionManager.selectItems(day.items)
                true
            }

            letter.setOnClickListener {
                if (!selectionManager.isSelectionActivated) {
                    return@setOnClickListener
                }

                if (selectionManager.isSelected(day.items)) {
                    selectionManager.deselectItems(day.items)
                    return@setOnClickListener
                }
                selectionManager.selectItems(day.items)
            }

            itemView.isSelected = selectionManager.isSelected(day.items)

            // In case the item have been selected, we should not activate
            // it. The selected background color should take precedence.
            itemView.isActivated = false
            if (!itemView.isSelected) {
                itemView.isActivated = day.isRegistered
            }
        }
    }

    fun haveSelectedItems(): Boolean {
        return selectionManager.isSelectionActivated
    }

    fun deselectItems() {
        selectionManager.deselectItems()
    }
}
