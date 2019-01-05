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

package me.raatiniemi.worker.features.projects.viewmodel

import me.raatiniemi.worker.domain.interactor.ClockIn
import me.raatiniemi.worker.domain.interactor.ClockOut
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.features.projects.model.ProjectsItem
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import rx.observers.TestSubscriber
import java.util.*

@RunWith(JUnit4::class)
class ClockActivityViewModelTest {
    private val timeIntervalRepository = TimeIntervalInMemoryRepository()
    private val clockIn = ClockIn(timeIntervalRepository)
    private val clockOut = ClockOut(timeIntervalRepository)
    private val getProjectTimeSince = GetProjectTimeSince(timeIntervalRepository)

    private val clockInSuccess = TestSubscriber<ProjectsItemAdapterResult>()
    private val clockInError = TestSubscriber<Throwable>()

    private val clockOutSuccess = TestSubscriber<ProjectsItemAdapterResult>()
    private val clockOutError = TestSubscriber<Throwable>()
    private val project = Project.from(1L, "Project #1")

    private lateinit var vm: ClockActivityViewModel.ViewModel

    @Before
    fun setUp() {
        vm = ClockActivityViewModel.ViewModel(clockIn, clockOut, getProjectTimeSince)

        vm.output().clockInSuccess().subscribe(clockInSuccess)
        vm.error().clockInError().subscribe(clockInError)

        vm.output().clockOutSuccess().subscribe(clockOutSuccess)
        vm.error().clockOutError().subscribe(clockOutError)
    }

