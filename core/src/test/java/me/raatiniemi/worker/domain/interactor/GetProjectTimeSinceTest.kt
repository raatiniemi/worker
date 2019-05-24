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

package me.raatiniemi.worker.domain.interactor

import me.raatiniemi.worker.domain.date.hours
import me.raatiniemi.worker.domain.date.minutes
import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class GetProjectTimeSinceTest {
    private val project = Project(1, "Name")

    private lateinit var repository: TimeIntervalRepository
    private lateinit var getProjectTimeSince: GetProjectTimeSince

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        getProjectTimeSince = GetProjectTimeSince(repository)
    }

    @Test
    fun `get project time since day`() {
        val newTimeInterval = newTimeInterval {
            start = Date(after(TimeIntervalStartingPoint.DAY))
            stop = Date(after(TimeIntervalStartingPoint.DAY, 2.hours))
        }
        repository.add(
            newTimeInterval {
                start = Date(before(TimeIntervalStartingPoint.DAY))
                stop = Date(before(TimeIntervalStartingPoint.DAY, 30.minutes))
            }
        )
        repository.add(newTimeInterval)
        val expected = listOf(
            timeInterval {
                id = 2
                start = newTimeInterval.start
                stopInMilliseconds = requireNotNull(newTimeInterval.stop).value
            }
        )

        val actual = getProjectTimeSince(project, TimeIntervalStartingPoint.DAY)

        assertEquals(expected, actual)
    }

    @Test
    fun `get project time since week`() {
        val newTimeInterval = newTimeInterval {
            start = Date(after(TimeIntervalStartingPoint.WEEK))
            stop = Date(after(TimeIntervalStartingPoint.WEEK, 2.hours))
        }
        repository.add(
            newTimeInterval {
                start = Date(before(TimeIntervalStartingPoint.WEEK))
                stop = Date(before(TimeIntervalStartingPoint.WEEK, 30.minutes))
            }
        )
        repository.add(newTimeInterval)
        val expected = listOf(
            timeInterval {
                id = 2
                start = newTimeInterval.start
                stopInMilliseconds = requireNotNull(newTimeInterval.stop).value
            }
        )

        val actual = getProjectTimeSince(project, TimeIntervalStartingPoint.WEEK)

        assertEquals(expected, actual)
    }

    @Test
    fun `get project time since month`() {
        val newTimeInterval = newTimeInterval {
            start = Date(after(TimeIntervalStartingPoint.MONTH))
            stop = Date(after(TimeIntervalStartingPoint.MONTH, 2.hours))
        }
        repository.add(
            newTimeInterval {
                start = Date(before(TimeIntervalStartingPoint.MONTH))
                stop = Date(before(TimeIntervalStartingPoint.MONTH, 30.minutes))
            }
        )
        repository.add(newTimeInterval)
        val expected = listOf(
            timeInterval {
                id = 2
                start = newTimeInterval.start
                stopInMilliseconds = requireNotNull(newTimeInterval.stop).value
            }
        )

        val actual = getProjectTimeSince(project, TimeIntervalStartingPoint.MONTH)

        assertEquals(expected, actual)
    }
}
