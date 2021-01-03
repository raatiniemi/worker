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
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.ios
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timeinterval.usecase.MarkRegisteredTime
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportWeek
import me.raatiniemi.worker.feature.projects.model.ProjectHolder
import me.raatiniemi.worker.koin.androidTestKoinModules
import me.raatiniemi.worker.koin.module.inMemorySharedTest
import me.raatiniemi.worker.util.CoroutineTestRule
import me.raatiniemi.worker.util.TestCoroutineDispatchProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.koin.test.inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TimeReportWeekDataSourceTest : AutoCloseKoinTest() {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val keyValueStore by inject<KeyValueStore>()
    private val projectHolder by inject<ProjectHolder>()
    private val clockIn by inject<ClockIn>()
    private val clockOut by inject<ClockOut>()
    private val markRegisteredTime by inject<MarkRegisteredTime>()

    private lateinit var dataSource: TimeReportWeekDataSource

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            loadKoinModules(androidTestKoinModules + inMemorySharedTest)
        }

        val factory = TimeReportWeekDataSource.Factory(
            scope = coroutineTestRule.testScope,
            dispatcherProvider = TestCoroutineDispatchProvider(coroutineTestRule.testDispatcher),
            projectProvider = get(),
            countTimeReportWeeks = get(),
            findTimeReportWeeks = get()
        )
        dataSource = factory.create()
    }

    // Load initial
    // - When not hiding registered time

    @Test
    fun loadInitial_withoutProject() {
        val expected = PositionalDataSourceResult.Initial<TimeReportDay>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withoutTimeIntervals() {
        projectHolder += android
        val expected = PositionalDataSourceResult.Initial<TimeReportDay>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withoutTimeIntervalForProject() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        projectHolder += ios
        val expected = PositionalDataSourceResult.Initial<TimeReportDay>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withTimeInterval() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data,
            position = 0,
            totalCount = data.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withTimeIntervals() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, startOfDay + 30.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data,
            position = 0,
            totalCount = data.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withTimeIntervalsWithinSameWeek() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val endOfWeek = setToEndOfWeek(startOfWeek) - 30.minutes
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data,
            position = 0,
            totalCount = data.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withTimeIntervalsInDifferentWeeks() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data,
            position = 0,
            totalCount = data.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withRegisteredTimeInterval() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        projectHolder += android
        val data = listOf(
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
                                builder.isRegistered = true
                            }
                        )
                    )
                )
            )
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data,
            position = 0,
            totalCount = data.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_whenExcludingByLoadPosition() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
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
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfWeek + 20.minutes
                                builder.stop = startOfWeek + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data.drop(1),
            position = 1,
            totalCount = data.size
        )

        dataSource.loadInitial(
            loadInitialParams(requestedStartPosition = 1, requestedLoadSize = 1),
            loadInitialCallback {
                assertEquals(expected, it)
            }
        )
    }

    @Test
    fun loadInitial_whenExcludingByLoadSize() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
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
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfWeek + 20.minutes
                                builder.stop = startOfWeek + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data.take(1),
            position = 0,
            totalCount = data.size
        )

        dataSource.loadInitial(loadInitialParams(requestedLoadSize = 1), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    // - When hiding registered time

    @Test
    fun loadInitial_whenHidingRegisteredTimeWithoutProject() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val expected = PositionalDataSourceResult.Initial<TimeReportDay>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_whenHidingRegisteredTimeWithoutTimeIntervals() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        projectHolder += android
        val expected = PositionalDataSourceResult.Initial<TimeReportDay>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_whenHidingRegisteredTimeWithoutTimeIntervalForProject() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        projectHolder += ios
        val expected = PositionalDataSourceResult.Initial<TimeReportDay>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_whenHidingRegisteredTimeWithTimeInterval() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data,
            position = 0,
            totalCount = data.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_whenHidingRegisteredTimeWithTimeIntervals() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, startOfDay + 30.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data,
            position = 0,
            totalCount = data.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_whenHidingRegisteredTimeWithTimeIntervalsWithinSameWeek() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val endOfWeek = setToEndOfWeek(startOfWeek) - 30.minutes
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data,
            position = 0,
            totalCount = data.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_whenHidingRegisteredTimeWithTimeIntervalsInDifferentWeeks() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data,
            position = 0,
            totalCount = data.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_whenHidingRegisteredTimeWithRegisteredTimeInterval() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        projectHolder += android
        val expected = PositionalDataSourceResult.Initial<TimeReportDay>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_whenHidingRegisteredTimeWhenExcludingByLoadPosition() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
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
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfWeek + 20.minutes
                                builder.stop = startOfWeek + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data.drop(1),
            position = 1,
            totalCount = data.size
        )

        dataSource.loadInitial(
            loadInitialParams(requestedStartPosition = 1, requestedLoadSize = 1),
            loadInitialCallback {
                assertEquals(expected, it)
            }
        )
    }

    @Test
    fun loadInitial_whenHidingRegisteredTimeWhenExcludingByLoadSize() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
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
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfWeek + 20.minutes
                                builder.stop = startOfWeek + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )
        val expected = PositionalDataSourceResult.Initial(
            data = data.take(1),
            position = 0,
            totalCount = data.size
        )

        dataSource.loadInitial(loadInitialParams(requestedLoadSize = 1), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    // Load range
    // - When not hiding registered time

    @Test
    fun loadRange_withoutProject() {
        val expected = PositionalDataSourceResult.Range<TimeReportDay>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withoutTimeIntervals() {
        projectHolder += android
        val expected = PositionalDataSourceResult.Range<TimeReportDay>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withoutTimeIntervalForProject() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        projectHolder += ios
        val expected = PositionalDataSourceResult.Range<TimeReportDay>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withTimeInterval() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withTimeIntervals() = runBlocking {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, startOfDay + 30.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withTimeIntervalsWithinSameWeek() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val endOfWeek = setToEndOfWeek(startOfWeek) - 30.minutes
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withTimeIntervalsInDifferentWeeks() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withRegisteredTimeInterval() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        projectHolder += android
        val data = listOf(
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
                                builder.isRegistered = true
                            }
                        )
                    )
                )
            )
        )
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_whenExcludingByLoadPosition() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
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
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfWeek + 20.minutes
                                builder.stop = startOfWeek + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )
        val expected = PositionalDataSourceResult.Range(data.drop(1))

        dataSource.loadRange(
            loadRangeParams(startPosition = 1, loadSize = 1),
            loadRangeCallback {
                assertEquals(expected, it)
            }
        )
    }

    @Test
    fun loadRange_whenExcludingByLoadSize() = runBlocking {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
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
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfWeek + 20.minutes
                                builder.stop = startOfWeek + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )
        val expected = PositionalDataSourceResult.Range(data.take(1))

        dataSource.loadRange(loadRangeParams(loadSize = 1), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    // - When hiding registered time

    @Test
    fun loadRange_whenHidingRegisteredTimeWithoutProject() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val expected = PositionalDataSourceResult.Range<TimeReportDay>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_whenHidingRegisteredTimeWithoutTimeIntervals() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        projectHolder += android
        val expected = PositionalDataSourceResult.Range<TimeReportDay>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_whenHidingRegisteredTimeWithoutTimeIntervalForProject() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        projectHolder += ios
        val expected = PositionalDataSourceResult.Range<TimeReportDay>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_whenHidingRegisteredTimeWithTimeInterval() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_whenHidingRegisteredTimeWithTimeIntervals() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, startOfDay + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, startOfDay + 30.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_whenHidingRegisteredTimeWithTimeIntervalsWithinSameWeek() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val endOfWeek = setToEndOfWeek(startOfWeek) - 30.minutes
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, endOfWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_whenHidingRegisteredTimeWithTimeIntervalsInDifferentWeeks() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        )
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_whenHidingRegisteredTimeWithRegisteredTimeInterval() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        projectHolder += android
        val expected = PositionalDataSourceResult.Range<TimeReportDay>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_whenHidingRegisteredTimeWhenExcludingByLoadPosition() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
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
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfWeek + 20.minutes
                                builder.stop = startOfWeek + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )
        val expected = PositionalDataSourceResult.Range(data.drop(1))

        dataSource.loadRange(
            loadRangeParams(startPosition = 1, loadSize = 1),
            loadRangeCallback {
                assertEquals(expected, it)
            }
        )
    }

    @Test
    fun loadRange_whenHidingRegisteredTimeWhenExcludingByLoadSize() = runBlocking {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, startOfWeek + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, startOfWeek + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, nextWeek + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        nextWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
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
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfWeek + 20.minutes
                                builder.stop = startOfWeek + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )
        val expected = PositionalDataSourceResult.Range(data.take(1))

        dataSource.loadRange(loadRangeParams(loadSize = 1), loadRangeCallback {
            assertEquals(expected, it)
        })
    }
}
