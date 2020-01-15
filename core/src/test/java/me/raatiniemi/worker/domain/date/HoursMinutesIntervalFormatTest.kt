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

package me.raatiniemi.worker.domain.date

import me.raatiniemi.worker.domain.time.Milliseconds
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class HoursMinutesIntervalFormatTest(
    private val expected: String,
    private val intervalInMilliseconds: Long
) {

    @Test
    fun format() {
        val intervalFormat = HoursMinutesIntervalFormat()
        val interval = Milliseconds(intervalInMilliseconds)

        val actual = intervalFormat.format(interval)

        assertEquals(expected, actual)
    }

    companion object {
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                arrayOf(
                    "1m",
                    60_000
                ),
                arrayOf(
                    "10m",
                    600_000
                ),
                arrayOf(
                    "30m",
                    1_800_000
                ),
                arrayOf(
                    "1h 0m",
                    3_600_000
                ),
                arrayOf(
                    "7h 30m",
                    27_000_000
                ),
                arrayOf(
                    "30h 0m",
                    108_000_000
                ),
                arrayOf(
                    "56h 25m",
                    203_100_000
                ),
                arrayOf(
                    "1h 0m",
                    3_580_000
                )
            )
    }
}
