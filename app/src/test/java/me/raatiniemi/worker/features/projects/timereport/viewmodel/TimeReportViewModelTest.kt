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

package me.raatiniemi.worker.features.projects.timereport.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime
import me.raatiniemi.worker.domain.interactor.RemoveTime
import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import me.raatiniemi.worker.features.projects.model.ProjectHolder
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportLongPressAction
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportTapAction
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.InMemoryUsageAnalytics
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import org.junit.Assert.assertEquals
import org.junit.Before
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
    private val usageAnalytics = InMemoryUsageAnalytics()
    private val projectHolder = ProjectHolder()
        .also { it += project }

    private val keyValueStore = InMemoryKeyValueStore()

    private lateinit var timeReportRepository: TimeReportRepository
    private lateinit var timeIntervalRepository: TimeIntervalRepository

    private lateinit var vm: TimeReportViewModel

    @Before
    fun setUp() {
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        timeReportRepository = TimeReportInMemoryRepository(timeIntervalRepository)

        vm = TimeReportViewModel(
            usageAnalytics,
            projectHolder,
            keyValueStore,
            timeReportRepository,
            MarkRegisteredTime(timeIntervalRepository),
            RemoveTime(timeIntervalRepository)
        )
    }

    @Test
    fun `toggle registered state with selected item`() = runBlocking {
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
            }
        )
        val timeInterval = timeInterval {
            id = 1
            start = Milliseconds(1)
        }
        val timeReportItem = TimeReportItem.with(timeInterval)
        val expected = listOf(
            timeInterval.copy(isRegistered = true)
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(timeReportItem))
        vm.toggleRegisteredStateForSelectedItems()

        assertEquals(listOf(Event.TimeReportToggle(1)), usageAnalytics.events)
        val actual = timeIntervalRepository.findAll(project, Milliseconds(0))
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle registered state for selected items`() = runBlocking {
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
            }
        )
        val firstTimeInterval = timeInterval {
            id = 1
            start = Milliseconds(1)
        }
        val secondTimeInterval = timeInterval {
            id = 2
            start = Milliseconds(1)
        }
        val firstTimeReportItem = TimeReportItem.with(firstTimeInterval)
        val secondTimeReportItem = TimeReportItem.with(secondTimeInterval)
        val expected = listOf(
            firstTimeInterval.copy(isRegistered = true),
            secondTimeInterval.copy(isRegistered = true)
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(firstTimeReportItem))
        vm.consume(TimeReportTapAction.TapItem(secondTimeReportItem))
        vm.toggleRegisteredStateForSelectedItems()

        assertEquals(listOf(Event.TimeReportToggle(2)), usageAnalytics.events)
        val actual = timeIntervalRepository.findAll(project, Milliseconds(0))
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with single item`() = runBlocking {
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
            }
        )
        val timeInterval = timeInterval {
            id = 1
            start = Milliseconds(1)
        }
        val timeReportItem = TimeReportItem(timeInterval)
        val expected = emptyList<TimeInterval>()

        vm.consume(TimeReportLongPressAction.LongPressItem(timeReportItem))
        vm.removeSelectedItems()

        assertEquals(listOf(Event.TimeReportRemove(1)), usageAnalytics.events)
        val actual = timeIntervalRepository.findAll(project, Milliseconds(0))
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with multiple items`() = runBlocking {
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds(1)
            }
        )
        val firstTimeReportItem = TimeReportItem(
            timeInterval {
                id = 1
                start = Milliseconds(1)
            }
        )
        val secondTimeReportItem = TimeReportItem(
            timeInterval {
                id = 2
                start = Milliseconds(1)
            }
        )
        val expected = emptyList<TimeInterval>()

        vm.consume(TimeReportLongPressAction.LongPressItem(firstTimeReportItem))
        vm.consume(TimeReportTapAction.TapItem(secondTimeReportItem))
        vm.removeSelectedItems()

        assertEquals(listOf(Event.TimeReportRemove(2)), usageAnalytics.events)
        val actual = timeIntervalRepository.findAll(project, Milliseconds(0))
        assertEquals(expected, actual)
    }
}
