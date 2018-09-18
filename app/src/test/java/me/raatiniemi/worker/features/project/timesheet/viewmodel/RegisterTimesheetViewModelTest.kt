/*
 * Copyright (C) 2017 Worker Project
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
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime
import me.raatiniemi.worker.domain.model.TimesheetItem
import me.raatiniemi.worker.factory.TimeFactory
import me.raatiniemi.worker.features.project.timesheet.model.TimesheetAdapterResult
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import rx.observers.TestSubscriber

@RunWith(JUnit4::class)
class RegisterTimesheetViewModelTest {
    private val useCase = mock(MarkRegisteredTime::class.java)
    private val vm = RegisterTimesheetViewModel.ViewModel(useCase)

    private val success = TestSubscriber<TimesheetAdapterResult>()
    private val errors = TestSubscriber<Throwable>()

    @Before
    fun setUp() {
        vm.success().subscribe(success)
        vm.errors().subscribe(errors)
    }

    @Test
    fun register_withError() {
        val time = TimeFactory.builder().build()
        val item = TimesheetItem.with(time)
        val results = listOf(TimesheetAdapterResult(0, 0, item))
        `when`(useCase.execute(eq(listOf(time))))
                .thenThrow(DomainException::class.java)

        vm.register(results)

        success.assertNoValues()
        success.assertNoTerminalEvent()
        errors.assertValueCount(1)
        errors.assertNoTerminalEvent()
    }

    @Test
    fun register_withItem() {
        val time = TimeFactory.builder().build()
        val item = TimesheetItem.with(time)
        val results = listOf(TimesheetAdapterResult(0, 0, item))
        `when`(useCase.execute(eq(listOf(time))))
                .thenReturn(listOf(time))

        vm.register(results)

        success.assertReceivedOnNext(results)
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    @Test
    fun register_withItems() {
        val times = listOf(
                TimeFactory.builder().id(1L).build(),
                TimeFactory.builder().id(2L).build()
        )
        val results = listOf(
                TimesheetAdapterResult(0, 0, TimesheetItem.with(times[0])),
                TimesheetAdapterResult(0, 1, TimesheetItem.with(times[1]))
        )
        `when`(useCase.execute(eq(times)))
                .thenReturn(times)

        vm.register(results)

        success.assertReceivedOnNext(results.sorted().reversed())
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }
}
