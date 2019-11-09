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

package me.raatiniemi.worker.domain.time

import java.time.DayOfWeek
import java.util.*

/**
 * Rewind the milliseconds timestamp to the start of the day, including changing
 * the hour, minute, second and millisecond.
 *
 * @param milliseconds Milliseconds timestamp to rewind.
 * @param timeZone Time zone used when rewinding.
 *
 * @return Rewound milliseconds timestamp.
 */
fun setToStartOfDay(
    milliseconds: Milliseconds,
    timeZone: TimeZone = TimeZone.getDefault()
): Milliseconds {
    return Calendar.getInstance()
        .apply { timeInMillis = milliseconds.value }
        .also { calendar ->
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeZone = timeZone
        }
        .let { Milliseconds(it.time.time) }
}

/**
 * Rewind the milliseconds timestamp to the start of the week, including changing
 * the hour, minute, second and millisecond.
 *
 * @param milliseconds Milliseconds timestamp to rewind.
 * @param timeZone Time zone used when rewinding.
 *
 * @return Rewound milliseconds timestamp.
 */
fun setToStartOfWeek(
    milliseconds: Milliseconds,
    timeZone: TimeZone = TimeZone.getDefault()
): Milliseconds {
    return Calendar.getInstance()
        .apply { timeInMillis = milliseconds.value }
        .also { calendar ->
            calendar.firstDayOfWeek = DayOfWeek.MONDAY.value
            calendar.set(Calendar.DAY_OF_WEEK, DayOfWeek.MONDAY.value)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeZone = timeZone
        }
        .let { Milliseconds(it.time.time) }
}

/**
 * Forwards the milliseconds timestamp to the end of the week, including changing
 * the hour, minute, second and millisecond.
 *
 * @param milliseconds Milliseconds timestamp to forward.
 * @param timeZone Time zone used when forwarding.
 *
 * @return Forwarded milliseconds timestamp.
 */
fun setToEndOfWeek(
    milliseconds: Milliseconds,
    timeZone: TimeZone = TimeZone.getDefault()
): Milliseconds {
    return (setToStartOfWeek(milliseconds, timeZone) + 1.weeks) - 1.seconds
}
