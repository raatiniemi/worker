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

package me.raatiniemi.worker.domain.usecase

import me.raatiniemi.worker.domain.date.hours
import me.raatiniemi.worker.domain.date.minutes
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import me.raatiniemi.worker.domain.timeinterval.model.*
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalStartingPoint
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetProjectTimeSinceTest {
    private lateinit var repository: TimeIntervalRepository
    private lateinit var getProjectTimeSince: GetProjectTimeSince

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        getProjectTimeSince = GetProjectTimeSince(repository)
    }

    @Test
    fun `get project time since day`() {
        val expectedStop = after(TimeIntervalStartingPoint.DAY, 2.hours)
        val newTimeInterval = newTimeInterval(android) {
            start = after(TimeIntervalStartingPoint.DAY)
        }
        repository.add(
            newTimeInterval(android) {
                start = before(TimeIntervalStartingPoint.DAY)
            }
        ).also {
            repository.update(it.clockOut(stop = before(TimeIntervalStartingPoint.DAY, 30.minutes)))
        }
        repository.add(newTimeInterval)
            .also {
                repository.update(it.clockOut(stop = expectedStop))
            }
        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = newTimeInterval.start
                builder.stop = expectedStop
            }
        )

        val actual = getProjectTimeSince(android, TimeIntervalStartingPoint.DAY)

        assertEquals(expected, actual)
    }

    @Test
    fun `get project time since week`() {
        val expectedStop = after(TimeIntervalStartingPoint.WEEK, 2.hours)
        val newTimeInterval = newTimeInterval(android) {
            start = after(TimeIntervalStartingPoint.WEEK)
        }
        repository.add(
            newTimeInterval(android) {
                start = before(TimeIntervalStartingPoint.WEEK)
            }
        ).also {
            repository.update(
                it.clockOut(
                    stop = before(
                        TimeIntervalStartingPoint.WEEK,
                        30.minutes
                    )
                )
            )
        }
        repository.add(newTimeInterval)
            .also {
                repository.update(it.clockOut(stop = expectedStop))
            }
        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = newTimeInterval.start
                builder.stop = expectedStop
            }
        )

        val actual = getProjectTimeSince(android, TimeIntervalStartingPoint.WEEK)

        assertEquals(expected, actual)
    }

    @Test
    fun `get project time since month`() {
        val expectedStop = after(TimeIntervalStartingPoint.MONTH, 2.hours)
        val newTimeInterval = newTimeInterval(android) {
            start = after(TimeIntervalStartingPoint.MONTH)
        }
        repository.add(
            newTimeInterval(android) {
                start = before(TimeIntervalStartingPoint.MONTH)
            }
        ).also {
            repository.update(
                it.clockOut(
                    stop = before(
                        TimeIntervalStartingPoint.MONTH,
                        30.minutes
                    )
                )
            )
        }
        repository.add(newTimeInterval)
            .also {
                repository.update(it.clockOut(stop = expectedStop))
            }
        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = newTimeInterval.start
                builder.stop = expectedStop
            }
        )

        val actual = getProjectTimeSince(android, TimeIntervalStartingPoint.MONTH)

        assertEquals(expected, actual)
    }
}
