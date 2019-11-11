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

    // Choose time

    @Test
    fun `choose time`() {
        vm.chooseTime()

        vm.viewActions.observeNonNull {
            assertEquals(DateTimeViewActions.ChooseTime, it)
        }
    }
}
