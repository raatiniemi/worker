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

import android.annotation.SuppressLint
import android.view.View
import me.raatiniemi.worker.domain.date.HoursMinutesFormat
import me.raatiniemi.worker.domain.time.HoursMinutes
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.feature.projects.timereport.model.TimeReportState
import me.raatiniemi.worker.feature.shared.view.shortDayMonthDayInMonth
import me.raatiniemi.worker.feature.shared.view.week
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val FORMAT_TIME_SUMMARY = "%s%s"
private const val FORMAT_FOR_EMPTY = ""
private const val FORMAT_FOR_POSITIVE = " (+%s)"
private const val FORMAT_FOR_NEGATIVE = " (%s)"
private const val SEPARATOR = " - "

private val timeFormat = SimpleDateFormat("HH:mm", Locale.forLanguageTag("en_US"))

internal fun week(week: TimeReportWeek) = week(Date(week.start.value))

@SuppressLint("DefaultLocale")
internal fun title(day: TimeReportDay) = shortDayMonthDayInMonth(Date(day.milliseconds.value))
    .replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(Locale.getDefault())
        } else {
            it.toString()
        }
    }

internal fun firstLetter(text: CharSequence): Char {
    return text.first()
}

internal fun timeSummaryWithDifference(
    day: TimeReportDay,
    formatter: HoursMinutesFormat
): String {
    val calculatedDifference = day.timeDifference
    return String.format(
        Locale.getDefault(),
        FORMAT_TIME_SUMMARY,
        formatter.apply(day.timeSummary),
        formatTimeDifference(
            timeDifferenceFormat(calculatedDifference),
            formatter.apply(calculatedDifference)
        )
    )
}

private fun formatTimeDifference(format: String, difference: String): String {
    return String.format(Locale.getDefault(), format, difference)
}

private fun timeDifferenceFormat(difference: HoursMinutes) = when {
    difference.empty -> FORMAT_FOR_EMPTY
    difference.positive -> FORMAT_FOR_POSITIVE
    else -> FORMAT_FOR_NEGATIVE
}

internal fun title(timeInterval: TimeInterval, dateFormat: DateFormat = timeFormat): String {
    val format = format(dateFormat)
    val join = join(SEPARATOR)

    return when (timeInterval) {
        is TimeInterval.Active -> format(timeInterval.start)
        is TimeInterval.Inactive -> {
            val start = format(timeInterval.start)
            val stop = format(timeInterval.stop)

            join(start to stop)
        }
        is TimeInterval.Registered -> {
            val start = format(timeInterval.start)
            val stop = format(timeInterval.stop)

            join(start to stop)
        }
    }
}

private fun format(dateFormat: DateFormat): (Milliseconds) -> String = { milliseconds ->
    dateFormat.format(convertToDate(milliseconds))
}

private fun convertToDate(milliseconds: Milliseconds) = Date(milliseconds.value)

private fun join(
    @Suppress("SameParameterValue") separator: String
): (Pair<String, String>) -> String = {
    "${it.first}$separator${it.second}"
}

internal fun apply(state: TimeReportState, view: View) = when (state) {
    TimeReportState.SELECTED -> {
        view.isSelected = true
        view.isActivated = false
    }
    TimeReportState.REGISTERED -> {
        view.isSelected = false
        view.isActivated = true
    }
    TimeReportState.EMPTY -> {
        view.isSelected = false
        view.isActivated = false
    }
}
