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
class RemoveTimeTest {
    private lateinit var repository: TimeIntervalRepository
    private lateinit var removeTime: RemoveTime

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        removeTime = RemoveTime(repository)
    }

    @Test
    fun `remove with time interval`() {
        repository.add(
            newTimeInterval {
                start = Milliseconds(1)
                stop = Milliseconds(10)
            }
        )
        val timeInterval = timeInterval {
            id = 1
            start = Milliseconds(1)
            stop = Milliseconds(10)
        }
        val expected = emptyList<TimeInterval>()

        removeTime(timeInterval)

        val actual = repository.findAll(Project(1, ProjectName("Project name")), Milliseconds(0))
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with time intervals`() {
        repository.add(
            newTimeInterval {
                start = Milliseconds(1)
                stop = Milliseconds(10)
            }
        )
        val timeInterval = timeInterval {
            id = 1
            start = Milliseconds(1)
            stop = Milliseconds(10)
        }
        val expected = emptyList<TimeInterval>()

        removeTime(listOf(timeInterval))

        val actual = repository.findAll(Project(1, ProjectName("Project name")), Milliseconds(0))
        assertEquals(expected, actual)
    }
}
