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

package me.raatiniemi.worker.features.project.timereport.viewmodel

import me.raatiniemi.worker.domain.exception.DomainException
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimesheetItem
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAdapterResult
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

    private val success = TestSubscriber<TimeReportAdapterResult>()
    private val errors = TestSubscriber<Throwable>()

    @Before
    fun setUp() {
        vm.success().subscribe(success)
        vm.errors().subscribe(errors)
    }

    @Test
    fun register_withError() {
        val timeInterval = TimeInterval.builder(1L).build()
        val item = TimesheetItem.with(timeInterval)
        val results = listOf(TimeReportAdapterResult(0, 0, item))
        `when`(useCase.execute(eq(listOf(timeInterval))))
                .thenThrow(DomainException::class.java)

        vm.register(results)

        success.assertNoValues()
        success.assertNoTerminalEvent()
        errors.assertValueCount(1)
        errors.assertNoTerminalEvent()
    }

    @Test
    fun register_withItem() {
        val timeInterval = TimeInterval.builder(1L).build()
        val item = TimesheetItem.with(timeInterval)
        val results = listOf(TimeReportAdapterResult(0, 0, item))
        `when`(useCase.execute(eq(listOf(timeInterval))))
                .thenReturn(listOf(timeInterval))

        vm.register(results)

        success.assertReceivedOnNext(results)
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    @Test
    fun register_withItems() {
        val times = listOf(
                TimeInterval.builder(1L).id(1L).build(),
                TimeInterval.builder(1L).id(2L).build()
        )
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimesheetItem.with(times[0])),
                TimeReportAdapterResult(0, 1, TimesheetItem.with(times[1]))
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
