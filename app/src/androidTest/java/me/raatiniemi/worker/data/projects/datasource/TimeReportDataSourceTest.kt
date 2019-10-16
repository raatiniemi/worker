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
import me.raatiniemi.worker.domain.configuration.InMemoryKeyValueStore
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.date.plus
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.days
import me.raatiniemi.worker.domain.time.minutes
import me.raatiniemi.worker.domain.time.setToStartOfDay
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockIn
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOut
import me.raatiniemi.worker.domain.timeinterval.usecase.MarkRegisteredTime
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository
import me.raatiniemi.worker.domain.timereport.usecase.CountTimeReports
import me.raatiniemi.worker.domain.timereport.usecase.FindTimeReports
import me.raatiniemi.worker.features.projects.model.ProjectHolder
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class TimeReportDataSourceTest {
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

    private lateinit var dataSource: TimeReportDataSource

    @Before
    fun setUp() {
        keyValueStore = InMemoryKeyValueStore()
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        timeReportRepository = TimeReportInMemoryRepository(timeIntervalRepository)

        clockIn = ClockIn(timeIntervalRepository)
        clockOut = ClockOut(timeIntervalRepository)
        markRegisteredTime = MarkRegisteredTime(timeIntervalRepository)

        projectHolder = ProjectHolder()

        dataSource = TimeReportDataSource(
            projectHolder,
            CountTimeReports(keyValueStore, timeReportRepository),
            FindTimeReports(keyValueStore, timeReportRepository)
        )
    }

    // Load initial

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
    fun loadInitial_withTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, date = Date(startOfDay.value) + 20.minutes)
        clockOut(android, date = Date(startOfDay.value) + 30.minutes)
        projectHolder += android
        val data = listOf(
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
    fun loadInitial_withTimeIntervalsFilterUsingPosition() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val nextDay = startOfDay + 1.days
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, date = Date(nextDay.value))
        clockOut(android, date = Date(nextDay.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportDay(
                Date(nextDay.value),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = nextDay
                        builder.stop = nextDay + 10.minutes
                    }
                )
            ),
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
    fun loadInitial_withTimeIntervalsFilterUsingPageSize() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val nextDay = startOfDay + 1.days
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, date = Date(nextDay.value))
        clockOut(android, date = Date(nextDay.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportDay(
                Date(nextDay.value),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = nextDay
                        builder.stop = nextDay + 10.minutes
                    }
                )
            ),
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
    fun loadRange_withTimeInterval() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
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
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withTimeIntervals() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, date = Date(startOfDay.value) + 20.minutes)
        clockOut(android, date = Date(startOfDay.value) + 30.minutes)
        projectHolder += android
        val data = listOf(
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
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withTimeIntervalsFilterUsingPosition() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val nextDay = startOfDay + 1.days
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, date = Date(nextDay.value))
        clockOut(android, date = Date(nextDay.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportDay(
                Date(nextDay.value),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = nextDay
                        builder.stop = nextDay + 10.minutes
                    }
                )
            ),
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
        val expected = PositionalDataSourceResult.Range(data.drop(1))

        dataSource.loadRange(
            loadRangeParams(startPosition = 1, loadSize = 1),
            loadRangeCallback {
                assertEquals(expected, it)
            }
        )
    }

    @Test
    fun loadRange_withTimeIntervalsFilterUsingPageSize() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val nextDay = startOfDay + 1.days
        clockIn(android, date = Date(startOfDay.value))
        clockOut(android, date = Date(startOfDay.value) + 10.minutes)
        clockIn(android, date = Date(nextDay.value))
        clockOut(android, date = Date(nextDay.value) + 10.minutes)
        projectHolder += android
        val data = listOf(
            timeReportDay(
                Date(nextDay.value),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = nextDay
                        builder.stop = nextDay + 10.minutes
                    }
                )
            ),
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
        val expected = PositionalDataSourceResult.Range(data.take(1))

        dataSource.loadRange(loadRangeParams(loadSize = 1), loadRangeCallback {
            assertEquals(expected, it)
        })
    }
}
