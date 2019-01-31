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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime
import me.raatiniemi.worker.domain.interactor.RemoveTime
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import me.raatiniemi.worker.features.project.model.ProjectHolder
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAdapterResult
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeReportViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val project = Project(1, "Project name #1")

    private val projectHolder = ProjectHolder().apply {
        project = 1
    }
    private val keyValueStore = InMemoryKeyValueStore()

    private lateinit var timeReportRepository: TimeReportRepository
    private lateinit var timeIntervalRepository: TimeIntervalRepository

    private fun setUpViewModel(timeIntervals: List<TimeInterval>): TimeReportViewModel {
        timeReportRepository = TimeReportInMemoryRepository(timeIntervals)
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        timeIntervals.forEach {
            timeIntervalRepository.add(it)
        }

        return TimeReportViewModel(
                projectHolder,
                keyValueStore,
                timeReportRepository,
                MarkRegisteredTime(timeIntervalRepository),
                RemoveTime(timeIntervalRepository)
        )
    }

    @Test
    fun `register with item`() = runBlocking {
        val vm = setUpViewModel(
                listOf(
                        timeInterval { }
                )
        )
        val timeInterval = timeInterval { id = 1 }
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem.with(timeInterval))
        )
        val expected = listOf(
                timeInterval.copy(isRegistered = true)
        )

        vm.register(results)

        val actual = timeIntervalRepository.findAll(project, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun `register with items`() = runBlocking {
        val vm = setUpViewModel(
                listOf(
                        timeInterval { },
                        timeInterval { }
                )
        )
        val firstTimeInterval = timeInterval { id = 1 }
        val secondTimeInterval = timeInterval { id = 2 }
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem.with(firstTimeInterval)),
                TimeReportAdapterResult(0, 1, TimeReportItem.with(secondTimeInterval))
        )
        val expected = listOf(
                firstTimeInterval.copy(isRegistered = true),
                secondTimeInterval.copy(isRegistered = true)
        )

        vm.register(results)

        val actual = timeIntervalRepository.findAll(project, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with single item`() = runBlocking {
        val vm = setUpViewModel(listOf(
                timeInterval { }
        ))
        val timeInterval = timeInterval { id = 1 }
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem(timeInterval))
        )
        val expected = emptyList<TimeInterval>()

        vm.remove(results)

        val actual = timeIntervalRepository.findAll(project, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with multiple items`() = runBlocking {
        val vm = setUpViewModel(listOf(
                timeInterval { },
                timeInterval { }
        ))
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem(timeInterval { id = 1 })),
                TimeReportAdapterResult(0, 1, TimeReportItem(timeInterval { id = 2 }))
        )
        val expected = emptyList<TimeInterval>()

        vm.remove(results)

        val actual = timeIntervalRepository.findAll(project, 0)
        assertEquals(expected, actual)
    }
}
