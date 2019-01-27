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

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.*

@RunWith(Parameterized::class)
class TimeReportGroupGetFirstLetterFromTitleTest(
        private val expected: String,
        private val calendar: Calendar
) {
    @Test
    fun getFirstLetterFromTitle() {
        val item: TimeReportGroup = TimeReportGroup.build(calendar.time, sortedSetOf())

        assertEquals(expected, item.firstLetterFromTitle)
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                    arrayOf(
                            "F",
                            GregorianCalendar(2016, 6, 1)
                    ),
                    arrayOf(
                            "S",
                            GregorianCalendar(2016, 6, 2)
                    ),
                    arrayOf(
                            "S",
                            GregorianCalendar(2016, 6, 3)
                    ),
                    arrayOf(
                            "M",
                            GregorianCalendar(2016, 6, 4)
                    ),
                    arrayOf(
                            "T",
                            GregorianCalendar(2016, 6, 5)
                    ),
                    arrayOf(
                            "W",
                            GregorianCalendar(2016, 6, 6)
                    ),
                    arrayOf(
                            "T",
                            GregorianCalendar(2016, 6, 7)
                    )
            )
    }
}
