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

import me.raatiniemi.worker.domain.model.newTimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class IsProjectActiveTest {
    private lateinit var repository: TimeIntervalRepository
    private lateinit var isProjectActive: IsProjectActive

    @Before
    fun setUp() {
        repository = TimeIntervalInMemoryRepository()
        isProjectActive = IsProjectActive(repository)
    }

    @Test
    fun `is project active without time interval`() {
        val actual = isProjectActive(1)

        assertFalse(actual)
    }

    @Test
    fun `is project active with active time interval`() {
        val newTimeInterval = newTimeInterval {
            start = Date(1)
            stopInMilliseconds = 0
        }
        repository.add(newTimeInterval)

        val actual = isProjectActive(1)

        assertTrue(actual)
    }
}
