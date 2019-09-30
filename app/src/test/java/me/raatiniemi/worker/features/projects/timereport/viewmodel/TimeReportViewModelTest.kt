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
import me.raatiniemi.worker.data.projects.datasource.TimeReportDataSourceFactory
import me.raatiniemi.worker.domain.configuration.InMemoryKeyValueStore
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.repository.resetToStartOfDay
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.hours
import me.raatiniemi.worker.domain.time.minutes
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.newTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timeinterval.usecase.MarkRegisteredTime
import me.raatiniemi.worker.domain.timeinterval.usecase.RemoveTime
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.timereport.usecase.CountTimeReports
import me.raatiniemi.worker.domain.timereport.usecase.FindTimeReports
import me.raatiniemi.worker.domain.timereport.usecase.countTimeReports
import me.raatiniemi.worker.domain.timereport.usecase.findTimeReports
import me.raatiniemi.worker.features.projects.model.ProjectHolder
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportLongPressAction
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportTapAction
import me.raatiniemi.worker.features.projects.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.features.shared.model.observeNoValue
import me.raatiniemi.worker.features.shared.model.observeNonNull
import me.raatiniemi.worker.monitor.analytics.Event
import me.raatiniemi.worker.monitor.analytics.InMemoryUsageAnalytics
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

    private val keyValueStore: KeyValueStore = InMemoryKeyValueStore()
    private val usageAnalytics = InMemoryUsageAnalytics()
    private val projectHolder = ProjectHolder()

    private lateinit var countTimeReports: CountTimeReports
    private lateinit var findTimeReports: FindTimeReports

    private lateinit var timeIntervalRepository: TimeIntervalRepository

    private lateinit var timeReportDataSourceFactory: TimeReportDataSourceFactory

    private lateinit var vm: TimeReportViewModel

    @Before
    fun setUp() {
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        val timeReportRepository = TimeReportInMemoryRepository(timeIntervalRepository)

        countTimeReports = countTimeReports(keyValueStore, timeReportRepository)
        findTimeReports = findTimeReports(keyValueStore, timeReportRepository)

        timeReportDataSourceFactory = TimeReportDataSourceFactory(
            projectHolder,
            countTimeReports,
            findTimeReports
        )

        vm = TimeReportViewModel(
            keyValueStore,
            usageAnalytics,
            timeReportDataSourceFactory,
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
            }
        ).also {
            timeIntervalRepository.update(it.clockOut(stop = Milliseconds(2)))
        }
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(2)
        }
        val expected = listOf(
            timeInterval(timeInterval) { builder ->
                builder.isRegistered = true
            }
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(timeInterval))
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
            }
        ).also {
            timeIntervalRepository.update(it.clockOut(stop = Milliseconds(2)))
        }
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).also {
            timeIntervalRepository.update(it.clockOut(stop = Milliseconds(2)))
        }
        val firstTimeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(2)
        }
        val secondTimeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(2)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(2)
        }
        val expected = listOf(
            timeInterval(firstTimeInterval) { builder ->
                builder.isRegistered = true
            },
            timeInterval(secondTimeInterval) { builder ->
                builder.isRegistered = true
            }
        )

        vm.consume(TimeReportLongPressAction.LongPressItem(firstTimeInterval))
        vm.consume(TimeReportTapAction.TapItem(secondTimeInterval))
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
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
        }
        vm.consume(TimeReportLongPressAction.LongPressItem(timeInterval))

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
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
        }
        val expected = emptyList<TimeInterval>()

        vm.consume(TimeReportLongPressAction.LongPressItem(timeInterval))
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
        val firstTimeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
        }

        val secondTimeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(2)
            builder.start = Milliseconds(1)
        }
        val expected = emptyList<TimeInterval>()

        vm.consume(TimeReportLongPressAction.LongPressItem(firstTimeInterval))
        vm.consume(TimeReportTapAction.TapItem(secondTimeInterval))
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
            timeReportDay(
                resetToStartOfDay(now),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = now - 20.minutes
                        builder.stop = now
                    }
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
            timeReportDay(
                resetToStartOfDay(now),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = now - 20.minutes
                    }
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
            timeReportDay(
                resetToStartOfDay(now),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = now - 20.minutes
                        builder.stop = now
                    }
                )
            ),
            timeReportDay(
                resetToStartOfDay(yesterday),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = yesterday - 20.minutes
                    }
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
