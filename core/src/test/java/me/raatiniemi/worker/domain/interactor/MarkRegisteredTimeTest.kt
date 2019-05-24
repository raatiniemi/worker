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

import me.raatiniemi.worker.domain.model.Milliseconds
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.newTimeInterval
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class MarkRegisteredTimeTest {
    private val project = Project(1, "Project name")

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
            newTimeInterval { start = Date(1) },
            newTimeInterval { start = Date(1) },
            newTimeInterval { start = Date(1) },
            newTimeInterval { start = Date(1) }
        )
        newTimeIntervals.forEach { repository.add(it) }
        val timeIntervals = listOf(
            timeInterval {
                id = 1
                start = Milliseconds(1)
            },
            timeInterval {
                id = 2
                start = Milliseconds(1)
            },
            timeInterval {
                id = 3
                start = Milliseconds(1)
            },
            timeInterval {
                id = 4
                start = Milliseconds(1)
            }
        )

        markRegisteredTime(timeIntervals)

        val expected = listOf(
            timeInterval {
                id = 1
                start = Milliseconds(1)
                isRegistered = true
            },
            timeInterval {
                id = 2
                start = Milliseconds(1)
                isRegistered = true
            },
            timeInterval {
                id = 3
                start = Milliseconds(1)
                isRegistered = true
            },
            timeInterval {
                id = 4
                start = Milliseconds(1)
                isRegistered = true
            }
        )
        val actual = repository.findAll(project, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun `mark registered time with multiple registered items`() {
        val newTimeIntervals = listOf(
            newTimeInterval {
                start = Date(1)
                isRegistered = true
            },
            newTimeInterval {
                start = Date(1)
                isRegistered = true
            },
            newTimeInterval {
                start = Date(1)
                isRegistered = true
            },
            newTimeInterval {
                start = Date(1)
                isRegistered = true
            }
        )
        newTimeIntervals.forEach { repository.add(it) }
        val timeIntervals = listOf(
            timeInterval {
                id = 1
                start = Milliseconds(1)
                isRegistered = true
            },
            timeInterval {
                id = 2
                start = Milliseconds(1)
                isRegistered = true
            },
            timeInterval {
                id = 3
                start = Milliseconds(1)
                isRegistered = true
            },
            timeInterval {
                id = 4
                start = Milliseconds(1)
                isRegistered = true
            }
        )

        markRegisteredTime(timeIntervals)

        val expected = listOf(
            timeInterval {
                id = 1
                start = Milliseconds(1)
            },
            timeInterval {
                id = 2
                start = Milliseconds(1)
            },
            timeInterval {
                id = 3
                start = Milliseconds(1)
            },
            timeInterval {
                id = 4
                start = Milliseconds(1)
            }
        )
        val actual = repository.findAll(project, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun `mark registered time with multiple items`() {
        val newTimeIntervals = listOf(
            newTimeInterval {
                start = Date(1)
            },
            newTimeInterval {
                start = Date(1)
                isRegistered = true
            },
            newTimeInterval {
                start = Date(1)
                isRegistered = true
            },
            newTimeInterval {
                start = Date(1)
                isRegistered = true
            }
        )
        newTimeIntervals.forEach { repository.add(it) }
        val timeIntervals = listOf(
            timeInterval {
                id = 1
                start = Milliseconds(1)
            },
            timeInterval {
                id = 2
                start = Milliseconds(1)
                isRegistered = true
            },
            timeInterval {
                id = 3
                start = Milliseconds(1)
                isRegistered = true
            },
            timeInterval {
                id = 4
                start = Milliseconds(1)
                isRegistered = true
            }
        )

        markRegisteredTime(timeIntervals)

        val expected = listOf(
            timeInterval {
                id = 1
                start = Milliseconds(1)
                isRegistered = true
            },
            timeInterval {
                id = 2
                start = Milliseconds(1)
                isRegistered = true
            },
            timeInterval {
                id = 3
                start = Milliseconds(1)
                isRegistered = true
            },
            timeInterval {
                id = 4
                start = Milliseconds(1)
                isRegistered = true
            }
        )
        val actual = repository.findAll(project, 0)
        assertEquals(expected, actual)
    }
}
