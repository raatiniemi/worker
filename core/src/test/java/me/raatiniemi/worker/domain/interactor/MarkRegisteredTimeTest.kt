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

import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class MarkRegisteredTimeTest {
    private lateinit var repository: TimeIntervalRepository

    @Before
    fun setUp() {
        repository = mock(TimeIntervalRepository::class.java)
    }

    @Test
    fun execute_withMultipleUnregisteredItems() {
        val timeIntervalsToUpdate = listOf(
                TimeInterval.builder(1).build(),
                TimeInterval.builder(1).build(),
                TimeInterval.builder(1).build(),
                TimeInterval.builder(1).build()
        )

        val markRegisteredTime = MarkRegisteredTime(repository)
        markRegisteredTime.execute(timeIntervalsToUpdate)

        val expectedTimeIntervals = listOf(
                TimeInterval.builder(1).register().build(),
                TimeInterval.builder(1).register().build(),
                TimeInterval.builder(1).register().build(),
                TimeInterval.builder(1).register().build()
        )
        verify<TimeIntervalRepository>(repository, times(1)).update(expectedTimeIntervals)
    }

    @Test
    fun execute_withMultipleRegisteredItems() {
        val timeIntervalsToUpdate = listOf(
                TimeInterval.builder(1).register().build(),
                TimeInterval.builder(1).register().build(),
                TimeInterval.builder(1).register().build(),
                TimeInterval.builder(1).register().build()
        )

        val markRegisteredTime = MarkRegisteredTime(repository)
        markRegisteredTime.execute(timeIntervalsToUpdate)

        val expectedTimeInterval = listOf(
                TimeInterval.builder(1).build(),
                TimeInterval.builder(1).build(),
                TimeInterval.builder(1).build(),
                TimeInterval.builder(1).build()
        )
        verify<TimeIntervalRepository>(repository, times(1)).update(expectedTimeInterval)
    }

    @Test
    fun execute_withMultipleItems() {
        val timeIntervalsToUpdate = listOf(
                TimeInterval.builder(1).build(),
                TimeInterval.builder(1).register().build(),
                TimeInterval.builder(1).register().build(),
                TimeInterval.builder(1).register().build()
        )

        val markRegisteredTime = MarkRegisteredTime(repository)
        markRegisteredTime.execute(timeIntervalsToUpdate)

        val expectedTimeInterval = listOf(
                TimeInterval.builder(1).register().build(),
                TimeInterval.builder(1).register().build(),
                TimeInterval.builder(1).register().build(),
                TimeInterval.builder(1).register().build()
        )
        verify<TimeIntervalRepository>(repository, times(1)).update(expectedTimeInterval)
    }
}
