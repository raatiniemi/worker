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

package me.raatiniemi.worker.features.projects.timereport.model

import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import me.raatiniemi.worker.domain.model.TimeReportDay
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.features.projects.timereport.viewmodel.TimeReportStateManager

class TimeReportStateManagerAdapterDecorator(
        private val adapter: RecyclerView.Adapter<*>,
        private val stateManager: TimeReportStateManager
) : TimeReportStateManager {
    @MainThread
    override fun expanded(position: Int): Boolean = stateManager.expanded(position)

    @MainThread
    override fun expand(position: Int) = stateManager.expand(position)
            .apply { adapter.notifyItemChanged(position) }

    @MainThread
    override fun collapse(position: Int) = stateManager.collapse(position)
            .apply { adapter.notifyItemChanged(position) }

    @MainThread
    override fun state(day: TimeReportDay) = stateManager.state(day)

    @MainThread
    override fun state(item: TimeReportItem) = stateManager.state(item)

    @MainThread
    override fun consume(longPress: TimeReportLongPressAction): Boolean {
        return stateManager.consume(longPress)
                .apply { adapter.notifyDataSetChanged() }
    }

    @MainThread
    override fun consume(tap: TimeReportTapAction) {
        return stateManager.consume(tap)
                .apply { adapter.notifyDataSetChanged() }
    }
}
