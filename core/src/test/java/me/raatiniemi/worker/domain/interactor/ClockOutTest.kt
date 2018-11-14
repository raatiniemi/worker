/*
 * Copyright (C) 2017 Worker Project
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

import me.raatiniemi.worker.domain.exception.InactiveProjectException
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class ClockOutTest {
    private lateinit var repository: TimeIntervalRepository
    private lateinit var useCase: ClockOut

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        useCase = ClockOut(repository)
    }

    @Test(expected = InactiveProjectException::class)
    fun execute_withoutActiveTime() {
        useCase.execute(1L, Date())
    }

    @Test
    fun execute() {
        val date = Date()
        val timeInterval = TimeInterval.builder(1)
                .id(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(0)
                .build()
        repository.add(timeInterval)
        val expected = listOf(timeInterval.copy(stopInMilliseconds = date.time))

        useCase.execute(1L, date)

        val actual = repository.findAll(Project(1, "Project name"), 0)
        assertEquals(expected, actual)
    }
}
