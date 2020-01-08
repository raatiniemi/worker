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

package me.raatiniemi.worker.domain.timeinterval.repository

import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.cli
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.newTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeIntervalInMemoryRepositoryTest {
    private lateinit var repository: TimeIntervalRepository

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
    }

    @Test
    fun `find all without time intervals`() {
        val expected = emptyList<TimeInterval>()

        val actual = repository.findAll(android, Milliseconds.empty)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all without time interval for project`() {
        val expected = emptyList<TimeInterval>()
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )

        val actual = repository.findAll(cli, Milliseconds.empty)

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time interval before starting point`() {
        val expected = emptyList<TimeInterval>()
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(10)
            }
        )

        val actual = repository.findAll(android, Milliseconds(15))

        assertEquals(expected, actual)
    }

    @Test
    fun `find all with time interval after starting point`() {
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(10)
            }
        )
        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(10)
                builder.stop = null
            }
        )

        val actual = repository.findAll(android, Milliseconds(1))

        assertEquals(expected, actual)
    }

    @Test
    fun `find by id without time interval`() {
        val actual = repository.findById(TimeIntervalId(1))

        assertNull(actual)
    }

    @Test
    fun `find by id with time interval`() {
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val expected = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = null
        }

        val actual = repository.findById(TimeIntervalId(1))

        assertEquals(expected, actual)
    }

    @Test
    fun `find active by project id without time intervals`() {
        val actual = repository.findActiveByProjectId(android.id)

        assertNull(actual)
    }

    @Test
    fun `find active by project id without active time interval`() {
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        ).also {
            repository.update(it.clockOut(stop = Milliseconds(10)))
        }

        val actual = repository.findActiveByProjectId(android.id)

        assertNull(actual)
    }

    @Test
    fun `find active by project id with active time interval`() {
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val expected = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
        }

        val actual = repository.findActiveByProjectId(android.id)

        assertEquals(expected, actual)
    }

    @Test
    fun `update without time interval`() {
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(10)
        }

        val actual = repository.update(timeInterval)

        assertNull(actual)
    }

    @Test
    fun `update with time interval`() {
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val expected = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(5)
        }

        val actual = repository.update(expected)

        assertEquals(expected, actual)
    }

    @Test
    fun `update without time intervals`() {
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(10)
            }
        )
        val expected = emptyList<TimeInterval>()

        val actual = repository.update(timeIntervals)

        assertEquals(expected, actual)
    }

    @Test
    fun `update with time intervals`() {
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val expected = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = Milliseconds(5)
            }
        )

        val actual = repository.update(expected)

        assertEquals(expected, actual)
    }

    @Test
    fun `remove without time interval`() {
        repository.remove(TimeIntervalId(1))
    }

    @Test
    fun `remove with time interval`() {
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val expected = emptyList<TimeInterval>()

        repository.remove(TimeIntervalId(1))

        val actual = repository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove without time intervals`() {
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(10)
        }
        val timeIntervals = listOf(timeInterval)

        repository.remove(timeIntervals)
    }

    @Test
    fun `remove with time intervals`() {
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = Milliseconds(1)
                builder.stop = null
            }
        )
        val expected = emptyList<TimeInterval>()

        repository.remove(timeIntervals)

        val actual = repository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }
}