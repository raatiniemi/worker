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
import me.raatiniemi.worker.domain.interactor.GetTimesheet
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimesheetItem
import me.raatiniemi.worker.features.project.timereport.model.TimesheetGroup
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import rx.observers.TestSubscriber
import java.util.*

@RunWith(JUnit4::class)
class GetTimesheetViewModelTest {
    private val useCase = mock(GetTimesheet::class.java)
    private val vm = GetTimesheetViewModel.ViewModel(useCase)

    private val success = TestSubscriber<TimesheetGroup>()
    private val errors = TestSubscriber<Throwable>()

    @Before
    fun setUp() {
        vm.success().subscribe(success)
        vm.errors().subscribe(errors)
    }

    @Test
    fun fetch_withError() {
        `when`(useCase.execute(eq(1L), eq(0), eq(false)))
                .thenThrow(DomainException::class.java)

        vm.fetch(1L, 0)

        success.assertNoValues()
        success.assertNoTerminalEvent()
        errors.assertValueCount(1)
        errors.assertNoTerminalEvent()
    }

    @Test
    fun fetch_hideRegisteredTime() {
        `when`(useCase.execute(eq(1L), eq(0), eq(true)))
                .thenReturn(TreeMap<Date, SortedSet<TimesheetItem>>())

        vm.hideRegisteredTime()
        vm.fetch(1L, 0)

        success.assertNoValues()
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    @Test
    fun fetch_withEmptyResult() {
        `when`(useCase.execute(eq(1L), eq(0), eq(false)))
                .thenReturn(TreeMap<Date, SortedSet<TimesheetItem>>())

        vm.fetch(1L, 0)

        success.assertNoValues()
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    @Test
    fun fetch_withSingleItem() {
        `when`(useCase.execute(eq(1L), eq(0), eq(false)))
                .thenReturn(buildTimesheetSegment())

        vm.fetch(1L, 0)

        success.assertValueCount(1)
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    @Test
    fun fetch_withMultipleItems() {
        `when`(useCase.execute(eq(1L), eq(0), eq(false)))
                .thenReturn(buildTimesheetSegmentWithNumberOfItems(2))

        vm.fetch(1L, 0)

        success.assertValueCount(2)
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    private fun buildTimesheetSegment(): SortedMap<Date, SortedSet<TimesheetItem>> {
        return buildTimesheetSegmentWithNumberOfItems(1)
    }

    private fun buildTimesheetSegmentWithNumberOfItems(numberOfItems: Int): SortedMap<Date, SortedSet<TimesheetItem>> {
        val segments = TreeMap<Date, SortedSet<TimesheetItem>>()
        for (i in 0 until numberOfItems) {
            segments[Date(i.toLong())] = timesheetItems
        }

        return segments
    }

    private val timesheetItems: TreeSet<TimesheetItem>
        get() {
            val timeInterval = TimeInterval.builder(1L).build()
            val item = TimesheetItem.with(timeInterval)

            return TreeSet(setOf(item))
        }
}
