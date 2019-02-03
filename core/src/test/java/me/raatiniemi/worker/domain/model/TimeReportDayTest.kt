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

package me.raatiniemi.worker.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.*

@RunWith(Parameterized::class)
class TimeReportDayTest(
        private val expectedTimeSummary: HoursMinutes,
        private val expectedTimeDifference: HoursMinutes,
        private val timeIntervals: List<TimeInterval>
) {
    @Test
    fun getTimeSummaryWithDifference() {
        val day = TimeReportDay(
                Date(),
                timeIntervals.map { TimeReportItem.with(it) }
        )

        assertEquals(expectedTimeSummary, day.timeSummary)
        assertEquals(expectedTimeDifference, day.timeDifference)
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                    arrayOf(
                            HoursMinutes(1, 0),
                            HoursMinutes(-7, 0),
                            listOf(
                                    timeInterval {
                                        startInMilliseconds = 1
                                        stopInMilliseconds = 3600000
                                    }
                            )
                    ),
                    arrayOf(
                            HoursMinutes(8, 0),
                            HoursMinutes.empty,
                            listOf(
                                    timeInterval {
                                        startInMilliseconds = 1
                                        stopInMilliseconds = 28800000
                                    }
                            )
                    ),
                    arrayOf(
                            HoursMinutes(9, 0),
                            HoursMinutes(1, 0),
                            listOf(
                                    timeInterval {
                                        startInMilliseconds = 1
                                        stopInMilliseconds = 32400000
                                    }
                            )
                    ),
                    arrayOf(
                            HoursMinutes(9, 7),
                            HoursMinutes(1, 7),
                            listOf(
                                    timeInterval {
                                        startInMilliseconds = 1
                                        stopInMilliseconds = 14380327
                                    },
                                    timeInterval {
                                        startInMilliseconds = 1
                                        stopInMilliseconds = 18407820
                                    }
                            )
                    ),
                    arrayOf(
                            HoursMinutes(8, 46),
                            HoursMinutes(0, 46),
                            listOf(
                                    timeInterval {
                                        startInMilliseconds = 1
                                        stopInMilliseconds = 13956031
                                    },
                                    timeInterval {
                                        startInMilliseconds = 1
                                        stopInMilliseconds = 17594386
                                    }
                            )
                    ),
                    arrayOf(
                            HoursMinutes(7, 52),
                            HoursMinutes(0, -8),
                            listOf(
                                    timeInterval {
                                        startInMilliseconds = 1
                                        stopInMilliseconds = 11661632
                                    },
                                    timeInterval {
                                        startInMilliseconds = 1
                                        stopInMilliseconds = 16707601
                                    }
                            )
                    )
            )

    }
}
