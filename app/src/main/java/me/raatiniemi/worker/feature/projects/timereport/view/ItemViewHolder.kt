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

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import me.raatiniemi.worker.R
import me.raatiniemi.worker.domain.date.HoursMinutesFormat
import me.raatiniemi.worker.domain.time.calculateHoursMinutes
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.calculateInterval
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportLongPressAction
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportTapAction
import me.raatiniemi.worker.feature.projects.timereport.viewmodel.TimeReportStateManager
import me.raatiniemi.worker.feature.shared.view.click
import me.raatiniemi.worker.feature.shared.view.longClick

internal class ItemViewHolder(
    private val stateManager: TimeReportStateManager,
    private val formatter: HoursMinutesFormat,
    view: View
) {
    private val itemView: ConstraintLayout = view.findViewById(R.id.clItem)
    private val timeInterval: AppCompatTextView = view.findViewById(R.id.tvTimeInterval)
    private val timeSummary: AppCompatTextView = view.findViewById(R.id.tvTimeSummary)

    fun bind(timeInterval: TimeInterval) {
        this.timeInterval.text = title(timeInterval)

        val hoursMinutes = calculateHoursMinutes(calculateInterval(timeInterval))
        timeSummary.text = formatter.apply(hoursMinutes)

        apply(stateManager.state(timeInterval), itemView)
        longClick(itemView) {
            stateManager.consume(TimeReportLongPressAction.LongPressItem(timeInterval))
            true
        }
        click(itemView) {
            stateManager.consume(TimeReportTapAction.TapItem(timeInterval))
        }
    }
}
