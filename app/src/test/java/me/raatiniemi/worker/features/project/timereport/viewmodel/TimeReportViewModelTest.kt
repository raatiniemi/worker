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
import me.raatiniemi.worker.domain.interactor.GetTimeReport
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
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAdapterResult
import me.raatiniemi.worker.features.project.timereport.model.TimeReportGroup
import me.raatiniemi.worker.features.project.timereport.model.TimeReportViewActions
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import org.junit.Assert.assertEquals
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

    private fun resetToStartOfDay(timeInMilliseconds: Long): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMilliseconds
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }

    private val keyValueStore = InMemoryKeyValueStore()

    private lateinit var timeReportRepository: TimeReportRepository
    private lateinit var timeIntervalRepository: TimeIntervalRepository

    private fun setUpViewModel(timeIntervals: List<TimeInterval>): TimeReportViewModel {
        timeReportRepository = TimeReportInMemoryRepository(timeIntervals)
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        timeIntervals.forEach { timeIntervalRepository.add(it) }

        return TimeReportViewModel(
                keyValueStore,
                GetTimeReport(timeReportRepository),
                MarkRegisteredTime(timeIntervalRepository),
                RemoveTime(timeIntervalRepository)
        )
    }

    @Test
    fun `fetch without time intervals`() = runBlocking {
        val expected = emptyList<TimeReportGroup>()
        val vm = setUpViewModel(emptyList())

        vm.fetch(1, 0)

        vm.timeReport.observeForever {
            assertEquals(expected, it)
        }
    }

    @Test
    fun `fetch with hide registered time intervals`() = runBlocking {
        val expected = emptyList<TimeReportGroup>()
        val vm = setUpViewModel(listOf(
                timeInterval {
                    isRegistered = true
                }
        ))
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME.rawValue, true)

        vm.fetch(1, 0)

        vm.timeReport.observeForever {
            assertEquals(expected, it)
        }
    }

    @Test
    fun `fetch with single item`() = runBlocking {
        val expected = listOf(
                TimeReportGroup.build(
                        resetToStartOfDay(0),
                        sortedSetOf(
                                TimeReportItem(timeInterval { id = 1 })
                        )
                )
        )
        val vm = setUpViewModel(listOf(
                timeInterval {
                    id = 1
                }
        ))

        vm.fetch(1, 0)

        vm.timeReport.observeForever {
            assertEquals(expected, it)
        }
    }

    @Test
    fun `fetch with multiple items`() = runBlocking {
        val date = Date()
        val expected = listOf(
                TimeReportGroup.build(
                        resetToStartOfDay(date.time),
                        sortedSetOf(
                                TimeReportItem(timeInterval {
                                    id = 2
                                    startInMilliseconds = date.time
                                })
                        )
                ),
                TimeReportGroup.build(
                        resetToStartOfDay(0),
                        sortedSetOf(
                                TimeReportItem(timeInterval {
                                    id = 1
                                    startInMilliseconds = 0
                                    stopInMilliseconds = 1
                                })
                        )
                )
        )
        val vm = setUpViewModel(listOf(
                timeInterval {
                    id = 1
                    startInMilliseconds = 0
                    stopInMilliseconds = 1
                },
                timeInterval {
                    id = 2
                    startInMilliseconds = date.time
                }
        ))

        vm.fetch(1, 0)

        vm.timeReport.observeForever {
            assertEquals(expected, it)
        }
    }

    @Test
    fun `register with item`() = runBlocking {
        val vm = setUpViewModel(listOf(
                timeInterval { }
        ))
        val timeInterval = timeInterval { id = 1 }
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem.with(timeInterval))
        )
        val expected = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem(timeInterval.copy(isRegistered = true)))
        )

        vm.register(results)

        vm.viewActions.observeForever {
            assertEquals(TimeReportViewActions.UpdateRegistered(expected), it)
        }
    }

    @Test
    fun `register with items`() = runBlocking {
        val vm = setUpViewModel(listOf(
                timeInterval { },
                timeInterval { }
        ))
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

        vm.viewActions.observeForever {
            assertEquals(TimeReportViewActions.UpdateRegistered(expected.asReversed()), it)
        }
    }

    @Test
    fun `remove with single item`() = runBlocking {
        val expected = emptyList<TimeInterval>()
        val project = Project(1, "Project name #1")
        val vm = setUpViewModel(listOf(
                timeInterval { }
        ))
        val timeInterval = timeInterval { id = 1 }
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem(timeInterval))
        )

        vm.remove(results)

        val actual = timeIntervalRepository.findAll(project, 0)
        assertEquals(expected, actual)
        vm.viewActions.observeForever {
            assertEquals(TimeReportViewActions.RemoveRegistered(results), it)
        }
    }

    @Test
    fun `remove with multiple items`() = runBlocking {
        val expected = emptyList<TimeInterval>()
        val project = Project(1, "Project name #1")
        val vm = setUpViewModel(listOf(
                timeInterval { },
                timeInterval { }
        ))
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem(timeInterval { id = 1 })),
                TimeReportAdapterResult(0, 1, TimeReportItem(timeInterval { id = 2 }))
        )

        vm.remove(results)

        val actual = timeIntervalRepository.findAll(project, 0)
        assertEquals(expected, actual)
        vm.viewActions.observeForever {
            assertEquals(TimeReportViewActions.RemoveRegistered(results.reversed()), it)
        }
    }
}
