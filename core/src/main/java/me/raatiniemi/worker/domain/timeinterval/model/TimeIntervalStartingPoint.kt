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

package me.raatiniemi.worker.domain.timeinterval.model

import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.calendar
import me.raatiniemi.worker.domain.time.setToStartOfDay
import me.raatiniemi.worker.domain.timeinterval.usecase.InvalidStartingPointException
import java.util.*

enum class TimeIntervalStartingPoint(val rawValue: Int) {
    DAY(0), WEEK(1), MONTH(2);

    fun calculateMilliseconds(): Milliseconds {
        val calendar = calendar {
            it.timeInMillis = setToStartOfDay(Milliseconds.now).value

            when (this) {
                DAY -> Unit
                WEEK -> it.set(Calendar.DAY_OF_WEEK, it.firstDayOfWeek)
                MONTH -> it.set(Calendar.DAY_OF_MONTH, 1)
            }
        }

        return Milliseconds(calendar.timeInMillis)
    }

    companion object {
        @JvmStatic
        fun from(startingPoint: Int): TimeIntervalStartingPoint {
            return when (startingPoint) {
                DAY.rawValue -> DAY
                WEEK.rawValue -> WEEK
                MONTH.rawValue -> MONTH
                else -> throw InvalidStartingPointException(
                    "Starting point '$startingPoint' is not valid"
                )
            }
        }
    }
}
