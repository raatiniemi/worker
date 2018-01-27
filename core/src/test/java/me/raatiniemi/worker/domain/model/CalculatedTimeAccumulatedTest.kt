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
import me.raatiniemi.worker.domain.model.CalculatedTime
import me.raatiniemi.worker.domain.model.accumulated
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class CalculatedTimeAccumulatedTest(
        private val message: String,
        private val expected: CalculatedTime,
        private val values: List<CalculatedTime>
) {
    companion object {
        @JvmStatic
        @Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(
                            "With empty list of items",
                            CalculatedTime.empty,
                            listOf<CalculatedTime>()
                    ),
                    arrayOf(
                            "With single item",
                            CalculatedTime(hours = 1, minutes = 0),
                            listOf(CalculatedTime(hours = 1, minutes = 0))
                    ),
                    arrayOf(
                            "With multiple items",
                            CalculatedTime(hours = 2, minutes = 0),
                            listOf(
                                    CalculatedTime(hours = 1, minutes = 0),
                                    CalculatedTime(hours = 1, minutes = 0)
                            )
                    ),
                    arrayOf(
                            "With overflowing minutes",
                            CalculatedTime(hours = 3, minutes = 15),
                            listOf(
                                    CalculatedTime(hours = 1, minutes = 30),
                                    CalculatedTime(hours = 1, minutes = 45)
                            )
                    )
            )
        }
    }

    @Test
    fun sum() {
        val actual = values.accumulated()

        assertEquals(message, expected, actual)
    }
}
