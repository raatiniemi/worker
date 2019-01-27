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
import me.raatiniemi.worker.domain.model.TimeReportItem
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class TimeReportAdapterResultComparableTest(
        private val message: String,
        private val expected: Int,
        private val lhs: TimeReportAdapterResult,
        private val rhs: TimeReportAdapterResult
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
            get() {
                val timeInterval = TimeInterval.builder(1L).build()
                val item = TimeReportItem.with(timeInterval)

                return listOf<Array<Any>>(
                        arrayOf(
                                "Equal",
                                0,
                                TimeReportAdapterResult(0, 0, item),
                                TimeReportAdapterResult(0, 0, item)
                        ),
                        arrayOf(
                                "lhs is more than rhs (group)",
                                1,
                                TimeReportAdapterResult(1, 0, item),
                                TimeReportAdapterResult(0, 0, item)),
                        arrayOf(
                                "lhs is less than rhs (group)",
                                -1,
                                TimeReportAdapterResult(0, 0, item),
                                TimeReportAdapterResult(1, 0, item)
                        ),
                        arrayOf(
                                "lhs is more than rhs (child)",
                                1,
                                TimeReportAdapterResult(0, 1, item),
                                TimeReportAdapterResult(0, 0, item)
                        ),
                        arrayOf(
                                "lhs is less than rhs (child)",
                                -1,
                                TimeReportAdapterResult(0, 0, item),
                                TimeReportAdapterResult(0, 1, item)
                        )
                )
            }
    }
}
