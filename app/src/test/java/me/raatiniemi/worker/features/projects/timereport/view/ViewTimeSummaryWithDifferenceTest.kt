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

package me.raatiniemi.worker.features.projects.timereport.view

import me.raatiniemi.worker.domain.date.DigitalHoursMinutesIntervalFormat
import me.raatiniemi.worker.domain.date.HoursMinutesFormat
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class ViewTimeSummaryWithDifferenceTest(
    private val expected: String,
    private val formatter: HoursMinutesFormat,
    private val timeIntervals: List<TimeInterval>
) {
    @Test
    fun timeSummaryWithDifference() {
        val day = timeReportDay(Milliseconds.now, timeIntervals)

        assertEquals(expected, timeSummaryWithDifference(day, formatter))
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
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(1)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(3600000)
                        }
                    )
                ),
                arrayOf(
                    "8:00",
                    DigitalHoursMinutesIntervalFormat(),
                    listOf(
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(1)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(28800000)
                        }
                    )
                ),
                arrayOf(
                    "9:00 (+1:00)",
                    DigitalHoursMinutesIntervalFormat(),
                    listOf(
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(1)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(32400000)
                        }
                    )
                ),
                arrayOf(
                    "9:07 (+1:07)",
                    DigitalHoursMinutesIntervalFormat(),
                    listOf(
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(1)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(14380327)
                        },
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(2)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(18407820)
                        }
                    )
                ),
                arrayOf(
                    "8:46 (+0:46)",
                    DigitalHoursMinutesIntervalFormat(),
                    listOf(
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(1)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(13956031)
                        },
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(2)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(17594386)
                        }
                    )
                ),
                arrayOf(
                    "7:52 (-0:08)",
                    DigitalHoursMinutesIntervalFormat(),
                    listOf(
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(1)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(11661632)
                        },
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(2)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(16707601)
                        }
                    )
                )
            )
    }
}
