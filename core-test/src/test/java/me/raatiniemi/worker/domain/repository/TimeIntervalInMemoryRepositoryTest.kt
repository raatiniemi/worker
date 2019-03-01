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

package me.raatiniemi.worker.domain.repository

import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.newTimeInterval
import me.raatiniemi.worker.domain.model.timeInterval
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeIntervalInMemoryRepositoryTest {
    private val project = Project(1, "Project #1")
    private lateinit var repository: TimeIntervalRepository

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
    }

    @Test
    fun `find all without time intervals`() {
        val expected = emptyList<TimeInterval>()

        val actual = repository.findAll(project, 0)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without time interval for project`() {
        val newTimeInterval = newTimeInterval {
            startInMilliseconds = 10
            stopInMilliseconds = 15
        }
        repository.add(newTimeInterval)
        val expected = emptyList<TimeInterval>()

        val anotherProject = project.copy(id = 2)
        val actual = repository.findAll(anotherProject, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time interval before starting point`() {
        val newTimeInterval = newTimeInterval {
            startInMilliseconds = 10
            stopInMilliseconds = 15
        }
        repository.add(newTimeInterval)
        val expected = emptyList<TimeInterval>()

        val actual = repository.findAll(project, 15)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time interval after starting point`() {
        val newTimeInterval = newTimeInterval {
            startInMilliseconds = 10
            stopInMilliseconds = 15
        }
        repository.add(newTimeInterval)
        val expected = listOf(
                timeInterval {
                    id = 1
                    startInMilliseconds = 10
                    stopInMilliseconds = 15
                }
        )

        val actual = repository.findAll(project, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun `find by id without time interval`() {
        val actual = repository.findById(1)

        assertNull(actual)
    }

    @Test
    fun `find by id with time interval`() {
        val newTimeInterval = newTimeInterval {
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        repository.add(newTimeInterval)
        val expected = timeInterval {
            id = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }

        val actual = repository.findById(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `find active by project id without time intervals`() {
        val actual = repository.findActiveByProjectId(1)

        assertNull(actual)
    }

    @Test
    fun `find active by project id without active time interval`() {
        val newTimeInterval = newTimeInterval {
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        repository.add(newTimeInterval)

        val actual = repository.findActiveByProjectId(1)

        assertNull(actual)
    }

    @Test
    fun `find active by project id with active time interval`() {
        val newTimeInterval = newTimeInterval {
            startInMilliseconds = 1
        }
        repository.add(newTimeInterval)
        val expected = timeInterval {
            id = 1
            startInMilliseconds = 1
        }

        val actual = repository.findActiveByProjectId(1)

        assertEquals(expected, actual)
    }

    @Test
    fun `update without time interval`() {
        val timeInterval = timeInterval {
            id = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }

        val actual = repository.update(timeInterval)

        assertNull(actual)
    }

    @Test
    fun `update with time interval`() {
        val newTimeInterval = newTimeInterval {
            startInMilliseconds = 1
            stopInMilliseconds = 0
        }
        repository.add(newTimeInterval)
        val expected = timeInterval {
            id = 1
            startInMilliseconds = 1
            stopInMilliseconds = 5
        }

        val actual = repository.update(expected)

        assertEquals(expected, actual)
    }

    @Test
    fun `update without time intervals`() {
        val timeIntervals = listOf(
                timeInterval {
                    id = 1
                    startInMilliseconds = 1
                    stopInMilliseconds = 10
                }
        )
        val expected = emptyList<TimeInterval>()

        val actual = repository.update(timeIntervals)

        assertEquals(expected, actual)
    }

    @Test
    fun `update with time intervals`() {
        val newTimeInterval = newTimeInterval {
            startInMilliseconds = 1
            stopInMilliseconds = 0
        }
        repository.add(newTimeInterval)
        val expected = listOf(
                timeInterval {
                    id = 1
                    startInMilliseconds = 1
                    stopInMilliseconds = 5
                }
        )

        val actual = repository.update(expected)

        assertEquals(expected, actual)
    }

    @Test
    fun `remove without time interval`() {
        repository.remove(1)
    }

    @Test
    fun `remove with time interval`() {
        val newTimeInterval = newTimeInterval {
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        repository.add(newTimeInterval)
        val expected = emptyList<TimeInterval>()

        repository.remove(1)

        val actual = repository.findAll(project, 0)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove without time intervals`() {
        val timeInterval = timeInterval {
            id = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val timeIntervals = listOf(timeInterval)

        repository.remove(timeIntervals)
    }

    @Test
    fun `remove with time intervals`() {
        val newTimeInterval = newTimeInterval {
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        repository.add(newTimeInterval)
        val timeIntervals = listOf(
                timeInterval {
                    id = 1
                    startInMilliseconds = 1
                    stopInMilliseconds = 10
                }
        )
        val expected = emptyList<TimeInterval>()

        repository.remove(timeIntervals)

        val actual = repository.findAll(project, 0)
        assertEquals(expected, actual)
    }
}
