/*
 * Copyright (C) 2017 Worker Project
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

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class CalculatedTimeMinusTest(
        private val expected: CalculatedTime,
        private val lhs: CalculatedTime,
        private val rhs: CalculatedTime
) {
    companion object {
        @JvmStatic
        @Parameters
        fun data(): Collection<Array<Any>> {
            return listOf<Array<Any>>(
                    arrayOf(
                            CalculatedTime(hours = 1, minutes = 0),
                            CalculatedTime(hours = 1, minutes = 0),
                            CalculatedTime.empty
                    ),
                    arrayOf(
                            CalculatedTime(hours = -1, minutes = 0),
                            CalculatedTime.empty,
                            CalculatedTime(hours = 1, minutes = 0)
                    ),
                    arrayOf(
                            CalculatedTime(hours = 1, minutes = 0),
                            CalculatedTime(hours = 2, minutes = 0),
                            CalculatedTime(hours = 1, minutes = 0)
                    ),
                    arrayOf(
                            CalculatedTime(hours = 1, minutes = 15),
                            CalculatedTime(hours = 3, minutes = 0),
                            CalculatedTime(hours = 1, minutes = 45)
                    ),
                    arrayOf(
                            CalculatedTime(hours = -4, minutes = -45),
                            CalculatedTime(hours = -3, minutes = 0),
                            CalculatedTime(hours = 1, minutes = 45)
                    ),
                    arrayOf(
                            CalculatedTime(hours = -2, minutes = 0),
                            CalculatedTime(hours = -3, minutes = 0),
                            CalculatedTime(hours = -1, minutes = 0)
                    ),
                    arrayOf(
                            CalculatedTime(hours = -1, minutes = -15),
                            CalculatedTime(hours = -3, minutes = 0),
                            CalculatedTime(hours = -1, minutes = -45)
                    )
            )
        }
    }

    @Test
    fun minus() {
        val actual = lhs - rhs

        assertEquals(expected, actual)
    }
}
