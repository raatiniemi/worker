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

package me.raatiniemi.worker.features.projects.timereport.model

import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.util.DigitalHoursMinutesIntervalFormat
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.*

@RunWith(Parameterized::class)
class TimeReportGroupGetTimeSummaryWithDifferenceTest(
    private val expected: String,
    private val formatter: HoursMinutesFormat,
    private val timeIntervals: List<TimeInterval>
) {
    @Test
    fun getTimeSummaryWithDifference() {
        val day = TimeReportDay(
            Date(),
            timeIntervals.map { TimeReportItem.with(it) }
        )

        assertEquals(expected, day.getTimeSummaryWithDifference(formatter))
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                arrayOf(
                    "1:00 (-7:00)",
                    DigitalHoursMinutesIntervalFormat(),
                    listOf(
                        timeInterval(android) {
                            start = Milliseconds(1)
                            stop = Milliseconds(3600000)
                        }
                    )
                ),
                arrayOf(
                    "8:00",
                    DigitalHoursMinutesIntervalFormat(),
                    listOf(
                        timeInterval(android) {
                            start = Milliseconds(1)
                            stop = Milliseconds(28800000)
                        }
                    )
                ),
                arrayOf(
                    "9:00 (+1:00)",
                    DigitalHoursMinutesIntervalFormat(),
                    listOf(
                        timeInterval(android) {
                            start = Milliseconds(1)
                            stop = Milliseconds(32400000)
                        }
                    )
                ),
                arrayOf(
                    "9:07 (+1:07)",
                    DigitalHoursMinutesIntervalFormat(),
                    listOf(
                        timeInterval(android) {
                            start = Milliseconds(1)
                            stop = Milliseconds(14380327)
                        },
                        timeInterval(android) {
                            start = Milliseconds(1)
                            stop = Milliseconds(18407820)
                        }
                    )
                ),
                arrayOf(
                    "8:46 (+0:46)",
                    DigitalHoursMinutesIntervalFormat(),
                    listOf(
                        timeInterval(android) {
                            start = Milliseconds(1)
                            stop = Milliseconds(13956031)
                        },
                        timeInterval(android) {
                            start = Milliseconds(1)
                            stop = Milliseconds(17594386)
                        }
                    )
                ),
                arrayOf(
                    "7:52 (-0:08)",
                    DigitalHoursMinutesIntervalFormat(),
                    listOf(
                        timeInterval(android) {
                            start = Milliseconds(1)
                            stop = Milliseconds(11661632)
                        },
                        timeInterval(android) {
                            start = Milliseconds(1)
                            stop = Milliseconds(16707601)
                        }
                    )
                )
            )
    }
}
