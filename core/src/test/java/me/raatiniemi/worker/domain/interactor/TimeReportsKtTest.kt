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

package me.raatiniemi.worker.domain.interactor

import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.newTimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.repository.TimeReportInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeReportRepository
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import me.raatiniemi.worker.util.KeyValueStore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class TimeReportsKtTest {
    private val project = Project(1, "Project #1")

    private lateinit var keyValueStore: KeyValueStore
    private lateinit var timeIntervalRepository: TimeIntervalRepository
    private lateinit var repository: TimeReportRepository

    private lateinit var countTimeReports: CountTimeReports

    @Before
    fun setUp() {
        keyValueStore = InMemoryKeyValueStore()
        timeIntervalRepository = TimeIntervalInMemoryRepository()
        repository = TimeReportInMemoryRepository(timeIntervalRepository)

        countTimeReports = countTimeReports(keyValueStore, repository)
    }

    @Test
    fun `count time reports without time intervals`() {
        val expected = 0

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time reports with unregistered time interval`() {
        val expected = 1
        timeIntervalRepository.add(newTimeInterval { })

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time reports with unregistered time intervals on same day`() {
        val expected = 1
        timeIntervalRepository.add(newTimeInterval { })
        timeIntervalRepository.add(newTimeInterval { })

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time reports with unregistered time intervals on different days`() {
        val expected = 2
        timeIntervalRepository.add(newTimeInterval { })
        timeIntervalRepository.add(
            newTimeInterval {
                startInMilliseconds = Date().time
            }
        )

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time reports with registered time interval`() {
        val expected = 1
        timeIntervalRepository.add(
            newTimeInterval {
                isRegistered = true
            }
        )

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time reports with registered time interval when hiding registered time`() {
        val expected = 0
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        timeIntervalRepository.add(
            newTimeInterval {
                isRegistered = true
            }
        )

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time reports with registered time intervals on same day when hiding registered time`() {
        val expected = 0
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        timeIntervalRepository.add(
            newTimeInterval {
                isRegistered = true
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                isRegistered = true
            }
        )

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time reports with registered time intervals on different days when hiding registered time`() {
        val expected = 0
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        timeIntervalRepository.add(
            newTimeInterval {
                isRegistered = true
            }
        )
        timeIntervalRepository.add(
            newTimeInterval {
                startInMilliseconds = Date().time
                isRegistered = true
            }
        )

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time reports with time intervals on same day`() {
        val expected = 1
        timeIntervalRepository.add(newTimeInterval { })
        timeIntervalRepository.add(
            newTimeInterval {
                isRegistered = true
            }
        )

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time reports with time intervals on different days`() {
        val expected = 2
        timeIntervalRepository.add(newTimeInterval { })
        timeIntervalRepository.add(
            newTimeInterval {
                startInMilliseconds = Date().time
                isRegistered = true
            }
        )

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time reports with time intervals on same day when hiding registered time`() {
        val expected = 1
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        timeIntervalRepository.add(newTimeInterval { })
        timeIntervalRepository.add(
            newTimeInterval {
                isRegistered = true
            }
        )

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }

    @Test
    fun `count time reports with time intervals on different days when hiding registered time`() {
        val expected = 1
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)
        timeIntervalRepository.add(newTimeInterval { })
        timeIntervalRepository.add(
            newTimeInterval {
                startInMilliseconds = Date().time
                isRegistered = true
            }
        )

        val actual = countTimeReports(project)

        assertEquals(expected, actual)
    }
}
