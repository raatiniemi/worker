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

import me.raatiniemi.worker.databinding.FragmentProjectTimeReportItemBinding
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
    private val binding: FragmentProjectTimeReportItemBinding,
    private val stateManager: TimeReportStateManager,
    private val formatter: HoursMinutesFormat
) {
    fun bind(timeInterval: TimeInterval) {
        with(binding) {
            tvTimeInterval.text = title(timeInterval)

            val hoursMinutes = calculateHoursMinutes(calculateInterval(timeInterval))
            tvTimeSummary.text = formatter.apply(hoursMinutes)

            apply(stateManager.state(timeInterval), clItem)
            longClick(clItem) {
                stateManager.consume(TimeReportLongPressAction.LongPressItem(timeInterval))
                true
            }
            click(clItem) {
                stateManager.consume(TimeReportTapAction.TapItem(timeInterval))
            }
        }
    }
}
