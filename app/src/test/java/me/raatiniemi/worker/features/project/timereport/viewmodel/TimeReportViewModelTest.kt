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
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import me.raatiniemi.worker.features.project.timereport.model.TimeReportGroup
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

    private lateinit var repository: TimeReportRepository
    private lateinit var getTimeReport: GetTimeReport

    private fun setUpViewModel(timeIntervals: List<TimeInterval>): TimeReportViewModel {
        repository = TimeReportInMemoryRepository(timeIntervals)
        getTimeReport = GetTimeReport(repository)

        return TimeReportViewModel(keyValueStore, getTimeReport)
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
}
