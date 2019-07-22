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

@RunWith(Parameterized::class)
class TimeReportTimeIntervalComparatorTest(
    private val message: String,
    private val expected: Int,
    private val lhs: TimeInterval,
    private val rhs: TimeInterval
) {
    private val comparator = TimeReportTimeIntervalComparator()

    @Test
    fun compareTo() {
        assertEquals(message, expected, comparator.compare(lhs, rhs))
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                arrayOf(
                    "Active with lhs.start = rhs.start",
                    0,
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = Milliseconds(1)
                        builder.stop = null
                    },
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = Milliseconds(1)
                        builder.stop = null
                    }
                ),
                arrayOf(
                    "Active with lhs.start > rhs.start",
                    -1,
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = Milliseconds(2)
                        builder.stop = null
                    },
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = Milliseconds(1)
                        builder.stop = null
                    }
                ),
                arrayOf(
                    "Active with lhs.start < rhs.start",
                    1,
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = Milliseconds(1)
                        builder.stop = null
                    },
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = Milliseconds(2)
                        builder.stop = null
                    }
                ),
                arrayOf(
                    "lhs == rhs",
                    0,
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = Milliseconds(1)
                        builder.stop = Milliseconds(2)
                    },
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = Milliseconds(1)
                        builder.stop = Milliseconds(2)
                    }
                ),
                arrayOf(
                    "lhs.start > rhs.start",
                    -1,
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = Milliseconds(2)
                        builder.stop = Milliseconds(2)
                    },
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = Milliseconds(1)
                        builder.stop = Milliseconds(2)
                    }
                ),
                arrayOf(
                    "lhs.start < rhs.start",
                    1,
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = Milliseconds(1)
                        builder.stop = Milliseconds(2)
                    },
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = Milliseconds(2)
                        builder.stop = Milliseconds(2)
                    }
                ),
                arrayOf(
                    "lhs.stop > rhs.stop",
                    -1,
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = Milliseconds(1)
                        builder.stop = Milliseconds(2)
                    },
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = Milliseconds(1)
                        builder.stop = Milliseconds(1)
                    }
                ),
                arrayOf(
                    "lhs.stop < rhs.stop",
                    1,
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = Milliseconds(1)
                        builder.stop = Milliseconds(1)
                    },
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = Milliseconds(1)
                        builder.stop = Milliseconds(2)
                    }
                )
            )
    }
}
