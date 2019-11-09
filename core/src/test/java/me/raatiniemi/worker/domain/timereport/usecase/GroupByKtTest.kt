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

package me.raatiniemi.worker.domain.timereport.usecase

import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.*
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import me.raatiniemi.worker.domain.timereport.model.TimeReportDay
import me.raatiniemi.worker.domain.timereport.model.TimeReportWeek
import me.raatiniemi.worker.domain.timereport.model.timeReportDay
import me.raatiniemi.worker.domain.timereport.model.timeReportWeek
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class GroupByKtTest {
    // Group by week

    @Test
    fun `group by week without time intervals`() {
        val timeIntervals = emptyList<TimeInterval>()
        val expected = emptyList<TimeReportWeek>()

        val actual = groupByWeek(timeIntervals)

        assertEquals(expected, actual)
    }

    @Test
    fun `group by week with time interval`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = startOfDay
                builder.stop = startOfDay + 10.minutes
            }
        )
        val expected = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        Date(startOfDay.value),
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfDay
                                builder.stop = startOfDay + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = groupByWeek(timeIntervals)

        assertEquals(expected, actual)
    }

    @Test
    fun `group by week with time intervals`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = startOfDay
                builder.stop = startOfDay + 10.minutes
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = startOfDay + 20.minutes
                builder.stop = startOfDay + 30.minutes
            }
        )
        val expected = listOf(
            timeReportWeek(
                startOfDay,
                listOf(
                    timeReportDay(
                        Date(startOfDay.value),
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = startOfDay + 20.minutes
                                builder.stop = startOfDay + 30.minutes
                            },
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfDay
                                builder.stop = startOfDay + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = groupByWeek(timeIntervals)

        assertEquals(expected, actual)
    }

    @Test
    fun `group by week with time intervals within same week`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val endOfWeek = setToEndOfWeek(startOfWeek)
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = startOfWeek
                builder.stop = startOfWeek + 10.minutes
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = endOfWeek
                builder.stop = endOfWeek + 10.minutes
            }
        )
        val expected = listOf(
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        Date(endOfWeek.value),
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = endOfWeek
                                builder.stop = endOfWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        Date(startOfWeek.value),
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = groupByWeek(timeIntervals)

        assertEquals(expected, actual)
    }

    @Test
    fun `group by week with time intervals in different weeks`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val nextWeek = startOfWeek + 1.weeks
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = startOfWeek
                builder.stop = startOfWeek + 10.minutes
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = nextWeek
                builder.stop = nextWeek + 10.minutes
            }
        )
        val expected = listOf(
            timeReportWeek(
                nextWeek,
                listOf(
                    timeReportDay(
                        Date(setToStartOfDay(nextWeek).value),
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = nextWeek
                                builder.stop = nextWeek + 10.minutes
                            }
                        )
                    )
                )
            ),
            timeReportWeek(
                startOfWeek,
                listOf(
                    timeReportDay(
                        Date(startOfWeek.value),
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = startOfWeek
                                builder.stop = startOfWeek + 10.minutes
                            }
                        )
                    )
                )
            )
        )

        val actual = groupByWeek(timeIntervals)

        assertEquals(expected, actual)
    }

    // Group by day

    @Test
    fun `group by day without time intervals`() {
        val timeIntervals = emptyList<TimeInterval>()
        val expected = emptyList<TimeReportDay>()

        val actual = groupByDay(timeIntervals)

        assertEquals(expected, actual)
    }

    @Test
    fun `group by day with time interval`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = startOfDay
                builder.stop = startOfDay + 10.minutes
            }
        )
        val expected = listOf(
            timeReportDay(
                Date(startOfDay.value),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = startOfDay
                        builder.stop = startOfDay + 10.minutes
                    }
                )
            )
        )

        val actual = groupByDay(timeIntervals)

        assertEquals(expected, actual)
    }

    @Test
    fun `group by day with time intervals`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = startOfDay
                builder.stop = startOfDay + 10.minutes
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = startOfDay + 20.minutes
                builder.stop = startOfDay + 30.minutes
            }
        )
        val expected = listOf(
            timeReportDay(
                Date(startOfDay.value),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = startOfDay + 20.minutes
                        builder.stop = startOfDay + 30.minutes
                    },
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = startOfDay
                        builder.stop = startOfDay + 10.minutes
                    }
                )
            )
        )

        val actual = groupByDay(timeIntervals)

        assertEquals(expected, actual)
    }

    @Test
    fun `group by day with time intervals within same week`() {
        val startOfDay = setToStartOfDay(Milliseconds.now)
        val startOfWeek = setToStartOfWeek(startOfDay)
        val endOfWeek = setToEndOfWeek(startOfWeek)
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = startOfWeek
                builder.stop = startOfWeek + 10.minutes
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = endOfWeek
                builder.stop = endOfWeek + 10.minutes
            }
        )
        val expected = listOf(
            timeReportDay(
                Date(endOfWeek.value),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = endOfWeek
                        builder.stop = endOfWeek + 10.minutes
                    }
                )
            ),
            timeReportDay(
                Date(startOfWeek.value),
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = startOfWeek
                        builder.stop = startOfWeek + 10.minutes
                    }
                )
            )
        )

        val actual = groupByDay(timeIntervals)

        assertEquals(expected, actual)
    }
}
