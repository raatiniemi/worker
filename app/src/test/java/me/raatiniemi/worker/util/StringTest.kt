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

package me.raatiniemi.worker.util

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StringTest {
    @Test
    fun `truncate with empty string`() {
        val expected = ""

        val actual = "".truncate(5)

        assertEquals(expected, actual)
    }

    @Test
    fun `truncate with string length below max length`() {
        val expected = "1234"

        val actual = "1234".truncate(5)

        assertEquals(expected, actual)
    }

    @Test
    fun `truncate with string length above max length`() {
        val expected = "12345"

        val actual = "123456789".truncate(5)

        assertEquals(expected, actual)
    }

    @Test
    fun `truncate with max length`() {
        val expected = "12345"

        val actual = "12345".truncate(5)

        assertEquals(expected, actual)
    }
}
