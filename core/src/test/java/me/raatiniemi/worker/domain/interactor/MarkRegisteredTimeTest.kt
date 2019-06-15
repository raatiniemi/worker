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

import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
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
        markRegisteredTime = MarkRegisteredTime(repository)
    }

    @Test
    fun `mark registered time with multiple unregistered items`() {
        val newTimeIntervals = listOf(
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
            }
        )
        newTimeIntervals.forEach { repository.add(it) }
        val timeIntervals = listOf(
            timeInterval(android) {
                id = 1
                start = Milliseconds(1)
                stop = Milliseconds(2)
            },
            timeInterval(android) {
                id = 2
                start = Milliseconds(1)
                stop = Milliseconds(2)
            },
            timeInterval(android) {
                id = 3
                start = Milliseconds(1)
                stop = Milliseconds(2)
            },
            timeInterval(android) {
                id = 4
                start = Milliseconds(1)
                stop = Milliseconds(2)
            }
        )

        markRegisteredTime(timeIntervals)

        val expected = listOf(
            timeInterval(android) {
                id = 1
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            timeInterval(android) {
                id = 2
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            timeInterval(android) {
                id = 3
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            timeInterval(android) {
                id = 4
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
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
                stop = Milliseconds(2)
                isRegistered = true
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            }
        )
        newTimeIntervals.forEach { repository.add(it) }
        val timeIntervals = listOf(
            timeInterval(android) {
                id = 1
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            timeInterval(android) {
                id = 2
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            timeInterval(android) {
                id = 3
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            timeInterval(android) {
                id = 4
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            }
        )

        markRegisteredTime(timeIntervals)

        val expected = listOf(
            timeInterval(android) {
                id = 1
                start = Milliseconds(1)
                stop = Milliseconds(2)
            },
            timeInterval(android) {
                id = 2
                start = Milliseconds(1)
                stop = Milliseconds(2)
            },
            timeInterval(android) {
                id = 3
                start = Milliseconds(1)
                stop = Milliseconds(2)
            },
            timeInterval(android) {
                id = 4
                start = Milliseconds(1)
                stop = Milliseconds(2)
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
                stop = Milliseconds(2)
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            newTimeInterval(android) {
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            }
        )
        newTimeIntervals.forEach { repository.add(it) }
        val timeIntervals = listOf(
            timeInterval(android) {
                id = 1
                start = Milliseconds(1)
                stop = Milliseconds(2)
            },
            timeInterval(android) {
                id = 2
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            timeInterval(android) {
                id = 3
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            timeInterval(android) {
                id = 4
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            }
        )

        markRegisteredTime(timeIntervals)

        val expected = listOf(
            timeInterval(android) {
                id = 1
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            timeInterval(android) {
                id = 2
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            timeInterval(android) {
                id = 3
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            },
            timeInterval(android) {
                id = 4
                start = Milliseconds(1)
                stop = Milliseconds(2)
                isRegistered = true
            }
        )
        val actual = repository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test(expected = UnableToMarkActiveTimeIntervalAsRegisteredException::class)
    fun `mark registered time with active time interval`() {
        val timeIntervals = listOf(
            timeInterval(android) {
                id = 1
                start = Milliseconds(1)
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
            timeInterval(android) {
                id = 1
                start = Milliseconds(1)
                isRegistered = true
            }
        )

        markRegisteredTime(timeIntervals)

        val expected = listOf(
            timeInterval(android) {
                id = 1
                start = Milliseconds(1)
            }
        )
        val actual = repository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }
}
