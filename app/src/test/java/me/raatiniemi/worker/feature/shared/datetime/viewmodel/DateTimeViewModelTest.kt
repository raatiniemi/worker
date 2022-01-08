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

package me.raatiniemi.worker.feature.shared.datetime.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import me.raatiniemi.worker.domain.date.minus
import me.raatiniemi.worker.domain.date.plus
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.feature.shared.datetime.model.DateTimeConfiguration
import me.raatiniemi.worker.feature.shared.datetime.model.DateTimeViewActions
import me.raatiniemi.worker.feature.shared.model.observeNoValue
import me.raatiniemi.worker.feature.shared.model.observeNonNull
import me.raatiniemi.worker.feature.shared.view.hourMinute
import me.raatiniemi.worker.feature.shared.view.yearMonthDay
import me.raatiniemi.worker.koin.testKoinModules
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import java.util.*

@RunWith(JUnit4::class)
class DateTimeViewModelTest : AutoCloseKoinTest() {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val vm by inject<DateTimeViewModel>()

    @Before
    fun setUp() {
        startKoin {
            modules(testKoinModules)
        }
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
    fun `choose date with date before min date`() {
        val now = Date()
        val yesterday = now - 1.days
        val configuration = DateTimeConfiguration(
            date = now,
            minDate = now
        )
        vm.configure(configuration)
        val expected = yearMonthDay(yesterday)

        vm.chooseDate(yearsMonthsDays(yesterday))

        vm.viewActions.observeNoValue()
        vm.date.observeNonNull {
            assertEquals(expected, it)
        }
    }

    @Test
    fun `choose date with date after max date`() {
        val now = Date()
        val tomorrow = now + 1.days
        val configuration = DateTimeConfiguration(
            date = now,
            maxDate = now
        )
        vm.configure(configuration)
        val expected = yearMonthDay(tomorrow)

        vm.chooseDate(yearsMonthsDays(tomorrow))

        vm.viewActions.observeNoValue()
        vm.date.observeNonNull {
            assertEquals(expected, it)
        }
    }

    @Test
    fun `choose date with valid date`() {
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
    fun `choose time with date before min date`() {
        val now = Date()
        val lastHour = now - 1.hours
        val configuration = DateTimeConfiguration(
            date = now,
            minDate = now
        )
        vm.configure(configuration)
        val expected = hourMinute(lastHour)

        vm.chooseTime(hoursMinutes(lastHour))

        vm.viewActions.observeNoValue()
        vm.time.observeNonNull {
            assertEquals(expected, it)
        }
    }

    @Test
    fun `choose time with date after max date`() {
        val now = Date()
        val nextHour = now + 1.hours
        val configuration = DateTimeConfiguration(
            date = now,
            maxDate = now
        )
        vm.configure(configuration)
        val expected = hourMinute(nextHour)

        vm.chooseTime(hoursMinutes(nextHour))

        vm.viewActions.observeNoValue()
        vm.time.observeNonNull {
            assertEquals(expected, it)
        }
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

    // Choose

    @Test
    fun `choose with initial values`() {
        val now = Date()
        val configuration = DateTimeConfiguration(
            date = now
        )
        vm.configure(configuration)

        vm.choose()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.Choose(now), it)
        }
    }

    @Test
    fun `choose with chosen date and date is before min date`() {
        val now = Date()
        val yesterday = now - 1.days
        val lastWeek = now - 7.days
        val configuration = DateTimeConfiguration(
            date = now,
            minDate = yesterday
        )
        vm.configure(configuration)
        vm.chooseDate(yearsMonthsDays(lastWeek))

        vm.choose()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.DateTimeIsIsBeforeAllowedInterval(yesterday), it)
        }
    }

    @Test
    fun `choose with chosen date and date is after max date`() {
        val now = Date()
        val tomorrow = now - 1.days
        val nextWeek = now + 7.days
        val configuration = DateTimeConfiguration(
            date = now,
            maxDate = tomorrow
        )
        vm.configure(configuration)
        vm.chooseDate(yearsMonthsDays(nextWeek))

        vm.choose()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.DateTimeIsIsAfterAllowedInterval(tomorrow), it)
        }
    }

    @Test
    fun `choose with chosen date`() {
        val now = Date()
        val yearsMonthsDays = yearsMonthsDays(Years(2015), Months(0), Days(18))
        val configuration = DateTimeConfiguration(
            date = now
        )
        vm.configure(configuration)
        vm.chooseDate(yearsMonthsDays)
        val expected = date(now, yearsMonthsDays)

        vm.choose()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.Choose(expected), it)
        }
    }

    @Test
    fun `choose with chosen time and time is before min date`() {
        val midnight = Date(setToStartOfDay(Milliseconds.now).value)
        val noon = midnight + 12.hours
        val sevenInTheMorning = midnight + 7.hours
        val configuration = DateTimeConfiguration(
            date = midnight,
            minDate = noon
        )
        vm.configure(configuration)
        vm.chooseTime(hoursMinutes(sevenInTheMorning))

        vm.choose()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.DateTimeIsIsBeforeAllowedInterval(noon), it)
        }
    }

    @Test
    fun `choose with chosen time and time is after max date`() {
        val midnight = Date(setToStartOfDay(Milliseconds.now).value)
        val noon = midnight + 12.hours
        val sevenInTheEvening = midnight + 19.hours
        val configuration = DateTimeConfiguration(
            date = midnight,
            maxDate = noon
        )
        vm.configure(configuration)
        vm.chooseTime(hoursMinutes(sevenInTheEvening))

        vm.choose()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.DateTimeIsIsAfterAllowedInterval(noon), it)
        }
    }

    @Test
    fun `choose with chosen time`() {
        val now = Date()
        val hoursMinutes = hoursMinutes(Hours(0), Minutes(0))
        val configuration = DateTimeConfiguration(
            date = now
        )
        vm.configure(configuration)
        vm.chooseTime(hoursMinutes)
        val expected = date(now, hoursMinutes)

        vm.choose()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.Choose(expected), it)
        }
    }

    @Test
    fun `choose with chosen date and time`() {
        val now = Date()
        val yearsMonthsDays = yearsMonthsDays(Years(2015), Months(0), Days(18))
        val hoursMinutes = hoursMinutes(Hours(0), Minutes(0))
        val configuration = DateTimeConfiguration(
            date = now
        )
        vm.configure(configuration)
        vm.chooseDate(yearsMonthsDays)
        vm.chooseTime(hoursMinutes)
        val expected = date(date(now, yearsMonthsDays), hoursMinutes)

        vm.choose()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.Choose(expected), it)
        }
    }

    // Dismiss

    @Test
    fun dismiss() {
        vm.dismiss()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.Dismiss, it)
        }
    }
}
