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

package me.raatiniemi.worker.domain.comparator

import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.model.timeInterval
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class TimeReportItemComparatorTest(
    private val message: String,
    private val expected: Int,
    private val lhs: TimeReportItem,
    private val rhs: TimeReportItem
) {
    @Test
    fun compareTo() {
        assertEquals(message, expected, lhs.compareTo(rhs))
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf<Array<Any>>(
                arrayOf(
                    "Active with lhs.start = rhs.start",
                    0,
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 0
                        }
                    ),
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 0
                        }
                    )
                ),
                arrayOf(
                    "Active with lhs.start > rhs.start",
                    -1,
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 2
                            stopInMilliseconds = 0
                        }
                    ),
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 0
                        }
                    )
                ),
                arrayOf(
                    "Active with lhs.start < rhs.start",
                    1,
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 0
                        }
                    ),
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 2
                            stopInMilliseconds = 0
                        }
                    )
                ),
                arrayOf(
                    "lhs == rhs",
                    0,
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 2
                        }
                    ),
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 2
                        }
                    )
                ),
                arrayOf(
                    "lhs.start > rhs.start",
                    -1,
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 2
                            stopInMilliseconds = 2
                        }
                    ),
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 2
                        }
                    )
                ),
                arrayOf(
                    "lhs.start < rhs.start",
                    1,
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 2
                        }
                    ),
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 2
                            stopInMilliseconds = 2
                        }
                    )
                ),
                arrayOf(
                    "lhs.stop > rhs.stop",
                    -1,
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 2
                        }
                    ),
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 1
                        }
                    )
                ),
                arrayOf(
                    "lhs.stop < rhs.stop",
                    1,
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 1
                        }
                    ),
                    TimeReportItem.with(
                        timeInterval {
                            startInMilliseconds = 1
                            stopInMilliseconds = 2
                        }
                    )
                )
            )
    }
}
