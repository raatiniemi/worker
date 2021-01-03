/*
 * Copyright (C) 2021 Tobias Raatiniemi
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
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Rewind the milliseconds timestamp to the start of the day, including changing
 * the hour, minute, second and millisecond.
 *
 * @param milliseconds Milliseconds timestamp to rewind.
 *
 * @return Rewound milliseconds timestamp.
 */
fun setToStartOfDay(milliseconds: Milliseconds): Milliseconds {
    return Instant.ofEpochMilli(milliseconds.value)
        .atZone(ZoneId.systemDefault())
        .truncatedTo(ChronoUnit.DAYS)
        .toInstant()
        .let { Milliseconds(it.toEpochMilli()) }
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
    return Instant.ofEpochMilli(milliseconds.value)
        .atZone(ZoneId.systemDefault())
        .truncatedTo(ChronoUnit.DAYS)
        .with(DayOfWeek.MONDAY)
        .toInstant()
        .let { Milliseconds(it.toEpochMilli()) }
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
    return (setToStartOfWeek(milliseconds) + 1.weeks) - 1.milliseconds
}
