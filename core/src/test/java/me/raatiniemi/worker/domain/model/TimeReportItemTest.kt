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

package me.raatiniemi.worker.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TimeReportItemTest {
    @Test
    fun asTime() {
        val timeInterval = timeInterval(android) {
            start = Milliseconds(1)
        }
        val item = TimeReportItem.with(timeInterval)

        assertSame(timeInterval, item.asTimeInterval())
    }

    @Test
    fun getId() {
        val timeInterval = timeInterval(android) {
            start = Milliseconds(1)
        }
        val item = TimeReportItem.with(timeInterval)

        assertEquals(timeInterval.id, item.id)
    }
}
