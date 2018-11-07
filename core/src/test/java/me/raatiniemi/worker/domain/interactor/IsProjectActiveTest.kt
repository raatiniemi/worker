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
import me.raatiniemi.worker.util.Optional
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class IsProjectActiveTest {
    private lateinit var repository: TimeIntervalRepository

    @Before
    fun setUp() {
        repository = mock(TimeIntervalRepository::class.java)
    }

    @Test
    fun execute_withoutTime() {
        `when`(repository.findActiveByProjectId(1))
                .thenReturn(Optional.empty())

        val isProjectActive = IsProjectActive(repository)
        assertFalse(isProjectActive.execute(1))
    }

    @Test
    fun execute_withActiveTime() {
        val timeInterval = TimeInterval.builder(1)
                .stopInMilliseconds(0)
                .build()
        `when`(repository.findActiveByProjectId(1))
                .thenReturn(Optional.of(timeInterval))

        val isProjectActive = IsProjectActive(repository)
        assertTrue(isProjectActive.execute(1))
    }
}
