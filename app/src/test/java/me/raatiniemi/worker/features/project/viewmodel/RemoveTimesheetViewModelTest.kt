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

package me.raatiniemi.worker.features.project.viewmodel

import me.raatiniemi.worker.domain.exception.DomainException
import me.raatiniemi.worker.domain.interactor.RemoveTime
import me.raatiniemi.worker.domain.model.TimesheetItem
import me.raatiniemi.worker.factory.TimeFactory
import me.raatiniemi.worker.features.project.model.TimesheetAdapterResult
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
        val time = TimeFactory.builder().build()
        val item = TimesheetItem.with(time)
        val results = listOf(TimesheetAdapterResult(0, 0, item))
        `when`(useCase.execute(eq(listOf(time))))
                .thenThrow(DomainException::class.java)

        vm.remove(results)

        success.assertNoValues()
        success.assertNoTerminalEvent()
        errors.assertValueCount(1)
        errors.assertNoTerminalEvent()
    }

    @Test
    fun remove_withSingleItem() {
        val time = TimeFactory.builder().build()
        val results = listOf(TimesheetAdapterResult(0, 0, TimesheetItem.with(time)))

        vm.remove(results)

        verify(useCase).execute(eq(listOf(time)))
        success.assertReceivedOnNext(results)
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    @Test
    fun remove_withMultipleItems() {
        val time = TimeFactory.builder().build()
        val results = listOf(
                TimesheetAdapterResult(0, 0, TimesheetItem.with(time)),
                TimesheetAdapterResult(0, 1, TimesheetItem.with(time))
        )

        vm.remove(results)

        verify(useCase).execute(eq(listOf(time, time)))
        success.assertReceivedOnNext(results.reversed())
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }
}
