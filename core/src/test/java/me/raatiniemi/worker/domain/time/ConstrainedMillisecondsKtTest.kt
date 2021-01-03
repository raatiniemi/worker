/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.time

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ConstrainedMillisecondsKtTest {
    @Test
    fun `constrained milliseconds with value between min and max`() {
        val now = Milliseconds.now
        val lowerBound = now - 1.hours
        val upperBound = now + 1.hours

        val actual = constrainedMilliseconds(now, lowerBound, upperBound)

        assertEquals(now, actual)
    }

    @Test
    fun `constrained milliseconds with grater than max`() {
        val now = Milliseconds.now
        val milliseconds = now + 3.hours
        val lowerBound = now - 1.hours
        val upperBound = now + 1.hours

        val actual = constrainedMilliseconds(milliseconds, lowerBound, upperBound)

        assertEquals(upperBound, actual)
    }

    @Test
    fun `constrained milliseconds with equal to max`() {
        val now = Milliseconds.now
        val lowerBound = now - 1.hours
        val upperBound = now + 1.hours

        val actual = constrainedMilliseconds(upperBound, lowerBound, upperBound)

        assertEquals(upperBound, actual)
    }

    @Test
    fun `constrained milliseconds with less than min`() {
        val now = Milliseconds.now
        val milliseconds = now - 3.hours
        val lowerBound = now - 1.hours
        val upperBound = now + 1.hours

        val actual = constrainedMilliseconds(milliseconds, lowerBound, upperBound)

        assertEquals(lowerBound, actual)
    }

    @Test
    fun `constrained milliseconds with equal to min`() {
        val now = Milliseconds.now
        val lowerBound = now - 1.hours
        val upperBound = now + 1.hours

        val actual = constrainedMilliseconds(lowerBound, lowerBound, upperBound)

        assertEquals(lowerBound, actual)
    }
    @Test
    fun `constrained milliseconds with min and max swapped`() {
        val now = Milliseconds.now
        val upperBound = now - 1.hours
        val lowerBound = now + 1.hours

        val actual = constrainedMilliseconds(now, lowerBound, upperBound)

        assertEquals(now, actual)
    }
}
