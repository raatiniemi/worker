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
import me.raatiniemi.worker.domain.model.TimeInterval
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
    private lateinit var useCase: MarkRegisteredTime

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        useCase = MarkRegisteredTime(repository)
    }

    @Test
    fun execute_withMultipleUnregisteredItems() {
        val timeIntervals = listOf(
                TimeInterval.builder(1).id(1).build(),
                TimeInterval.builder(1).id(2).build(),
                TimeInterval.builder(1).id(3).build(),
                TimeInterval.builder(1).id(4).build()
        )
        timeIntervals.forEach { repository.add(it) }

        useCase.execute(timeIntervals)

        val expected = listOf(
                TimeInterval.builder(1).id(1).register().build(),
                TimeInterval.builder(1).id(2).register().build(),
                TimeInterval.builder(1).id(3).register().build(),
                TimeInterval.builder(1).id(4).register().build()
        )
        val actual = repository.findAll(Project(1, "Project name"), 0)
        assertEquals(expected, actual)
    }

    @Test
    fun execute_withMultipleRegisteredItems() {
        val timeIntervals = listOf(
                TimeInterval.builder(1).id(1).register().build(),
                TimeInterval.builder(1).id(2).register().build(),
                TimeInterval.builder(1).id(3).register().build(),
                TimeInterval.builder(1).id(4).register().build()
        )
        timeIntervals.forEach { repository.add(it) }

        useCase.execute(timeIntervals)

        val expected = listOf(
                TimeInterval.builder(1).id(1).build(),
                TimeInterval.builder(1).id(2).build(),
                TimeInterval.builder(1).id(3).build(),
                TimeInterval.builder(1).id(4).build()
        )
        val actual = repository.findAll(Project(1, "Project name"), 0)
        assertEquals(expected, actual)
    }

    @Test
    fun execute_withMultipleItems() {
        val timeIntervals = listOf(
                TimeInterval.builder(1).id(1).build(),
                TimeInterval.builder(1).id(2).register().build(),
                TimeInterval.builder(1).id(3).register().build(),
                TimeInterval.builder(1).id(4).register().build()
        )
        timeIntervals.forEach { repository.add(it) }

        useCase.execute(timeIntervals)

        val expected = listOf(
                TimeInterval.builder(1).id(1).register().build(),
                TimeInterval.builder(1).id(2).register().build(),
                TimeInterval.builder(1).id(3).register().build(),
                TimeInterval.builder(1).id(4).register().build()
        )
        val actual = repository.findAll(Project(1, "Project name"), 0)
        assertEquals(expected, actual)
    }
}
