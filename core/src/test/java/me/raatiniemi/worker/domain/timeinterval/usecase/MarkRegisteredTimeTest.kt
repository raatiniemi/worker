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

package me.raatiniemi.worker.domain.timeinterval.usecase

import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.newTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MarkRegisteredTimeTest {
    private lateinit var repository: TimeIntervalRepository
    private lateinit var markRegisteredTime: MarkRegisteredTime

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        markRegisteredTime =
            MarkRegisteredTime(repository)
    }

    @Test
    fun `mark registered time with multiple unregistered items`() {
        val newTimeIntervals = listOf(
            newTimeInterval(android) {
                start = Milliseconds(1)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        newTimeIntervals.forEach {
            repository.add(it)
                .also { timeInterval ->
                    repository.update(
                        timeInterval.clockOut(stop = Milliseconds(10))
                    )
                }
        }
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(3)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(4)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
            }
        )

        markRegisteredTime(timeIntervals)

        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(3)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(4)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            }
        )
        val actual = repository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun `mark registered time with multiple registered items`() {
        val newTimeIntervals = listOf(
            newTimeInterval(android) {
                start = Milliseconds(1)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        newTimeIntervals.forEach { repository.add(it) }
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(3)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(4)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            }
        )

        markRegisteredTime(timeIntervals)

        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(3)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(4)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
            }
        )
        val actual = repository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun `mark registered time with multiple items`() {
        val newTimeIntervals = listOf(
            newTimeInterval(android) {
                start = Milliseconds(1)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        newTimeIntervals.forEach {
            repository.add(it)
                .also { timeInterval ->
                    repository.update(
                        timeInterval.clockOut(stop = Milliseconds(2))
                    )
                }
        }
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(3)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(4)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            }
        )

        markRegisteredTime(timeIntervals)

        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(3)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(4)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(2)
                builder.isRegistered = true
            }
        )
        val actual = repository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test(expected = UnableToMarkActiveTimeIntervalAsRegisteredException::class)
    fun `mark registered time with active time interval`() {
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
            }
        )

        markRegisteredTime(timeIntervals)
    }

    @Test
    fun `mark registered time with registered active time interval`() {
        val newTimeIntervals = listOf(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        newTimeIntervals.forEach { repository.add(it) }
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(10)
                builder.isRegistered = true
            }
        )

        markRegisteredTime(timeIntervals)

        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(10)
            }
        )
        val actual = repository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }
}