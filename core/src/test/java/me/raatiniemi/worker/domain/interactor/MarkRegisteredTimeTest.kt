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

import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.timeInterval
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
    fun execute_withMultipleUnregisteredItems() {
        val timeIntervals = listOf(
                timeInterval { id = 1 },
                timeInterval { id = 2 },
                timeInterval { id = 3 },
                timeInterval { id = 4 }
        )
        timeIntervals.forEach { repository.add(it) }

        markRegisteredTime(timeIntervals)

        val expected = listOf(
                timeInterval {
                    id = 1
                    isRegistered = true
                },
                timeInterval {
                    id = 2
                    isRegistered = true
                },
                timeInterval {
                    id = 3
                    isRegistered = true
                },
                timeInterval {
                    id = 4
                    isRegistered = true
                }
        )
        val actual = repository.findAll(Project(1, "Project name"), 0)
        assertEquals(expected, actual)
    }

    @Test
    fun execute_withMultipleRegisteredItems() {
        val timeIntervals = listOf(
                timeInterval {
                    id = 1
                    isRegistered = true
                },
                timeInterval {
                    id = 2
                    isRegistered = true
                },
                timeInterval {
                    id = 3
                    isRegistered = true
                },
                timeInterval {
                    id = 4
                    isRegistered = true
                }
        )
        timeIntervals.forEach { repository.add(it) }

        markRegisteredTime(timeIntervals)

        val expected = listOf(
                timeInterval { id = 1 },
                timeInterval { id = 2 },
                timeInterval { id = 3 },
                timeInterval { id = 4 }
        )
        val actual = repository.findAll(Project(1, "Project name"), 0)
        assertEquals(expected, actual)
    }

    @Test
    fun execute_withMultipleItems() {
        val timeIntervals = listOf(
                timeInterval {
                    id = 1
                },
                timeInterval {
                    id = 2
                    isRegistered = true
                },
                timeInterval {
                    id = 3
                    isRegistered = true
                },
                timeInterval {
                    id = 4
                    isRegistered = true
                }
        )
        timeIntervals.forEach { repository.add(it) }

        markRegisteredTime(timeIntervals)

        val expected = listOf(
                timeInterval {
                    id = 1
                    isRegistered = true
                },
                timeInterval {
                    id = 2
                    isRegistered = true
                },
                timeInterval {
                    id = 3
                    isRegistered = true
                },
                timeInterval {
                    id = 4
                    isRegistered = true
                }
        )
        val actual = repository.findAll(Project(1, "Project name"), 0)
        assertEquals(expected, actual)
    }
}
