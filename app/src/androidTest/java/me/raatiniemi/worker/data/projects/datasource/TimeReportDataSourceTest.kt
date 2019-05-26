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

import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.domain.date.hours
import me.raatiniemi.worker.domain.interactor.countTimeReports
import me.raatiniemi.worker.domain.interactor.findTimeReports
import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.repository.*
import me.raatiniemi.worker.features.projects.model.ProjectHolder
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import me.raatiniemi.worker.util.KeyValueStore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimeReportDataSourceTest {
    private val project = Project(1, "Project #1")
    private val projectHolder = ProjectHolder()

    private lateinit var keyValueStore: KeyValueStore
    private lateinit var timeIntervalRepository: TimeIntervalRepository
    private lateinit var repository: TimeReportRepository

    private lateinit var dataSource: TimeReportDataSource

    @Before
    fun setUp() {
        keyValueStore = InMemoryKeyValueStore()
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        repository = TimeReportInMemoryRepository(timeIntervalRepository)

        dataSource = TimeReportDataSource(
            projectHolder,
            countTimeReports(keyValueStore, repository),
            findTimeReports(keyValueStore, repository)
        )
    }

    @Test
    fun loadInitial_withoutProject() {
        val expected = PositionalDataSourceResult.Initial<TimeReportDay>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withoutTimeIntervals() {
        projectHolder += project
        val expected = PositionalDataSourceResult.Initial<TimeReportDay>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withTimeInterval() {
        projectHolder += project
        val timeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )
        val data = listOf(
            TimeReportDay(
                resetToStartOfDay(timeInterval.start),
                listOf(TimeReportItem(timeInterval))
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
        projectHolder += project
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now - 25.hours
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )
        val data = listOf(
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(TimeReportItem(secondTimeInterval))
            ),
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(TimeReportItem(firstTimeInterval))
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
        projectHolder += project
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now - 25.hours
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )
        val data = listOf(
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(TimeReportItem(secondTimeInterval))
            ),
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(TimeReportItem(firstTimeInterval))
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
        projectHolder += project
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now - 25.hours
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )
        val data = listOf(
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(TimeReportItem(secondTimeInterval))
            ),
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(TimeReportItem(firstTimeInterval))
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

    @Test
    fun loadRange_withoutProject() {
        val expected = PositionalDataSourceResult.Range<TimeReportDay>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withoutTimeIntervals() {
        projectHolder += project
        val expected = PositionalDataSourceResult.Range<TimeReportDay>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withTimeInterval() {
        projectHolder += project
        val timeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )
        val data = listOf(
            TimeReportDay(
                resetToStartOfDay(timeInterval.start),
                listOf(TimeReportItem(timeInterval))
            )
        )
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withTimeIntervals() {
        projectHolder += project
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now - 25.hours
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )
        val data = listOf(
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(TimeReportItem(secondTimeInterval))
            ),
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(TimeReportItem(firstTimeInterval))
            )
        )
        val expected = PositionalDataSourceResult.Range(data)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withTimeIntervalsFilterUsingPosition() {
        projectHolder += project
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now - 25.hours
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )
        val data = listOf(
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(TimeReportItem(secondTimeInterval))
            ),
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(TimeReportItem(firstTimeInterval))
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
        projectHolder += project
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now - 25.hours
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval {
                start = Milliseconds.now
            }
        )
        val data = listOf(
            TimeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(TimeReportItem(secondTimeInterval))
            ),
            TimeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(TimeReportItem(firstTimeInterval))
            )
        )
        val expected = PositionalDataSourceResult.Range(data.take(1))

        dataSource.loadRange(loadRangeParams(loadSize = 1), loadRangeCallback {
            assertEquals(expected, it)
        })
    }
}
