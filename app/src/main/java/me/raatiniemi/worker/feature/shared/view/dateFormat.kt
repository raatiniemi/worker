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

package me.raatiniemi.worker.feature.shared.view

import java.text.SimpleDateFormat
import java.util.*

internal fun yearMonthDayHourMinute(date: Date): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        .run { format(date) }
}

internal fun yearMonthDay(date: Date): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        .run { format(date) }
}

internal fun hourMinute(date: Date): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault())
        .run { format(date) }
}

internal fun week(date: Date): String {
    return SimpleDateFormat("w", Locale.getDefault()).run { format(date) }
}

fun shortDayMonthDayInMonth(date: Date): String {
    return SimpleDateFormat("EEE (MMM d)", Locale.getDefault())
        .run { format(date) }
}
