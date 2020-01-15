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

package me.raatiniemi.worker.domain.timereport.model

import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.HoursMinutes
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class TimeReportDayTimeDifferenceTest(
    private val expectedTimeDifference: HoursMinutes,
    private val timeIntervals: List<TimeInterval>
) {
    @Test
    fun getTimeSummaryWithDifference() {
        val day = timeReportDay(Milliseconds.now, timeIntervals)

        assertEquals(expectedTimeDifference, day.timeDifference)
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                arrayOf(
                    HoursMinutes(-7, 0),
                    listOf(
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(1)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(3600000)
                        }
                    )
                ),
                arrayOf(
                    HoursMinutes.empty,
                    listOf(
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(1)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(28800000)
                        }
                    )
                ),
                arrayOf(
                    HoursMinutes(1, 0),
                    listOf(
                        timeInterval(android.id) { builder ->
                            builder.id = TimeIntervalId(1)
                            builder.start = Milliseconds(1)
                            builder.stop = Milliseconds(32400000)
                        }
                    )
                ),
                arrayOf(
                    HoursMinutes(1, 7),
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
                    HoursMinutes(0, 46),
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
                    HoursMinutes(0, -8),
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
