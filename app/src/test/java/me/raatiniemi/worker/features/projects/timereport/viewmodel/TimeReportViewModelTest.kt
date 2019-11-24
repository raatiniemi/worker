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
import me.raatiniemi.worker.data.projects.datasource.TimeReportWeekDataSource
import me.raatiniemi.worker.domain.configuration.InMemoryKeyValueStore
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.newTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timeinterval.usecase.MarkRegisteredTime
import me.raatiniemi.worker.domain.timeinterval.usecase.RemoveTime
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportWeek
import me.raatiniemi.worker.domain.timereport.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.timereport.usecase.CountTimeReportWeeks
import me.raatiniemi.worker.domain.timereport.usecase.FindTimeReportWeeks
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

@RunWith(JUnit4::class)
class TimeReportViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val keyValueStore: KeyValueStore = InMemoryKeyValueStore()
    private val usageAnalytics = InMemoryUsageAnalytics()
    private val projectHolder = ProjectHolder()

    private lateinit var countTimeReportWeeks: CountTimeReportWeeks
    private lateinit var findTimeReportWeeks: FindTimeReportWeeks

    private lateinit var timeIntervalRepository: TimeIntervalRepository

    private lateinit var dataSourceFactory: TimeReportWeekDataSource.Factory

    private lateinit var vm: TimeReportViewModel

    @Before
    fun setUp() {
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        val timeReportRepository = TimeReportInMemoryRepository(timeIntervalRepository)

        countTimeReportWeeks = CountTimeReportWeeks(keyValueStore, timeReportRepository)
        findTimeReportWeeks = FindTimeReportWeeks(keyValueStore, timeReportRepository)

        dataSourceFactory = TimeReportWeekDataSource.Factory(
            projectHolder,
            countTimeReportWeeks,
            findTimeReportWeeks
        )

        vm = TimeReportViewModel(
            keyValueStore,
            usageAnalytics,
            dataSourceFactory,
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

    // Refresh active time report week

    @Test
    fun `refresh active time report week without weeks`() = runBlocking {
        projectHolder += android
        val weeks = emptyList<TimeReportWeek>()

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active time report week without active week`() = runBlocking {
        projectHolder += android
        val now = Milliseconds.now
        val weeks = listOf(
            timeReportWeek(
                setToStartOfWeek(now),
                listOf(
                    timeReportDay(
                        now,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = now
                                builder.stop = now + 20.minutes
                            }
                        )
                    )
                )
            )
        )

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNoValue()
    }

    @Test
    fun `refresh active time report week with week`() = runBlocking {
        projectHolder += android
        val now = Milliseconds.now
        val weeks = listOf(
            timeReportWeek(
                setToStartOfWeek(now),
                listOf(
                    timeReportDay(
                        now,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = now
                            }
                        )
                    )
                )
            )
        )

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNonNull {
            assertEquals(TimeReportViewActions.RefreshTimeReportWeek(0), it)
        }
    }

    @Test
    fun `refresh active time report week with days`() = runBlocking {
        projectHolder += android
        val now = Milliseconds.now
        val startOfWeek = setToStartOfWeek(now)
        val nextDay = startOfWeek + 1.days
        val weeks = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        nextDay,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = nextDay
                            }
                        )
                    ),
                    timeReportDay(
                        startOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 2.hours
                            }
                        )
                    )
                )
            )
        )

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNonNull {
            assertEquals(TimeReportViewActions.RefreshTimeReportWeek(0), it)
        }
    }

    @Test
    fun `refresh active time report week with weeks`() = runBlocking {
        projectHolder += android
        val now = Milliseconds.now
        val startOfWeek = setToStartOfWeek(now)
        val nextWeek = startOfWeek + 1.weeks
        val weeks = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = nextWeek
                                builder.stop = nextWeek + 2.hours
                            }
                        )
                    )
                )
            ),
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        startOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                            }
                        )
                    )
                )
            )
        )

        vm.refreshActiveTimeReportWeek(weeks)

        vm.viewActions.observeNonNull {
            assertEquals(TimeReportViewActions.RefreshTimeReportWeek(1), it)
        }
    }
}
