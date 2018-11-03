/*
 * Copyright (C) 2017 Worker Project
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

import me.raatiniemi.worker.domain.util.DigitalHoursMinutesIntervalFormat
import me.raatiniemi.worker.domain.util.FractionIntervalFormat
import me.raatiniemi.worker.domain.util.HoursMinutesFormat
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class TimesheetItemGetTimeSummaryWithFormatterTest(
        private val expected: String,
        private val formatter: HoursMinutesFormat,
        private val timeInterval: TimeInterval
) {
    @Test
    fun getTimeSummary() {
        val item = TimesheetItem.with(timeInterval)

        assertEquals(expected, item.getTimeSummaryWithFormatter(formatter))
    }

    companion object {
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                    arrayOf(
                            "1.00",
                            FractionIntervalFormat(),
                            TimeInterval.builder(1L)
                                    .stopInMilliseconds(3600000)
                                    .build()
                    ),
                    arrayOf(
                            "9.00",
                            FractionIntervalFormat(),
                            TimeInterval.builder(1L)
                                    .stopInMilliseconds(32400000)
                                    .build()
                    ),
                    arrayOf(
                            "1:00",
                            DigitalHoursMinutesIntervalFormat(),
                            TimeInterval.builder(1L)
                                    .stopInMilliseconds(3600000)
                                    .build()
                    ),
                    arrayOf(
                            "9:00",
                            DigitalHoursMinutesIntervalFormat(),
                            TimeInterval.builder(1L)
                                    .stopInMilliseconds(32400000)
                                    .build()
                    )
            )
    }
}
