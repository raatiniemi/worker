/*
 * Copyright (C) 2022 Tobias Raatiniemi
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
                        startOfDay,
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
                        startOfDay,
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
                        endOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = endOfWeek
                                builder.stop = endOfWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        startOfWeek,
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
    fun `group by week with time intervals using fixed values within same week`() {
        val startOfWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val endOfWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
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
                        endOfWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = endOfWeek
                                builder.stop = endOfWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        startOfWeek,
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
                        setToStartOfDay(nextWeek),
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
                        startOfWeek,
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
    fun `group by week with time intervals during three weeks over new year`() {
        val endOfFirstWeek = Milliseconds(1577606247000) // 2019-12-29 07:57:27
        val firstInSecondWeek = Milliseconds(1577690413000) // 2019-12-30 07:20:13
        val secondInSecondWeek = Milliseconds(1577779099000) // 2019-12-31 07:58:19
        val thirdInSecondWeek = Milliseconds(1577985643000) // 2020-01-02 17:20:43
        val fourthInSecondWeek = Milliseconds(1578211149000) // 2020-01-05 07:59:09
        val startOfThirdWeek = Milliseconds(1578297584000) // 2020-01-06 07:59:44
        val timeIntervals = listOf(
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(1)
                builder.start = endOfFirstWeek
                builder.stop = endOfFirstWeek + 10.minutes
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(2)
                builder.start = firstInSecondWeek
                builder.stop = firstInSecondWeek + 10.minutes
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(3)
                builder.start = secondInSecondWeek
                builder.stop = secondInSecondWeek + 10.minutes
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(4)
                builder.start = thirdInSecondWeek
                builder.stop = thirdInSecondWeek + 10.minutes
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(5)
                builder.start = fourthInSecondWeek
                builder.stop = fourthInSecondWeek + 10.minutes
            },
            timeInterval(android.id) { builder ->
                builder.id = TimeIntervalId(6)
                builder.start = startOfThirdWeek
                builder.stop = startOfThirdWeek + 10.minutes
            }
        )
        val expected = listOf(
            timeReportWeek(
                startOfThirdWeek,
                listOf(
                    timeReportDay(
                        startOfThirdWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(6)
                                builder.start = startOfThirdWeek
                                builder.stop = startOfThirdWeek + 10.minutes
                            }
                        )
                    )
                )
            ),
            timeReportWeek(
                firstInSecondWeek,
                listOf(
                    timeReportDay(
                        fourthInSecondWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(5)
                                builder.start = fourthInSecondWeek
                                builder.stop = fourthInSecondWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        thirdInSecondWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(4)
                                builder.start = thirdInSecondWeek
                                builder.stop = thirdInSecondWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        secondInSecondWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(3)
                                builder.start = secondInSecondWeek
                                builder.stop = secondInSecondWeek + 10.minutes
                            }
                        )
                    ),
                    timeReportDay(
                        firstInSecondWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(2)
                                builder.start = firstInSecondWeek
                                builder.stop = firstInSecondWeek + 10.minutes
                            }
                        )
                    )
                )
            ),
            timeReportWeek(
                endOfFirstWeek,
                listOf(
                    timeReportDay(
                        endOfFirstWeek,
                        listOf(
                            timeInterval(android.id) { builder ->
                                builder.id = TimeIntervalId(1)
                                builder.start = endOfFirstWeek
                                builder.stop = endOfFirstWeek + 10.minutes
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
                startOfDay,
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
                startOfDay,
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
                endOfWeek,
                listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = endOfWeek
                        builder.stop = endOfWeek + 10.minutes
                    }
                )
            ),
            timeReportDay(
                startOfWeek,
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
