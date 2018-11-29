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
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class HoursMinutesPlusTest(
        private val expected: HoursMinutes,
        private val lhs: HoursMinutes,
        private val rhs: HoursMinutes
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf<Array<Any>>(
                    arrayOf(
                            HoursMinutes(hours = 1, minutes = 0),
                            HoursMinutes(hours = 1, minutes = 0),
                            HoursMinutes.empty
                    ),
                    arrayOf(
                            HoursMinutes(hours = 1, minutes = 0),
                            HoursMinutes.empty,
                            HoursMinutes(hours = 1, minutes = 0)
                    ),
                    arrayOf(
                            HoursMinutes(hours = 1, minutes = 45),
                            HoursMinutes(hours = 1, minutes = 0),
                            HoursMinutes(hours = 0, minutes = 45)
                    ),
                    arrayOf(
                            HoursMinutes(hours = 3, minutes = 30),
                            HoursMinutes(hours = 1, minutes = 45),
                            HoursMinutes(hours = 1, minutes = 45)
                    ),
                    arrayOf(
                            HoursMinutes(hours = 4, minutes = 0),
                            HoursMinutes(hours = 2, minutes = 45),
                            HoursMinutes(hours = 1, minutes = 15)
                    ),
                    arrayOf(
                            HoursMinutes(hours = 0, minutes = 45),
                            HoursMinutes(hours = -1, minutes = 0),
                            HoursMinutes(hours = 1, minutes = 45)
                    ),
                    arrayOf(
                            HoursMinutes(hours = -2, minutes = 45),
                            HoursMinutes(hours = -1, minutes = 0),
                            HoursMinutes(hours = -1, minutes = 45)
                    )
            )
        }
    }

    @Test
    fun plus() {
        val actual = lhs + rhs

        assertEquals(expected, actual)
    }
}
