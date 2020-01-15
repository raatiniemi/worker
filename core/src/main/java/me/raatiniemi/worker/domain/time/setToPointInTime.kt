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

package me.raatiniemi.worker.domain.time

import java.util.*

/**
 * Rewind the milliseconds timestamp to the start of the day, including changing
 * the hour, minute, second and millisecond.
 *
 * @param milliseconds Milliseconds timestamp to rewind.
 *
 * @return Rewound milliseconds timestamp.
 */
fun setToStartOfDay(milliseconds: Milliseconds): Milliseconds {
    val calendar = calendar {
        it.timeInMillis = milliseconds.value
        it.set(Calendar.HOUR_OF_DAY, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
    }

    return Milliseconds(calendar.time.time)
}

/**
 * Rewind the milliseconds timestamp to the start of the week, including changing
 * the hour, minute, second and millisecond.
 *
 * @param milliseconds Milliseconds timestamp to rewind.
 *
 * @return Rewound milliseconds timestamp.
 */
fun setToStartOfWeek(milliseconds: Milliseconds): Milliseconds {
    val startOfDay = setToStartOfDay(milliseconds)
    val calendar = calendar {
        it.timeInMillis = startOfDay.value
        it.set(Calendar.DAY_OF_WEEK, it.firstDayOfWeek)
    }

    return Milliseconds(calendar.time.time)
}

/**
 * Forwards the milliseconds timestamp to the end of the week, including changing
 * the hour, minute, second and millisecond.
 *
 * @param milliseconds Milliseconds timestamp to forward.
 *
 * @return Forwarded milliseconds timestamp.
 */
fun setToEndOfWeek(milliseconds: Milliseconds): Milliseconds {
    return (setToStartOfWeek(milliseconds) + 1.weeks) - 1.seconds
}
