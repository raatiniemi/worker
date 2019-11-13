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

package me.raatiniemi.worker.features.shared.datetime.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import me.raatiniemi.worker.domain.date.minus
import me.raatiniemi.worker.domain.date.plus
import me.raatiniemi.worker.domain.time.hours
import me.raatiniemi.worker.domain.time.hoursMinutes
import me.raatiniemi.worker.domain.time.weeks
import me.raatiniemi.worker.domain.time.yearsMonthsDays
import me.raatiniemi.worker.features.shared.datetime.model.DateTimeConfiguration
import me.raatiniemi.worker.features.shared.datetime.model.DateTimeViewActions
import me.raatiniemi.worker.features.shared.model.observeNoValue
import me.raatiniemi.worker.features.shared.model.observeNonNull
import me.raatiniemi.worker.features.shared.view.hourMinute
import me.raatiniemi.worker.features.shared.view.yearMonthDay
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class DateTimeViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var vm: DateTimeViewModel

    @Before
    fun setUp() {
        vm = DateTimeViewModel()
    }

    // Min. date

    @Test
    fun `min date without configuration`() {
        vm.minDate.observeNoValue()
    }

    @Test
    fun `min date with empty date`() {
        val configuration = DateTimeConfiguration()

        vm.configure(configuration)

        vm.minDate.observeNoValue()
    }

    @Test
    fun `min date with min date`() {
        val minDate = Date() - 1.hours
        val configuration = DateTimeConfiguration(
            minDate = minDate
        )
        val expected = minDate.time

        vm.configure(configuration)

        vm.minDate.observeNonNull {
            assertEquals(expected, it)
        }
    }

    // Max. date

    @Test
    fun `max date without configuration`() {
        vm.maxDate.observeNoValue()
    }

    @Test
    fun `max date with empty min date`() {
        val configuration = DateTimeConfiguration()

        vm.configure(configuration)

        vm.maxDate.observeNoValue()
    }

    @Test
    fun `max date with date`() {
        val maxDate = Date() + 1.hours
        val configuration = DateTimeConfiguration(
            maxDate = maxDate
        )
        val expected = maxDate.time

        vm.configure(configuration)

        vm.maxDate.observeNonNull {
            assertEquals(expected, it)
        }
    }

    // Date

    @Test
    fun `date without configuration`() {
        vm.date.observeNoValue()
    }

    @Test
    fun date() {
        val configuration = DateTimeConfiguration()
        val expected = yearMonthDay(configuration.date)

        vm.configure(configuration)

        vm.date.observeNonNull {
            assertEquals(expected, it)
        }
    }

    // Time

    @Test
    fun `time without configuration`() {
        vm.time.observeNoValue()
    }

    @Test
    fun time() {
        val configuration = DateTimeConfiguration()
        val expected = hourMinute(configuration.date)

        vm.configure(configuration)

        vm.time.observeNonNull {
            assertEquals(expected, it)
        }
    }

    // Choose date

    @Test
    fun `choose date`() {
        vm.chooseDate()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.ChooseDate, it)
        }
    }

    @Test
    fun `choose date without date`() {
        val now = yearsMonthsDays(Date())

        vm.chooseDate(now)

        vm.date.observeNoValue()
    }

    @Test
    fun `choose date with date`() {
        vm.configure(DateTimeConfiguration())
        val lastWeek = Date() - 1.weeks
        val expected = yearMonthDay(lastWeek)

        vm.chooseDate(yearsMonthsDays(lastWeek))

        vm.date.observeNonNull {
            assertEquals(expected, it)
        }
    }

    // Choose time

    @Test
    fun `choose time`() {
        vm.chooseTime()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.ChooseTime, it)
        }
    }

    @Test
    fun `choose time without date`() {
        val now = yearsMonthsDays(Date())

        vm.chooseDate(now)

        vm.date.observeNoValue()
    }

    @Test
    fun `choose time with date`() {
        vm.configure(DateTimeConfiguration())
        val lastHour = Date() - 1.hours
        val expected = hourMinute(lastHour)

        vm.chooseTime(hoursMinutes(lastHour))

        vm.time.observeNonNull {
            assertEquals(expected, it)
        }
    }
}
