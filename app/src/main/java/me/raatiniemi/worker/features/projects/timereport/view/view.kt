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

package me.raatiniemi.worker.features.projects.timereport.view

import me.raatiniemi.worker.domain.model.Milliseconds
import me.raatiniemi.worker.domain.model.TimeInterval
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val separator = " - "
private val timeFormat = SimpleDateFormat("HH:mm", Locale.forLanguageTag("en_US"))

internal fun title(timeInterval: TimeInterval, dateFormat: DateFormat = timeFormat): String {
    val format = format(dateFormat)
    val join = join(separator)

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

private fun join(separator: String): (Pair<String, String>) -> String = {
    "${it.first}$separator${it.second}"
}
