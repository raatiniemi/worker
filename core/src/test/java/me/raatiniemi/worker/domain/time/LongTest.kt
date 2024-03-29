/*
 * Copyright (C) 2022 Tobias Raatiniemi
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
class LongTest {
    @Test
    fun milliseconds() {
        val expected = 200L

        val actual = 200L.milliseconds

        assertEquals(expected, actual)
    }

    @Test
    fun seconds() {
        val expected = 1_000L

        val actual = 1L.seconds

        assertEquals(expected, actual)
    }

    @Test
    fun minutes() {
        val expected = 60_000L

        val actual = 1L.minutes

        assertEquals(expected, actual)
    }

    @Test
    fun hours() {
        val expected = 3_600_000L

        val actual = 1L.hours

        assertEquals(expected, actual)
    }

    @Test
    fun days() {
        val expected = 86_400_000L

        val actual = 1L.days

        assertEquals(expected, actual)
    }

    @Test
    fun weeks() {
        val expected = 604_800_000L

        val actual = 1L.weeks

        assertEquals(expected, actual)
    }
}
