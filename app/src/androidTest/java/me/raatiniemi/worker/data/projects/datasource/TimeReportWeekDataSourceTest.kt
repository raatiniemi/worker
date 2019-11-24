/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.data.projects.datasource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.InMemoryKeyValueStore
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.date.plus
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.ios
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timeinterval.usecase.MarkRegisteredTime
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportWeek
import me.raatiniemi.worker.domain.timereport.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository
import me.raatiniemi.worker.domain.timereport.usecase.CountTimeReportWeeks
import me.raatiniemi.worker.domain.timereport.usecase.FindTimeReportWeeks
import me.raatiniemi.worker.features.projects.model.ProjectHolder
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class TimeReportWeekDataSourceTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var keyValueStore: KeyValueStore
    private lateinit var timeIntervalRepository: TimeIntervalRepository
    private lateinit var timeReportRepository: TimeReportRepository

    private lateinit var clockIn: ClockIn
    private lateinit var clockOut: ClockOut
    private lateinit var markRegisteredTime: MarkRegisteredTime

    private lateinit var projectHolder: ProjectHolder

    private lateinit var dataSource: TimeReportWeekDataSource

    @Before
    fun setUp() {
        keyValueStore = InMemoryKeyValueStore()
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        timeReportRepository = TimeReportInMemoryRepository(timeIntervalRepository)

        clockIn = ClockIn(timeIntervalRepository)
        clockOut = ClockOut(timeIntervalRepository)
        markRegisteredTime = MarkRegisteredTime(timeIntervalRepository)

        projectHolder = ProjectHolder()

        dataSource = TimeReportWeekDataSource(
            projectHolder,
            CountTimeReportWeeks(keyValueStore, timeReportRepository),
            FindTimeReportWeeks(keyValueStore, timeReportRepository)
        )
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
    fun loadInitial_withoutTimeIntervalForProject() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        projectHolder += ios
        val expected = PositionalDataSourceResult.Initial<TimeReportDay>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        Date(startOfDay.value),
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
    fun loadInitial_withTimeIntervals() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, date = Date(startOfDay.value) + 30.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        Date(startOfDay.value),
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
    fun loadInitial_withTimeIntervalsWithinSameWeek() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val endOfWeek = setToEndOfWeek(startOfWeek)
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, date = Date(endOfWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        Date(endOfWeek.value),
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = endOfWeek
                                builder.stop = endOfWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        Date(startOfWeek.value),
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
    fun loadInitial_withTimeIntervalsInDifferentWeeks() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
    fun loadInitial_withRegisteredTimeInterval() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        Date(startOfWeek.value),
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
    fun loadInitial_whenExcludingByLoadPosition() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, date = Date(startOfWeek.value) + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
    fun loadInitial_whenExcludingByLoadSize() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, date = Date(startOfWeek.value) + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
    fun loadInitial_whenHidingRegisteredTimeWithoutTimeIntervalForProject() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        projectHolder += ios
        val expected = PositionalDataSourceResult.Initial<TimeReportDay>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_whenHidingRegisteredTimeWithTimeInterval() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        Date(startOfDay.value),
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
    fun loadInitial_whenHidingRegisteredTimeWithTimeIntervals() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, date = Date(startOfDay.value) + 30.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        Date(startOfDay.value),
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
    fun loadInitial_whenHidingRegisteredTimeWithTimeIntervalsWithinSameWeek() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val endOfWeek = setToEndOfWeek(startOfWeek)
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, date = Date(endOfWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        Date(endOfWeek.value),
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = endOfWeek
                                builder.stop = endOfWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        Date(startOfWeek.value),
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
    fun loadInitial_whenHidingRegisteredTimeWithTimeIntervalsInDifferentWeeks() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
    fun loadInitial_whenHidingRegisteredTimeWithRegisteredTimeInterval() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
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
    fun loadInitial_whenHidingRegisteredTimeWhenExcludingByLoadPosition() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, date = Date(startOfWeek.value) + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
    fun loadInitial_whenHidingRegisteredTimeWhenExcludingByLoadSize() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, date = Date(startOfWeek.value) + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
    fun loadRange_withoutTimeIntervalForProject() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        projectHolder += ios
        val expected = PositionalDataSourceResult.Range<TimeReportDay>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        Date(startOfDay.value),
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
    fun loadRange_withTimeIntervals() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, date = Date(startOfDay.value) + 30.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        Date(startOfDay.value),
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
    fun loadRange_withTimeIntervalsWithinSameWeek() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val endOfWeek = setToEndOfWeek(startOfWeek)
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, date = Date(endOfWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        Date(endOfWeek.value),
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = endOfWeek
                                builder.stop = endOfWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        Date(startOfWeek.value),
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
    fun loadRange_withTimeIntervalsInDifferentWeeks() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
    fun loadRange_withRegisteredTimeInterval() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
            .also { timeInterval ->
                markRegisteredTime(listOf(timeInterval))
            }
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        Date(startOfWeek.value),
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
    fun loadRange_whenExcludingByLoadPosition() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, date = Date(startOfWeek.value) + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
    fun loadRange_whenExcludingByLoadSize() {
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, date = Date(startOfWeek.value) + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
    fun loadRange_whenHidingRegisteredTimeWithoutTimeIntervalForProject() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        projectHolder += ios
        val expected = PositionalDataSourceResult.Range<TimeReportDay>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_whenHidingRegisteredTimeWithTimeInterval() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        Date(startOfDay.value),
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
    fun loadRange_whenHidingRegisteredTimeWithTimeIntervals() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, startOfDay)
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, startOfDay + 20.minutes)
        clockOut(android, date = Date(startOfDay.value) + 30.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        Date(startOfDay.value),
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
    fun loadRange_whenHidingRegisteredTimeWithTimeIntervalsWithinSameWeek() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val endOfWeek = setToEndOfWeek(startOfWeek)
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, endOfWeek)
        clockOut(android, date = Date(endOfWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        Date(endOfWeek.value),
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = endOfWeek
                                builder.stop = endOfWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        Date(startOfWeek.value),
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
    fun loadRange_whenHidingRegisteredTimeWithTimeIntervalsInDifferentWeeks() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
    fun loadRange_whenHidingRegisteredTimeWithRegisteredTimeInterval() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
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
    fun loadRange_whenHidingRegisteredTimeWhenExcludingByLoadPosition() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, date = Date(startOfWeek.value) + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
    fun loadRange_whenHidingRegisteredTimeWhenExcludingByLoadSize() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val startOfWeek = setToStartOfWeek(Milliseconds.now)
        val nextWeek = startOfWeek + 1.weeks
        clockIn(android, startOfWeek)
        clockOut(android, date = Date(startOfWeek.value) + 10.minutes)
        clockIn(android, startOfWeek + 20.minutes)
        clockOut(android, date = Date(startOfWeek.value) + 30.minutes)
        clockIn(android, nextWeek)
        clockOut(android, date = Date(nextWeek.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(nextWeek.value),
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
                        Date(startOfWeek.value),
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
