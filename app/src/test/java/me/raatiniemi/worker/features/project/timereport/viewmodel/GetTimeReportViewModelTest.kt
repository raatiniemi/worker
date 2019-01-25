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

import me.raatiniemi.worker.domain.interactor.GetTimeReport
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import me.raatiniemi.worker.features.project.timereport.model.TimeReportGroup
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import rx.observers.TestSubscriber
import java.util.*

@RunWith(JUnit4::class)
class GetTimeReportViewModelTest {
    private lateinit var repository: TimeReportRepository
    private lateinit var getTimeReport: GetTimeReport

    private val success = TestSubscriber<TimeReportGroup>()
    private val errors = TestSubscriber<Throwable>()

    private fun setUpViewModel(timeIntervals: List<TimeInterval>): GetTimeReportViewModel {
        repository = TimeReportInMemoryRepository(timeIntervals)
        getTimeReport = GetTimeReport(repository)

        val vm = GetTimeReportViewModel(getTimeReport)
        vm.success().subscribe(success)
        vm.errors().subscribe(errors)

        return vm
    }

    @Test
    fun `fetch without time intervals`() {
        val vm = setUpViewModel(emptyList())

        vm.fetch(1, 0)

        success.assertNoValues()
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    @Test
    fun `fetch with hide registered time intervals`() {
        val vm = setUpViewModel(listOf(
                timeInterval {
                    isRegistered = true
                }
        ))
        vm.hideRegisteredTime()

        vm.fetch(1, 0)

        success.assertNoValues()
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    @Test
    fun `fetch with single item`() {
        val vm = setUpViewModel(listOf(
                timeInterval { }
        ))

        vm.fetch(1, 0)

        success.assertValueCount(1)
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }

    @Test
    fun `fetch with multiple items`() {
        val vm = setUpViewModel(listOf(
                timeInterval {
                    startInMilliseconds = 0
                    stopInMilliseconds = 1
                },
                timeInterval {
                    startInMilliseconds = Date().time
                }
        ))

        vm.fetch(1, 0)

        success.assertValueCount(2)
        success.assertNoTerminalEvent()
        errors.assertNoValues()
        errors.assertNoTerminalEvent()
    }
}
