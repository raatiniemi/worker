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
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class RemoveTimeTest {
    private lateinit var repository: TimeIntervalRepository
    private lateinit var useCase: RemoveTime

    @Before
    fun setUp() {
        repository = mock(TimeIntervalRepository::class.java)
        useCase = RemoveTime(repository)
    }

    @Test
    fun execute_withItem() {
        val timeInterval = TimeInterval.builder(1)
                .id(1)
                .build()

        useCase.execute(timeInterval)

        verify<TimeIntervalRepository>(repository).remove(eq(1))
    }

    @Test
    fun execute_withItems() {
        val timeInterval = TimeInterval.builder(1)
                .id(1)
                .build()
        val items = listOf(timeInterval)

        useCase.execute(items)

        verify<TimeIntervalRepository>(repository).remove(eq(items))
    }
}
