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

package me.raatiniemi.worker.domain.time

import me.raatiniemi.worker.domain.date.plus
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class SetToPointInTimeKtTest {
    @Test
    fun `set to start of day`() {
        val now = Milliseconds.now
        val startOfDay = setToStartOfDay(now).let { milliseconds ->
            Calendar.getInstance()
                .apply { time = Date(milliseconds.value) }
        }
        val endOfDay = setToStartOfDay(now).let { milliseconds ->
            Calendar.getInstance()
                .apply { time = Date(milliseconds.value) + 23.hours + 59.minutes + 59.seconds }
        }

        val expected = startOfDay.get(Calendar.DAY_OF_WEEK)
        val actual = endOfDay.get(Calendar.DAY_OF_WEEK)

        assertEquals(expected, actual)
    }

    @Test
    fun `set to start and end of week`() {
        val now = Milliseconds.now
        val startOfWeek = setToStartOfWeek(now).let { milliseconds ->
            Calendar.getInstance()
                .apply { time = Date(milliseconds.value) }
        }
        val endOfWeek = setToEndOfWeek(now).let { milliseconds ->
            Calendar.getInstance()
                .apply { time = Date(milliseconds.value) }
        }

        val expected = startOfWeek.get(Calendar.WEEK_OF_YEAR)
        val actual = endOfWeek.get(Calendar.WEEK_OF_YEAR)

        assertEquals(expected, actual)
    }
}
