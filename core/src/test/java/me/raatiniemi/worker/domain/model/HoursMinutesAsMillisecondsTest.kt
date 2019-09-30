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

import me.raatiniemi.worker.domain.time.HoursMinutes
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class HoursMinutesAsMillisecondsTest(
    private val expected: Long,
    private val hoursMinutes: HoursMinutes
) {
    @Test
    fun asMilliseconds() {
        assertEquals(expected, hoursMinutes.asMilliseconds())
    }

    companion object {
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                arrayOf(60000L, HoursMinutes(0, 1)),
                arrayOf(600000L, HoursMinutes(0, 10)),
                arrayOf(900000L, HoursMinutes(0, 15)),
                arrayOf(1800000L, HoursMinutes(0, 30)),
                arrayOf(3600000L, HoursMinutes(1, 0)),
                arrayOf(4500000L, HoursMinutes(1, 15)),
                arrayOf(7200000L, HoursMinutes(2, 0)),
                arrayOf(27000000L, HoursMinutes(7, 30)),
                arrayOf(108000000L, HoursMinutes(30, 0)),
                arrayOf(203100000L, HoursMinutes(56, 25))
            )
    }
}
