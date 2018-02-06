/*
 * Copyright (C) 2018 Worker Project
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

package me.raatiniemi.worker.domain.repository

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PageRequestTest {
    @Test
    fun withOffsetAndMaxResults() {
        val pageRequest = PageRequest.withOffsetAndMaxResults(offset = 1, maxResults = 2)

        assertEquals(1, pageRequest.offset)
        assertEquals(2, pageRequest.maxResults)
    }

    @Test
    fun withOffset() {
        val pageRequest = PageRequest.withOffset(offset = 1)

        assertEquals(1, pageRequest.offset)
        assertEquals(PageRequest.MAX_RESULTS, pageRequest.maxResults)
    }

    @Test
    fun withMaxResults() {
        val pageRequest = PageRequest.withMaxResults(100)

        assertEquals(0, pageRequest.offset)
        assertEquals(100, pageRequest.maxResults)
    }
}