    @Test
    fun clockIn_withError() {
        val timeInterval = TimeInterval.builder(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(0)
                .build()
        timeIntervalRepository.add(timeInterval)
        val projectsItem = ProjectsItem.from(project, listOf(timeInterval))
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.input().clockIn(result, Date())

        clockInSuccess.assertNoValues()
        clockInError.assertValueCount(1)
        clockOutSuccess.assertNoValues()
        clockOutError.assertNoValues()
        clockInSuccess.assertNoTerminalEvent()
        clockInError.assertNoTerminalEvent()
        clockOutSuccess.assertNoTerminalEvent()
        clockOutError.assertNoTerminalEvent()
    }

    @Test
    fun clockIn() {
        val projectsItem = ProjectsItem.from(project, emptyList())
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.input().clockIn(result, Date())

        clockInSuccess.assertValueCount(1)
        clockInError.assertNoValues()
        clockOutSuccess.assertNoValues()
        clockOutError.assertNoValues()
        clockInSuccess.assertNoTerminalEvent()
        clockInError.assertNoTerminalEvent()
        clockOutSuccess.assertNoTerminalEvent()
        clockOutError.assertNoTerminalEvent()
        verifyProjectStatus(clockInSuccess.onNextEvents, true)
    }

    @Test
    fun clockIn_withDifferentStartingPoint() {
        val projectsItem = ProjectsItem.from(project, emptyList())
        val result = ProjectsItemAdapterResult(0, projectsItem)
        vm.input().startingPointForTimeSummary(TimeIntervalStartingPoint.DAY.rawValue)

        vm.input().clockIn(result, Date())

        clockInSuccess.assertValueCount(1)
        clockInError.assertNoValues()
        clockOutSuccess.assertNoValues()
        clockOutError.assertNoValues()
        clockInSuccess.assertNoTerminalEvent()
        clockInError.assertNoTerminalEvent()
        clockOutSuccess.assertNoTerminalEvent()
        clockOutError.assertNoTerminalEvent()
        verifyProjectStatus(clockInSuccess.onNextEvents, true)
    }

    @Test
    fun clockIn_withInvalidStartingPoint() {
        val projectsItem = ProjectsItem.from(project, emptyList())
        val result = ProjectsItemAdapterResult(0, projectsItem)
        vm.input().startingPointForTimeSummary(-1)

        vm.input().clockIn(result, Date())

        clockInSuccess.assertValueCount(1)
        clockInError.assertNoValues()
        clockOutSuccess.assertNoValues()
        clockOutError.assertNoValues()
        clockInSuccess.assertNoTerminalEvent()
        clockInError.assertNoTerminalEvent()
        clockOutSuccess.assertNoTerminalEvent()
        clockOutError.assertNoTerminalEvent()
        verifyProjectStatus(clockInSuccess.onNextEvents, true)
    }

    @Test
    fun clockOut_withError() {
        val projectsItem = ProjectsItem.from(project, emptyList())
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.input().clockOut(result, Date())

        clockInSuccess.assertNoValues()
        clockInError.assertNoValues()
        clockOutSuccess.assertNoValues()
        clockOutError.assertValueCount(1)
        clockInSuccess.assertNoTerminalEvent()
        clockInError.assertNoTerminalEvent()
        clockOutSuccess.assertNoTerminalEvent()
        clockOutError.assertNoTerminalEvent()
    }

    @Test
    fun clockOut() {
        val timeInterval = TimeInterval.builder(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(0)
                .build()
        timeIntervalRepository.add(timeInterval)
        val projectsItem = ProjectsItem.from(project, listOf(timeInterval))
        val result = ProjectsItemAdapterResult(0, projectsItem)

        vm.input().clockOut(result, Date())

        clockInSuccess.assertNoValues()
        clockInError.assertNoValues()
        clockOutSuccess.assertValueCount(1)
        clockOutError.assertNoValues()
        clockInSuccess.assertNoTerminalEvent()
        clockInError.assertNoTerminalEvent()
        clockOutSuccess.assertNoTerminalEvent()
        clockOutError.assertNoTerminalEvent()
        verifyProjectStatus(clockOutSuccess.onNextEvents, false)
    }

    @Test
    fun clockOut_withDifferentStartingPoint() {
        val timeInterval = TimeInterval.builder(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(0)
                .build()
        timeIntervalRepository.add(timeInterval)
        val projectsItem = ProjectsItem.from(project, listOf(timeInterval))
        val result = ProjectsItemAdapterResult(0, projectsItem)
        vm.input().startingPointForTimeSummary(TimeIntervalStartingPoint.DAY.rawValue)

        vm.input().clockOut(result, Date())

        clockInSuccess.assertNoValues()
        clockInError.assertNoValues()
        clockOutSuccess.assertValueCount(1)
        clockOutError.assertNoValues()
        clockInSuccess.assertNoTerminalEvent()
        clockInError.assertNoTerminalEvent()
        clockOutSuccess.assertNoTerminalEvent()
        clockOutError.assertNoTerminalEvent()
        verifyProjectStatus(clockOutSuccess.onNextEvents, false)
    }

    @Test
    fun clockOut_withInvalidStartingPoint() {
        val timeInterval = TimeInterval.builder(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(0)
                .build()
        timeIntervalRepository.add(timeInterval)
        val projectsItem = ProjectsItem.from(project, listOf(timeInterval))
        val result = ProjectsItemAdapterResult(0, projectsItem)
        vm.input().startingPointForTimeSummary(-1)

        vm.input().clockOut(result, Date())

        clockInSuccess.assertNoValues()
        clockInError.assertNoValues()
        clockOutSuccess.assertValueCount(1)
        clockOutError.assertNoValues()
        clockInSuccess.assertNoTerminalEvent()
        clockInError.assertNoTerminalEvent()
        clockOutSuccess.assertNoTerminalEvent()
        clockOutError.assertNoTerminalEvent()
        verifyProjectStatus(clockOutSuccess.onNextEvents, false)
    }

    private fun verifyProjectStatus(results: List<ProjectsItemAdapterResult>, isActive: Boolean) {
        val (_, projectsItem) = results[0]

        if (isActive) {
            assertTrue(projectsItem.isActive)
            return
        }

        assertFalse(projectsItem.isActive)
    }
}
