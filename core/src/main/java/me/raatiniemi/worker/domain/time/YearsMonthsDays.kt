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

import java.util.*

data class YearsMonthsDays internal constructor(
    val years: Years,
    val months: Months,
    val days: Days
)


@JvmInline
value class Years(val value: Int)

@JvmInline
value class Months(val value: Int)

@JvmInline
value class Days(val value: Int)

fun yearsMonthsDays(years: Years, months: Months, days: Days): YearsMonthsDays {
    return YearsMonthsDays(years, months, days)
}

fun date(date: Date, yearsMonthsDays: YearsMonthsDays): Date {
    val calendar = calendar {
        it.timeInMillis = date.time
        val (years, months, days) = yearsMonthsDays
        it.set(Calendar.YEAR, years.value)
        it.set(Calendar.MONTH, months.value)
        it.set(Calendar.DAY_OF_MONTH, days.value)
    }

    return calendar.time
}

fun yearsMonthsDays(date: Date): YearsMonthsDays {
    val calendar = calendar {
        it.timeInMillis = date.time
    }

    return YearsMonthsDays(
        years = Years(calendar.get(Calendar.YEAR)),
        months = Months(calendar.get(Calendar.MONTH)),
        days = Days(calendar.get(Calendar.DAY_OF_MONTH))
    )
}
