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

import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAdapterResult
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import rx.observers.TestSubscriber

@RunWith(JUnit4::class)
class RegisterTimeReportViewModelTest {
    private lateinit var repository: TimeIntervalRepository
    private lateinit var markRegisteredTime: MarkRegisteredTime
    private lateinit var vm: RegisterTimeReportViewModel

    private val success = TestSubscriber<TimeReportAdapterResult>()
    private val errors = TestSubscriber<Throwable>()

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        markRegisteredTime = MarkRegisteredTime(repository)
        vm = RegisterTimeReportViewModel(markRegisteredTime)

        vm.success().subscribe(success)
        vm.errors().subscribe(errors)
    }

    @Test
    fun register_withItem() {
        repository.add(timeInterval { })
        val timeInterval = timeInterval { id = 1 }
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem.with(timeInterval))
        )
        val expected = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem(timeInterval.copy(isRegistered = true)))
        )

        vm.register(results)

        success.assertReceivedOnNext(expected)
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    @Test
    fun register_withItems() {
        repository.add(timeInterval { })
        repository.add(timeInterval { })
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem.with(timeInterval { id = 1 })),
                TimeReportAdapterResult(0, 1, TimeReportItem.with(timeInterval { id = 2 }))
        )
        val expected = results.map {
            TimeReportAdapterResult(
                    it.group,
                    it.child,
                    TimeReportItem(it.timeInterval.copy(isRegistered = true))
            )
        }

        vm.register(results)

        success.assertReceivedOnNext(expected.asReversed())
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }
}
