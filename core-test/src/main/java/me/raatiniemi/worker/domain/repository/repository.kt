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

package me.raatiniemi.worker.domain.repository

import java.util.*

/**
 * Use index unless it's above count, in which count will be used.
 */
internal fun indexWithCountCap(index: Int, count: Int): Int {
    return if (index > count) {
        count
    } else {
        index
    }
}

/**
 * Reset timestamp in milliseconds to start of day.
 */
fun resetToStartOfDay(timeInMilliseconds: Long): Date = Calendar.getInstance()
    .apply { timeInMillis = timeInMilliseconds }
    .also {
        it.set(Calendar.HOUR_OF_DAY, 0)
        it.set(Calendar.MINUTE, 0)
        it.set(Calendar.SECOND, 0)
        it.set(Calendar.MILLISECOND, 0)
    }
    .run { time }
