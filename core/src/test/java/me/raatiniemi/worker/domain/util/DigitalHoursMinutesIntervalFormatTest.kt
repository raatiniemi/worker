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

package me.raatiniemi.worker.domain.util

import me.raatiniemi.worker.domain.model.HoursMinutes
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class DigitalHoursMinutesIntervalFormatTest(
        private val expected: String,
        private val hoursMinutes: HoursMinutes
) {
    private val intervalFormat = DigitalHoursMinutesIntervalFormat()

    @Test
    fun format() {
        assertEquals(expected, intervalFormat.apply(hoursMinutes))
    }

    companion object {
        @JvmStatic
        @Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf("0:01", HoursMinutes(hours = 0, minutes = 1)),
                    arrayOf("0:10", HoursMinutes(hours = 0, minutes = 10)),
                    arrayOf("0:30", HoursMinutes(hours = 0, minutes = 30)),
                    arrayOf("1:00", HoursMinutes(hours = 1, minutes = 0)),
                    arrayOf("7:30", HoursMinutes(hours = 7, minutes = 30)),
                    arrayOf("30:00", HoursMinutes(hours = 30, minutes = 0)),
                    arrayOf("56:25", HoursMinutes(hours = 56, minutes = 25)),
                    arrayOf("-0:30", HoursMinutes(hours = 0, minutes = -30)),
                    arrayOf("-2:00", HoursMinutes(hours = -2, minutes = 0)),
                    arrayOf("-2:30", HoursMinutes(hours = -2, minutes = -30))
            )
        }
    }
}
