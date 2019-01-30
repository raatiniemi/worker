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
import androidx.recyclerview.widget.RecyclerView
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAdapterResult
import me.raatiniemi.worker.features.project.timereport.model.TimeReportGroup
import me.raatiniemi.worker.features.project.timereport.view.GroupItemViewHolder
import me.raatiniemi.worker.features.shared.view.shortDayMonthDayInMonth
import me.raatiniemi.worker.features.shared.view.widget.LetterDrawable
import me.raatiniemi.worker.util.SelectionListener
import me.raatiniemi.worker.util.SelectionManager
import me.raatiniemi.worker.util.SelectionManagerAdapterDecorator
import java.util.*

internal class TimeReportAdapter(
        private val formatter: HoursMinutesFormat,
        selectionListener: SelectionListener
) : RecyclerView.Adapter<GroupItemViewHolder>() {
    private val items = ArrayList<TimeReportGroup>()

    private val selectionManager: SelectionManager<TimeReportAdapterResult>

    val selectedItems: List<TimeReportAdapterResult>
        get() = selectionManager.selectedItems

    init {
        selectionManager = SelectionManagerAdapterDecorator(this, selectionListener)

        setHasStableIds(true)
    }

    override fun getItemCount() = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fragment_time_report_group_item, parent, false)

        return GroupItemViewHolder(view)
    }

    override fun onBindViewHolder(vh: GroupItemViewHolder, position: Int) {
        val groupItem = items[position]

        with(vh) {
            title.text = shortDayMonthDayInMonth(groupItem.date)
            summarize.text = groupItem.getTimeSummaryWithDifference(formatter)

            val firstLetterInTitle = title.text.run { first().toString() }
            letter.setImageDrawable(LetterDrawable.build(firstLetterInTitle))

            val results = groupItem.buildItemResultsWithGroupIndex(position)

            letter.setOnLongClickListener {
                if (selectionManager.isSelectionActivated) {
                    return@setOnLongClickListener false
                }

                selectionManager.selectItems(results)
                true
            }

            letter.setOnClickListener {
                if (!selectionManager.isSelectionActivated) {
                    return@setOnClickListener
                }

                if (selectionManager.isSelected(results)) {
                    selectionManager.deselectItems(results)
                    return@setOnClickListener
                }
                selectionManager.selectItems(results)
            }

            itemView.isSelected = selectionManager.isSelected(results)

            // In case the item have been selected, we should not activate
            // it. The selected background color should take precedence.
            itemView.isActivated = false
            if (!itemView.isSelected) {
                itemView.isActivated = groupItem.isRegistered
            }
        }
    }

    fun add(items: List<TimeReportGroup>): Int {
        // Retrieve the current count to have a reference point
        // at which location the new items will be inserted.
        val index = itemCount
        this.items.addAll(items)

        // Notify the adapter of the new items.
        notifyDataSetChanged()

        // Return the reference point for the location of the new items.
        return index
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun haveSelectedItems(): Boolean {
        return selectionManager.isSelectionActivated
    }

    fun deselectItems() {
        selectionManager.deselectItems()
    }
}
