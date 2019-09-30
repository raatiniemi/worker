/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.date

import me.raatiniemi.worker.domain.time.hours
import me.raatiniemi.worker.domain.time.milliseconds
import me.raatiniemi.worker.domain.time.minutes
import me.raatiniemi.worker.domain.time.seconds
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class DateTest {
    private val initialValue = Date(0)

    @Test
    fun `plus one millisecond`() {
        val expected = Date(1)

        val actual = initialValue + 1.milliseconds

        assertEquals(expected, actual)
    }

    @Test
    fun `plus one second`() {
        val expected = Date(1_000)

        val actual = initialValue + 1.seconds

        assertEquals(expected, actual)
    }

    @Test
    fun `plus one minute`() {
        val expected = Date(60_000)

        val actual = initialValue + 1.minutes

        assertEquals(expected, actual)
    }

    @Test
    fun `plus one hour`() {
        val expected = Date(3_600_000)

        val actual = initialValue + 1.hours

        assertEquals(expected, actual)
    }

    @Test
    fun `minus one millisecond`() {
        val value = Date(1)

        val actual = value - 1.milliseconds

        assertEquals(initialValue, actual)
    }

    @Test
    fun `minus one second`() {
        val value = Date(1_000)

        val actual = value - 1.seconds

        assertEquals(initialValue, actual)
    }

    @Test
    fun `minus one minute`() {
        val value = Date(60_000)

        val actual = value - 1.minutes

        assertEquals(initialValue, actual)
    }

    @Test
    fun `minus one hour`() {
        val value = Date(3_600_000)

        val actual = value - 1.hours

        assertEquals(initialValue, actual)
    }
}
