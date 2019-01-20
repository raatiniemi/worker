/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.project.timereport.view

import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAdapterResult
import me.raatiniemi.worker.features.project.timereport.model.TimeReportGroup
import me.raatiniemi.worker.features.shared.view.adapter.ExpandableListAdapter
import me.raatiniemi.worker.features.shared.view.widget.LetterDrawable
import me.raatiniemi.worker.util.SelectionListener
import me.raatiniemi.worker.util.SelectionManager
import me.raatiniemi.worker.util.SelectionManagerAdapterDecorator

internal class TimeReportAdapter(
        private val formatter: HoursMinutesFormat,
        selectionListener: SelectionListener
) : ExpandableListAdapter<TimeReportItem, TimeReportGroup, GroupItemViewHolder, ChildItemViewHolder>() {
    private val selectionManager: SelectionManager<TimeReportAdapterResult>

    val selectedItems: List<TimeReportAdapterResult>
        get() = selectionManager.selectedItems

    init {
        selectionManager = SelectionManagerAdapterDecorator(this, selectionListener)

        setHasStableIds(true)
    }

    private fun isPointInView(point: Point, view: View): Boolean {
        val x = view.x
        val y = view.y
        val width = x + view.width
        val height = y + view.height

        return (!(point.x < x || point.y < y)
                && point.x <= width
                && point.y <= height)
    }

    override fun onCreateGroupViewHolder(viewGroup: ViewGroup, viewType: Int): GroupItemViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.fragment_time_report_group_item, viewGroup, false)

        return GroupItemViewHolder(view)
    }

    override fun onCreateChildViewHolder(viewGroup: ViewGroup, viewType: Int): ChildItemViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.fragment_time_report_child_item, viewGroup, false)

        return ChildItemViewHolder(view)
    }

    override fun onBindGroupViewHolder(vh: GroupItemViewHolder, group: Int, viewType: Int) {
        val groupItem = get(group)

        vh.title.text = groupItem.title
        vh.summarize.text = groupItem.getTimeSummaryWithDifference(formatter)

        vh.letter.setImageDrawable(LetterDrawable.build(groupItem.firstLetterFromTitle))

        val results = groupItem.buildItemResultsWithGroupIndex(group)

        vh.letter.setOnLongClickListener {
            if (selectionManager.isSelectionActivated) {
                return@setOnLongClickListener false
            }

            selectionManager.selectItems(results)
            true
        }

        vh.letter.setOnClickListener {
            if (!selectionManager.isSelectionActivated) {
                return@setOnClickListener
            }

            if (selectionManager.isSelected(results)) {
                selectionManager.deselectItems(results)
                return@setOnClickListener
            }
            selectionManager.selectItems(results)
        }

        vh.itemView.isSelected = selectionManager.isSelected(results)

        // In case the item have been selected, we should not activate
        // it. The selected background color should take precedence.
        vh.itemView.isActivated = false
        if (!vh.itemView.isSelected) {
            vh.itemView.isActivated = groupItem.isRegistered
        }
    }

    override fun onBindChildViewHolder(vh: ChildItemViewHolder, group: Int, child: Int, viewType: Int) {
        val item = get(group, child)

        val result = TimeReportAdapterResult(group, child, item)

        // Register the long click listener on the time item.
        vh.itemView.setOnLongClickListener {
            if (selectionManager.isSelectionActivated) {
                return@setOnLongClickListener false
            }

            if (selectionManager.isSelected(result)) {
                return@setOnLongClickListener false
            }

            selectionManager.selectItem(result)
            true
        }
        vh.itemView.setOnClickListener {
            if (!selectionManager.isSelectionActivated) {
                return@setOnClickListener
            }

            if (selectionManager.isSelected(result)) {
                selectionManager.deselectItem(result)
                return@setOnClickListener
            }

            selectionManager.selectItem(result)
        }

        vh.itemView.isSelected = selectionManager.isSelected(result)

        // In case the item have been selected, we should not activate
        // it. The selected background color should take precedence.
        vh.itemView.isActivated = false
        if (!vh.itemView.isSelected) {
            vh.itemView.isActivated = item.isRegistered
        }

        vh.title.text = item.title
        vh.summarize.text = item.getTimeSummaryWithFormatter(formatter)
    }

    override fun getGroupItemViewType(group: Int): Int {
        return 0
    }

    override fun getChildItemViewType(group: Int, child: Int): Int {
        return 0
    }

    override fun getGroupId(group: Int): Long {
        val groupItem = get(group)
        return groupItem.id
    }

    override fun getChildId(group: Int, child: Int): Long {
        val item = get(group, child)
        return item.id!!
    }

    override fun onCheckCanExpandOrCollapseGroup(vh: GroupItemViewHolder, group: Int, x: Int, y: Int, expand: Boolean): Boolean {
        return !selectionManager.isSelectionActivated || !isPointInView(Point(x, y), vh.letter)
    }

    fun remove(results: List<TimeReportAdapterResult>) = results.sorted()
            .reversed()
            .forEach { remove(it) }

    fun remove(result: TimeReportAdapterResult) {
        remove(result.group, result.child)
    }

    fun set(results: List<TimeReportAdapterResult>) = results.sorted()
            .forEach { set(it) }

    fun set(result: TimeReportAdapterResult) {
        set(result.group, result.child, TimeReportItem.with(result.timeInterval))
    }

    fun haveSelectedItems(): Boolean {
        return selectionManager.isSelectionActivated
    }

    fun deselectItems() {
        selectionManager.deselectItems()
    }
}
