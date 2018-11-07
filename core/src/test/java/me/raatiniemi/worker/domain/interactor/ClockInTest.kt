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

import me.raatiniemi.worker.domain.exception.ActiveProjectException
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import me.raatiniemi.worker.util.Optional
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import java.util.*

@RunWith(JUnit4::class)
class ClockInTest {
    private lateinit var repository: TimeIntervalRepository

    @Before
    fun setUp() {
        repository = mock(TimeIntervalRepository::class.java)
    }

    @Test(expected = ActiveProjectException::class)
    fun execute_withActiveTime() {
        val timeInterval = TimeInterval.builder(1)
                .stopInMilliseconds(0)
                .build()
        `when`(repository.findActiveByProjectId(1))
                .thenReturn(Optional.of(timeInterval))

        val clockIn = ClockIn(repository)
        clockIn.execute(1, Date())
    }

    @Test
    fun execute() {
        `when`(repository.findActiveByProjectId(1))
                .thenReturn(Optional.empty())

        val clockIn = ClockIn(repository)
        clockIn.execute(1, Date())

        verify<TimeIntervalRepository>(repository).add(isA(TimeInterval::class.java))
    }
}
