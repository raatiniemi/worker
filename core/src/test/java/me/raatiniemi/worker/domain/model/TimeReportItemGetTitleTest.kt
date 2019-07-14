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
import org.junit.runners.Parameterized.Parameters
import java.util.*

@RunWith(Parameterized::class)
class TimeReportItemGetTitleTest(
    private val message: String,
    private val expected: String,
    private val timeInterval: TimeInterval
) {
    @Test
    fun getTitle() {
        val item = TimeReportItem.with(timeInterval)

        assertEquals(message, expected, item.title)
    }

    companion object {
        private val START = GregorianCalendar(2016, 1, 28, 8, 0)
        private val STOP = GregorianCalendar(2016, 1, 28, 11, 30)

        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                arrayOf(
                    "active time interval",
                    "08:00",
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = Milliseconds(START.timeInMillis)
                    }
                ),
                arrayOf(
                    "inactive time interval",
                    "08:00 - 11:30",
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = Milliseconds(START.timeInMillis)
                        builder.stop = Milliseconds(STOP.timeInMillis)
                    }
                )
            )
    }
}
