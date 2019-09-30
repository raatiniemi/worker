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

package me.raatiniemi.worker.domain.timereport.usecase

import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.InMemoryKeyValueStore
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.model.LoadPosition
import me.raatiniemi.worker.domain.model.LoadRange
import me.raatiniemi.worker.domain.model.LoadSize
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.repository.resetToStartOfDay
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.hours
import me.raatiniemi.worker.domain.time.minutes
import me.raatiniemi.worker.domain.timeinterval.model.newTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.timereport.repository.TimeReportRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FindTimeReportsTest {
    private lateinit var keyValueStore: KeyValueStore
    private lateinit var timeIntervalRepository: TimeIntervalRepository
    private lateinit var repository: TimeReportRepository

    private lateinit var findTimeReports: FindTimeReports

    @Before
    fun setUp() {
        keyValueStore = InMemoryKeyValueStore()
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        repository = TimeReportInMemoryRepository(timeIntervalRepository)

        findTimeReports = FindTimeReports(keyValueStore, repository)
    }

    @Test
    fun `find time reports without time intervals`() {
        val expected = emptyList<TimeReportDay>()
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with unregistered time interval`() {
        val timeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(timeInterval.start),
                listOf(timeInterval)
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with unregistered time intervals on same day`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(11)
            }
        )
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    secondTimeInterval,
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with unregistered time intervals on different days`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now - 25.hours
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        )
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(
                    secondTimeInterval
                )
            ),
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with registered time interval`() {
        val timeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds.now
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(timeInterval.start),
                listOf(
                    timeInterval
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with registered time intervals on same day`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds.now
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now + 1.minutes
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds.now + 5.minutes
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    secondTimeInterval,
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with registered time intervals on different days`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now - 25.hours
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds.now
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds.now + 5.minutes
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(
                    secondTimeInterval
                )
            ),
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with time intervals on same day`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds(11)
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds.now
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    secondTimeInterval,
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with time intervals on different days`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now - 25.hours
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds.now
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(
                    secondTimeInterval
                )
            ),
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with time intervals on same day when hiding registered time`() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        )
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds.now
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with time intervals on different days when hiding registered time`() {
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now - 25.hours
            }
        )
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        ).let {
            timeInterval(it) { builder ->
                builder.stop = Milliseconds.now
                builder.isRegistered = true
            }
        }.also {
            timeIntervalRepository.update(it)
        }
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with time intervals filter using position`() {
        val firstTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now - 25.hours
            }
        )
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        )
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(firstTimeInterval.start),
                listOf(
                    firstTimeInterval
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(1), LoadSize(10))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }

    @Test
    fun `find time reports with time intervals filter using page size`() {
        timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now - 25.hours
            }
        )
        val secondTimeInterval = timeIntervalRepository.add(
            newTimeInterval(android) {
                start = Milliseconds.now
            }
        )
        val expected = listOf(
            timeReportDay(
                resetToStartOfDay(secondTimeInterval.start),
                listOf(
                    secondTimeInterval
                )
            )
        )
        val loadRange = LoadRange(LoadPosition(0), LoadSize(1))

        val actual = findTimeReports(android, loadRange)

        assertEquals(expected, actual)
    }
}
