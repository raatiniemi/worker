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
import me.raatiniemi.worker.domain.date.hours
import me.raatiniemi.worker.domain.date.minutes
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime
import me.raatiniemi.worker.domain.interactor.RemoveTime
import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.repository.*
import me.raatiniemi.worker.features.projects.model.ProjectHolder
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportLongPressAction
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportTapAction
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.features.shared.model.observeNoValue
import me.raatiniemi.worker.features.shared.model.observeNonNull
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.InMemoryUsageAnalytics
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class TimeReportViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val usageAnalytics = InMemoryUsageAnalytics()
    private val projectHolder = ProjectHolder()

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
        projectHolder += android
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
            }
        )
        val timeInterval = timeInterval {
            id = 1
            projectId = android.id
            start = Milliseconds(1)
            stop = Milliseconds(2)
        }
        val timeReportItem = TimeReportItem.with(timeInterval)
        val expected = listOf(
            timeInterval.copy(isRegistered = true)
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(timeReportItem))
        vm.toggleRegisteredStateForSelectedItems()

        assertEquals(listOf(Event.TimeReportToggle(1)), usageAnalytics.events)
        val actual = timeIntervalRepository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle registered state for selected items`() = runBlocking {
        projectHolder += android
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
            }
        )
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
            }
        )
        val firstTimeInterval = timeInterval {
            id = 1
            projectId = android.id
            start = Milliseconds(1)
            stop = Milliseconds(2)
        }
        val secondTimeInterval = timeInterval {
            id = 2
            projectId = android.id
            start = Milliseconds(1)
            stop = Milliseconds(2)
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
        val actual = timeIntervalRepository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun `toggle registered state for active time interval`() = runBlocking {
        projectHolder += android
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val timeInterval = timeInterval {
            id = 1
            projectId = android.id
            start = Milliseconds(1)
        }
        val timeReportItem = TimeReportItem.with(timeInterval)
        vm.consume(TimeReportLongPressAction.LongPressItem(timeReportItem))

        vm.toggleRegisteredStateForSelectedItems()

        vm.viewActions.observeNonNull {
            assertEquals(
                TimeReportViewActions.ShowUnableToMarkActiveTimeIntervalsAsRegisteredErrorMessage,
                it
            )
        }
    }

    @Test
    fun `remove with single item`() = runBlocking {
        projectHolder += android
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val timeInterval = timeInterval {
            id = 1
            projectId = android.id
            start = Milliseconds(1)
        }
        val timeReportItem = TimeReportItem(timeInterval)
        val expected = emptyList<TimeInterval>()

        vm.consume(TimeReportLongPressAction.LongPressItem(timeReportItem))
        vm.removeSelectedItems()

        assertEquals(listOf(Event.TimeReportRemove(1)), usageAnalytics.events)
        val actual = timeIntervalRepository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with multiple items`() = runBlocking {
        projectHolder += android
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val firstTimeReportItem = TimeReportItem(
            timeInterval {
                id = 1
                projectId = android.id
                start = Milliseconds(1)
            }
        )
        val secondTimeReportItem = TimeReportItem(
            timeInterval {
                id = 2
                projectId = android.id
                start = Milliseconds(1)
            }
        )
        val expected = emptyList<TimeInterval>()

        vm.consume(TimeReportLongPressAction.LongPressItem(firstTimeReportItem))
        vm.consume(TimeReportTapAction.TapItem(secondTimeReportItem))
        vm.removeSelectedItems()

        assertEquals(listOf(Event.TimeReportRemove(2)), usageAnalytics.events)
        val actual = timeIntervalRepository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun `refresh active time report day without day`() = runBlocking {
        projectHolder += android
        val timeReportDays = emptyList<TimeReportDay>()

        vm.refreshActiveTimeReportDay(timeReportDays)

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active time report day without active day`() = runBlocking {
        projectHolder += android
        val now = Milliseconds(Date().time)
        val timeReportDays = listOf(
            TimeReportDay(
                resetToStartOfDay(now),
                listOf(
                    TimeReportItem(
                        timeInterval {
                            projectId = android.id
                            start = now - 20.minutes
                            stop = now
                        }
                    )
                )
            )
        )

        vm.refreshActiveTimeReportDay(timeReportDays)

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active time report day with day`() = runBlocking {
        projectHolder += android
        val now = Milliseconds(Date().time)
        val timeReportDays = listOf(
            TimeReportDay(
                resetToStartOfDay(now),
                listOf(
                    TimeReportItem(
                        timeInterval {
                            projectId = android.id
                            start = now - 20.minutes
                        }
                    )
                )
            )
        )

        vm.refreshActiveTimeReportDay(timeReportDays)

        vm.viewActions.observeNonNull {
            val positions = listOf(0)
            assertEquals(TimeReportViewActions.RefreshTimeReportDays(positions), it)
        }
    }

    @Test
    fun `refresh active time report day with days`() = runBlocking {
        projectHolder += android
        val now = Milliseconds(Date().time)
        val yesterday = Milliseconds(Date().time) - 25.hours
        val timeReportDays = listOf(
            TimeReportDay(
                resetToStartOfDay(now),
                listOf(
                    TimeReportItem(
                        timeInterval {
                            projectId = android.id
                            start = now - 20.minutes
                            stop = now
                        }
                    )
                )
            ),
            TimeReportDay(
                resetToStartOfDay(yesterday),
                listOf(
                    TimeReportItem(
                        timeInterval {
                            projectId = android.id
                            start = yesterday - 20.minutes
                        }
                    )
                )
            )
        )

        vm.refreshActiveTimeReportDay(timeReportDays)

        vm.viewActions.observeNonNull {
            val positions = listOf(1)
            assertEquals(TimeReportViewActions.RefreshTimeReportDays(positions), it)
        }
    }
}
