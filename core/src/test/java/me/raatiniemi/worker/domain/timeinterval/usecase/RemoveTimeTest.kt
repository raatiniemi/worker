/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
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
class RemoveTimeTest {
    private lateinit var repository: TimeIntervalRepository
    private lateinit var removeTime: RemoveTime

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        removeTime = RemoveTime(repository)
    }

    @Test
    fun `remove with time interval`() = runBlocking {
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = null
        }
        val expected = emptyList<TimeInterval>()

        removeTime(timeInterval)

        val actual = repository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }

    @Test
    fun `remove with time intervals`() = runBlocking {
        repository.add(
            newTimeInterval(android) {
                start = Milliseconds(1)
            }
        )
        val timeInterval = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.stop = null
        }
        val expected = emptyList<TimeInterval>()

        removeTime(listOf(timeInterval))

        val actual = repository.findAll(android, Milliseconds.empty)
        assertEquals(expected, actual)
    }
}
