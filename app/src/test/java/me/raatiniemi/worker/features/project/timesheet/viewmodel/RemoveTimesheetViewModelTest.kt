/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.project.timesheet.viewmodel

import me.raatiniemi.worker.domain.exception.DomainException
import me.raatiniemi.worker.domain.interactor.RemoveTime
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimesheetItem
import me.raatiniemi.worker.features.project.timesheet.model.TimesheetAdapterResult
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import rx.observers.TestSubscriber

@RunWith(JUnit4::class)
class RemoveTimesheetViewModelTest {
    private val useCase = mock(RemoveTime::class.java)
    private val vm = RemoveTimesheetViewModel.ViewModel(useCase)

    private val success = TestSubscriber<TimesheetAdapterResult>()
    private val errors = TestSubscriber<Throwable>()

    @Before
    fun setUp() {
        vm.success().subscribe(success)
        vm.errors().subscribe(errors)
    }

    @Test
    fun remove_withError() {
        val timeInterval = TimeInterval.builder(1L).build()
        val item = TimesheetItem.with(timeInterval)
        val results = listOf(TimesheetAdapterResult(0, 0, item))
        `when`(useCase.execute(eq(listOf(timeInterval))))
                .thenThrow(DomainException::class.java)

        vm.remove(results)

        success.assertNoValues()
        success.assertNoTerminalEvent()
        errors.assertValueCount(1)
        errors.assertNoTerminalEvent()
    }

    @Test
    fun remove_withSingleItem() {
        val timeInterval = TimeInterval.builder(1L).build()
        val results = listOf(TimesheetAdapterResult(0, 0, TimesheetItem.with(timeInterval)))

        vm.remove(results)

        verify(useCase).execute(eq(listOf(timeInterval)))
        success.assertReceivedOnNext(results)
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    @Test
    fun remove_withMultipleItems() {
        val timeInterval = TimeInterval.builder(1L).build()
        val results = listOf(
                TimesheetAdapterResult(0, 0, TimesheetItem.with(timeInterval)),
                TimesheetAdapterResult(0, 1, TimesheetItem.with(timeInterval))
        )

        vm.remove(results)

        verify(useCase).execute(eq(listOf(timeInterval, timeInterval)))
        success.assertReceivedOnNext(results.reversed())
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }
}
