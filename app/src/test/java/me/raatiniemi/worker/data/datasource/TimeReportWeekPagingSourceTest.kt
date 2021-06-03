/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

package me.raatiniemi.worker.data.datasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource.LoadParams.Refresh
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportWeek
import me.raatiniemi.worker.feature.projects.model.ProjectHolder
import me.raatiniemi.worker.koin.testKoinModules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.koin.test.inject

@RunWith(JUnit4::class)
class TimeReportWeekPagingSourceTest : AutoCloseKoinTest() {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val projectHolder by inject<ProjectHolder>()
    private val clockIn by inject<ClockIn>()
    private val clockOut by inject<ClockOut>()

    private lateinit var pagingSource: TimeReportWeekPagingSource

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            loadKoinModules(testKoinModules)
        }

        pagingSource = TimeReportWeekPagingSource(
            projectProvider = projectHolder,
            countTimeReportWeeks = get(),
            findTimeReportWeeks = get()
        )
    }

    // Load

    @Test
    fun `load without project`() {
        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }

        assertTrue(actual is Error)
    }

    @Test
    fun `load without time report weeks`() {
        projectHolder += android
        val expected = Page(
            data = emptyList(),
            prevKey = null,
            nextKey = null
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `load with time interval`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        runBlocking {
            clockIn(android, startOfDay)
            clockOut(android, startOfDay + 10.minutes)
        }
        projectHolder += android
        val expected = Page(
            data = listOf(
                timeReportWeek(
                    startOfDay,
                    listOf(
                        timeReportDay(
                            startOfDay,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(1)
                                    builder.start = startOfDay
                                    builder.stop = startOfDay + 10.minutes
                                }
                            )
                        )
                    )
                )
            ),
            prevKey = null,
            nextKey = null
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `load with time intervals on same day`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        runBlocking {
            clockIn(android, startOfDay)
            clockOut(android, startOfDay + 10.minutes)
            clockIn(android, startOfDay + 20.minutes)
            clockOut(android, startOfDay + 30.minutes)
        }
        projectHolder += android
        val expected = Page(
            data = listOf(
                timeReportWeek(
                    startOfDay,
                    listOf(
                        timeReportDay(
                            startOfDay,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(2)
                                    builder.start = startOfDay + 20.minutes
                                    builder.stop = startOfDay + 30.minutes
                                },
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(1)
                                    builder.start = startOfDay
                                    builder.stop = startOfDay + 10.minutes
                                }
                            )
                        )
                    )
                )
            ),
            prevKey = null,
            nextKey = null
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `load with time intervals on different days within same week`() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val endOfWeek = setToEndOfWeek(startOfWeek) - 30.minutes
        runBlocking {
            clockIn(android, startOfWeek)
            clockOut(android, startOfWeek + 10.minutes)
            clockIn(android, endOfWeek)
            clockOut(android, endOfWeek + 10.minutes)
        }
        projectHolder += android
        val expected = Page(
            data = listOf(
                timeReportWeek(
                    startOfWeek,
                    listOf(
                        timeReportDay(
                            endOfWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(2)
                                    builder.start = endOfWeek
                                    builder.stop = endOfWeek + 10.minutes
                                }
                            )
                        ),
                        timeReportDay(
                            startOfWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(1)
                                    builder.start = startOfWeek
                                    builder.stop = startOfWeek + 10.minutes
                                }
                            )
                        )
                    )
                )
            ),
            prevKey = null,
            nextKey = null
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `load with time intervals on different days on separate same weeks`() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        runBlocking {
            clockIn(android, startOfWeek)
            clockOut(android, startOfWeek + 10.minutes)
            clockIn(android, nextWeek)
            clockOut(android, nextWeek + 10.minutes)
        }
        projectHolder += android
        val expected = Page(
            data = listOf(
                timeReportWeek(
                    nextWeek,
                    listOf(
                        timeReportDay(
                            nextWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(2)
                                    builder.start = nextWeek
                                    builder.stop = nextWeek + 10.minutes
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
                                    builder.stop = startOfWeek + 10.minutes
                                }
                            )
                        )
                    )
                )
            ),
            prevKey = null,
            nextKey = null
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `load with time report week before range`() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        runBlocking {
            clockIn(android, startOfWeek)
            clockOut(android, startOfWeek + 10.minutes)
            clockIn(android, nextWeek)
            clockOut(android, nextWeek + 10.minutes)
        }
        projectHolder += android
        val expected = Page(
            data = listOf(
                timeReportWeek(
                    startOfWeek,
                    listOf(
                        timeReportDay(
                            startOfWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(1)
                                    builder.start = startOfWeek
                                    builder.stop = startOfWeek + 10.minutes
                                }
                            )
                        )
                    )
                )
            ),
            prevKey = null,
            nextKey = null
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = 1,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `load with time report week after range`() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        runBlocking {
            clockIn(android, startOfWeek)
            clockOut(android, startOfWeek + 10.minutes)
            clockIn(android, nextWeek)
            clockOut(android, nextWeek + 10.minutes)
        }
        projectHolder += android
        val expected = Page(
            data = listOf(
                timeReportWeek(
                    nextWeek,
                    listOf(
                        timeReportDay(
                            nextWeek,
                            listOf(
                                timeInterval(android.id) { builder ->
                                    builder.id = TimeIntervalId(2)
                                    builder.start = nextWeek
                                    builder.stop = nextWeek + 10.minutes
                                }
                            )
                        )
                    )
                )
            ),
            prevKey = null,
            nextKey = 1
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = null,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }
}
