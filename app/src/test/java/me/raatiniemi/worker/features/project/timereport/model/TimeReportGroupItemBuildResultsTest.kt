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

package me.raatiniemi.worker.features.project.timereport.model

import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportGroup
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.model.timeInterval
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.*

@RunWith(Parameterized::class)
class TimeReportGroupItemBuildResultsTest(
        private val message: String,
        private val expected: List<TimeReportAdapterResult>,
        private val groupIndex: Int,
        private val groupItem: TimeReportGroup
) {
    @Test
    fun buildItemResultsWithGroupIndex() {
        val actual = groupItem.buildItemResultsWithGroupIndex(groupIndex)

        assertEquals(message, expected, actual)
    }

    companion object {
        private fun buildTimesheetGroupWithNumberOfItems(numberOfItems: Int): TimeReportGroup {
            if (0 == numberOfItems) {
                return TimeReportGroup.build(Date(), sortedSetOf())
            }

            val items = TreeSet<TimeReportItem>()
            for (i in 0 until numberOfItems) {
                val timeInterval = TimeInterval.builder(1L)
                        .startInMilliseconds(i.toLong())
                        .build()

                items.add(TimeReportItem.with(timeInterval))
            }

            return TimeReportGroup.build(Date(), items)
        }

        @Suppress("unused")
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                    arrayOf(
                            "Without items",
                            emptyList<TimeReportAdapterResult>(),
                            0,
                            buildTimesheetGroupWithNumberOfItems(0)
                    ),
                    arrayOf(
                            "With one item",
                            listOf(
                                    TimeReportAdapterResult(
                                            1,
                                            0,
                                            TimeReportItem.with(timeInterval { })
                                    )
                            ),
                            1,
                            buildTimesheetGroupWithNumberOfItems(1)
                    ),
                    arrayOf(
                            "With multiple items",
                            listOf(
                                    TimeReportAdapterResult(
                                            2,
                                            0,
                                            TimeReportItem.with(timeInterval {
                                                startInMilliseconds = 5
                                            })
                                    ),
                                    TimeReportAdapterResult(
                                            2,
                                            1,
                                            TimeReportItem.with(timeInterval {
                                                startInMilliseconds = 4
                                            })
                                    ),
                                    TimeReportAdapterResult(
                                            2,
                                            2,
                                            TimeReportItem.with(timeInterval {
                                                startInMilliseconds = 3
                                            })
                                    ),
                                    TimeReportAdapterResult(
                                            2,
                                            3,
                                            TimeReportItem.with(timeInterval {
                                                startInMilliseconds = 2
                                            })
                                    ),
                                    TimeReportAdapterResult(
                                            2,
                                            4,
                                            TimeReportItem.with(timeInterval {
                                                startInMilliseconds = 1
                                            })
                                    ),
                                    TimeReportAdapterResult(
                                            2,
                                            5,
                                            TimeReportItem.with(timeInterval {
                                                startInMilliseconds = 0
                                            })
                                    )
                            ),
                            2,
                            buildTimesheetGroupWithNumberOfItems(6)
                    )
            )
    }
}
