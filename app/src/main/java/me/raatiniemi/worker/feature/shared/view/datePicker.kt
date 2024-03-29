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

import android.widget.DatePicker
import me.raatiniemi.worker.domain.time.*

internal fun update(datePicker: DatePicker, yearsMonthsDays: YearsMonthsDays) {
    val (years, months, days) = yearsMonthsDays
    datePicker.updateDate(years.value, months.value, days.value)
}

internal fun change(datePicker: DatePicker, change: (YearsMonthsDays) -> Unit) {
    datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
        change(yearsMonthsDays(Years(year), Months(monthOfYear), Days(dayOfMonth)))
    }
}
