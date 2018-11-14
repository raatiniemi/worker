/*
 * Copyright (C) 2018 Worker Project
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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeIntervalInMemoryRepositoryTest {
    private lateinit var repository: TimeIntervalRepository
    private val project = Project(1, "Project #1")

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
    }

    @Test
    fun `findAll withoutTimeIntervals`() {
        val actual = repository.findAll(project, 0)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun `findAll withoutTimeIntervalForProject`() {
        val timeInterval = TimeInterval(projectId = 1, startInMilliseconds = 10, stopInMilliseconds = 15)
        repository.add(timeInterval)

        val actual = repository.findAll(project.copy(id = 2), 1)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun `findAll withTimeIntervalBefore`() {
        repository.add(TimeInterval(projectId = 1, startInMilliseconds = 10, stopInMilliseconds = 15))

        val actual = repository.findAll(project, 15)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun `findAll withTimeIntervalAfter`() {
        val timeInterval = TimeInterval(projectId = 1, startInMilliseconds = 10, stopInMilliseconds = 15)
        repository.add(timeInterval)
        val expected = listOf(timeInterval.copy(id = 1))

        val actual = repository.findAll(project, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun `findById withoutTimeIntervals`() {
        val actual = repository.findById(1)

        assertFalse(actual.isPresent)
    }

    @Test
    fun `findById withTimeInterval`() {
        val timeInterval = TimeInterval(projectId = 1, startInMilliseconds = 1, stopInMilliseconds = 10)
        repository.add(timeInterval)
        val expected = timeInterval.copy(id = 1)

        val actual = repository.findById(1)

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun `findActiveByProjectId withoutTimeIntervals`() {
        val actual = repository.findActiveByProjectId(1)

        assertFalse(actual.isPresent)
    }

    @Test
    fun `findActiveByProjectId withoutActiveTimeIntervals`() {
        val timeInterval = TimeInterval(projectId = 1, startInMilliseconds = 1, stopInMilliseconds = 10)
        repository.add(timeInterval)

        val actual = repository.findActiveByProjectId(1)

        assertFalse(actual.isPresent)
    }

    @Test
    fun `findActiveByProjectId withTimeInterval`() {
        val timeInterval = TimeInterval(projectId = 1, startInMilliseconds = 1, stopInMilliseconds = 0)
        repository.add(timeInterval)
        val expected = timeInterval.copy(id = 1)

        val actual = repository.findActiveByProjectId(1)

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun `update withoutTimeInterval`() {
        val timeInterval = TimeInterval(
                id = 1,
                projectId = 1,
                startInMilliseconds = 1,
                stopInMilliseconds = 10,
                isRegistered = true
        )

        val actual = repository.update(timeInterval)

        assertFalse(actual.isPresent)
    }

    @Test
    fun `update withTimeInterval`() {
        val timeInterval = TimeInterval(
                id = 1,
                projectId = 1,
                startInMilliseconds = 1,
                stopInMilliseconds = 0
        )
        repository.add(timeInterval)
        val expected = timeInterval.copy(
                stopInMilliseconds = 5
        )

        val actual = repository.update(expected)

        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun `update withoutTimeIntervals`() {
        val timeIntervals = listOf(
                TimeInterval(
                        id = 1,
                        projectId = 1,
                        startInMilliseconds = 1,
                        stopInMilliseconds = 10,
                        isRegistered = true
                )
        )

        val actual = repository.update(timeIntervals)

        assertEquals(emptyList<TimeInterval>(), actual)
    }

    @Test
    fun `update withTimeIntervals`() {
        val timeInterval = TimeInterval(
                id = 1,
                projectId = 1,
                startInMilliseconds = 1,
                stopInMilliseconds = 0
        )
        repository.add(timeInterval)
        val expected = listOf(timeInterval.copy(stopInMilliseconds = 5))

        val actual = repository.update(expected)

        assertEquals(expected, actual)
    }

    @Test
    fun `remove withoutTimeInterval`() {
        repository.remove(1)
    }

    @Test
    fun `remove withTimeInterval`() {
        val timeInterval = TimeInterval(
                id = 1,
                projectId = 1,
                startInMilliseconds = 1,
                stopInMilliseconds = 10,
                isRegistered = true
        )
        repository.add(timeInterval)

        repository.remove(1)

        val actual = repository.findById(1)
        assertFalse(actual.isPresent)
    }

    @Test
    fun `remove withoutTimeIntervals`() {
        val timeInterval = TimeInterval(
                id = 1,
                projectId = 1,
                startInMilliseconds = 1,
                stopInMilliseconds = 10,
                isRegistered = true
        )

        repository.remove(listOf(timeInterval))
    }

    @Test
    fun `remove withTimeIntervals`() {
        val timeInterval = TimeInterval(
                id = 1,
                projectId = 1,
                startInMilliseconds = 1,
                stopInMilliseconds = 10,
                isRegistered = true
        )
        repository.add(timeInterval)

        repository.remove(listOf(timeInterval))

        val actual = repository.findAll(project, 0)
        assertEquals(emptyList<TimeInterval>(), actual)
    }
}
