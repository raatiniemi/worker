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

package me.raatiniemi.worker.data.projects

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeReportQueryGroupTest {
    @Test
    fun `iterator without items`() {
        val expected = emptyList<String>()
        val group = TimeReportQueryGroup(1, "")

        val actual = group.mapNotNull { it }

        assertEquals(expected, actual)
    }

    @Test
    fun `iterator with item`() {
        val expected = listOf<Long>(5)
        val group = TimeReportQueryGroup(1, "5")

        val actual = group.mapNotNull { it }

        assertEquals(expected, actual)
    }

    @Test
    fun `iterator with items`() {
        val expected = listOf<Long>(5, 3, 4, 1)
        val group = TimeReportQueryGroup(1, "5,3,4,1")

        val actual = group.toList()

        assertEquals(expected, actual)
    }
}
