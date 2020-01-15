/*
 * Copyright (C) 2020 Tobias Raatiniemi
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
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class CalculateTimeTest(private val expected: HoursMinutes, private val milliseconds: Long) {
    @Test
    fun `calculate hours minutes`() {
        val actual = calculateHoursMinutes(Milliseconds(milliseconds))

        assertEquals("$expected", expected, actual)
    }

    companion object {
        @JvmStatic
        val data: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                arrayOf(
                    HoursMinutes(0, 1),
                    60_000
                ),
                arrayOf(
                    HoursMinutes(0, 10),
                    600_000
                ),
                arrayOf(
                    HoursMinutes(0, 15),
                    900_000
                ),
                arrayOf(
                    HoursMinutes(0, 30),
                    1_800_000
                ),
                arrayOf(
                    HoursMinutes(1, 0),
                    3_580_000
                ),
                arrayOf(
                    HoursMinutes(1, 0),
                    3_600_000
                ),
                arrayOf(
                    HoursMinutes(1, 15),
                    4_500_000
                ),
                arrayOf(
                    HoursMinutes(2, 0),
                    7_175_000
                ),
                arrayOf(
                    HoursMinutes(7, 30),
                    27_000_000
                ),
                arrayOf(
                    HoursMinutes(30, 0),
                    108_000_000
                ),
                arrayOf(
                    HoursMinutes(56, 25),
                    203_100_000
                )
            )
    }
}
