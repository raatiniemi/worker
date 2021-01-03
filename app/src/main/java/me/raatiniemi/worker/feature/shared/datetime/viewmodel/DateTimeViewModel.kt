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

package me.raatiniemi.worker.feature.shared.datetime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import me.raatiniemi.worker.domain.time.HoursMinutes
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.YearsMonthsDays
import me.raatiniemi.worker.domain.time.date
import me.raatiniemi.worker.feature.shared.datetime.model.DateTimeConfiguration
import me.raatiniemi.worker.feature.shared.datetime.model.DateTimeViewActions
import me.raatiniemi.worker.feature.shared.model.ConsumableLiveData
import me.raatiniemi.worker.feature.shared.model.consume
import me.raatiniemi.worker.feature.shared.model.plusAssign
import me.raatiniemi.worker.feature.shared.view.hourMinute
import me.raatiniemi.worker.feature.shared.view.yearMonthDay
import java.util.*

internal class DateTimeViewModel : ViewModel() {
    private val _minDate = MutableLiveData<Long>()
    val minDate: LiveData<Long> = _minDate

    private val _maxDate = MutableLiveData<Long>()
    val maxDate: LiveData<Long> = _maxDate

    private val _date = MutableLiveData<Date>()

    val date: LiveData<String> = _date.map(::yearMonthDay)
    val time: LiveData<String> = _date.map(::hourMinute)

    val viewActions = ConsumableLiveData<DateTimeViewActions>()

    fun configure(configuration: DateTimeConfiguration) {
        configuration.minDate?.run {
            _minDate += time
        }
        configuration.maxDate?.run {
            _maxDate += time
        }

        _date += configuration.date
    }

    fun chooseDate() {
        viewActions += DateTimeViewActions.ChooseDate
    }

    fun chooseDate(yearsMonthsDays: YearsMonthsDays) {
        consume(_date) { date ->
            _date += date(date, yearsMonthsDays)
        }
    }

    fun chooseTime() {
        viewActions += DateTimeViewActions.ChooseTime
    }

    fun chooseTime(hoursMinutes: HoursMinutes) {
        consume(_date) { date ->
            _date += date(date, hoursMinutes)
        }
    }

    fun choose() {
        consume(_date) { date ->
            viewActions += try {
                DateTimeViewActions.Choose(validate(date))
            } catch (e: DateTimeIsBeforeAllowedIntervalException) {
                DateTimeViewActions.DateTimeIsIsBeforeAllowedInterval(Date(e.minimumAllowedDate.value))
            } catch (e: DateTimeIsAfterAllowedIntervalException) {
                DateTimeViewActions.DateTimeIsIsAfterAllowedInterval(Date(e.minimumAllowedDate.value))
            }
        }
    }

    private fun validate(date: Date): Date {
        consume(minDate) { milliseconds ->
            if (date.time < milliseconds) {
                throw DateTimeIsBeforeAllowedIntervalException(Milliseconds(milliseconds))
            }
        }

        consume(maxDate) { milliseconds ->
            if (date.time > milliseconds) {
                throw DateTimeIsAfterAllowedIntervalException(Milliseconds(milliseconds))
            }
        }

        return date
    }
}
