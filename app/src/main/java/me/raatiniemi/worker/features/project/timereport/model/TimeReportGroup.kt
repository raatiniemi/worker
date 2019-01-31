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

package me.raatiniemi.worker.features.project.timereport.model

import me.raatiniemi.worker.domain.model.HoursMinutes
import me.raatiniemi.worker.domain.model.TimeReportGroup
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import java.util.*

fun TimeReportGroup.getTimeSummaryWithDifference(formatter: HoursMinutesFormat): String {
    val timeSummary = formatter.apply(timeSummary)
    val calculatedDifference = timeDifference

    return String.format(
            Locale.getDefault(),
            "%s%s",
            timeSummary,
            formatTimeDifference(
                    getTimeDifferenceFormat(calculatedDifference),
                    formatter.apply(calculatedDifference)
            )
    )
}

private fun formatTimeDifference(format: String, difference: String): String {
    return String.format(Locale.getDefault(), format, difference)
}

private fun getTimeDifferenceFormat(difference: HoursMinutes): String {
    if (difference.empty) {
        return ""
    }

    if (difference.positive) {
        return " (+%s)"
    }

    return " (%s)"
}

fun TimeReportGroup.buildItemResultsWithGroupIndex(groupIndex: Int): List<TimeReportAdapterResult> {
    return items.mapIndexed { childIndex, item ->
        TimeReportAdapterResult(groupIndex, childIndex, item)
    }
}
