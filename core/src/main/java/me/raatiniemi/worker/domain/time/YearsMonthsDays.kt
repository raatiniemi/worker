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

import java.util.*

data class YearsMonthsDays internal constructor(
    val years: Years,
    val months: Months,
    val days: Days
)

inline class Years(val value: Int)
inline class Months(val value: Int)
inline class Days(val value: Int)

fun yearsMonthsDays(years: Years, months: Months, days: Days): YearsMonthsDays {
    return YearsMonthsDays(years, months, days)
}

fun date(date: Date, yearsMonthsDays: YearsMonthsDays): Date {
    return Calendar.getInstance()
        .also { it.timeInMillis = date.time }
        .apply {
            val (years, months, days) = yearsMonthsDays
            set(Calendar.YEAR, years.value)
            set(Calendar.MONTH, months.value)
            set(Calendar.DAY_OF_MONTH, days.value)
        }
        .run { time }
}

fun yearsMonthsDays(date: Date): YearsMonthsDays {
    return Calendar.getInstance()
        .also { it.timeInMillis = date.time }
        .run {
            YearsMonthsDays(
                years = Years(get(Calendar.YEAR)),
                months = Months(get(Calendar.MONTH)),
                days = Days(get(Calendar.DAY_OF_MONTH))
            )
        }
}
