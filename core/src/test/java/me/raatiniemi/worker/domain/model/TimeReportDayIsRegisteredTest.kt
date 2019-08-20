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

import me.raatiniemi.worker.domain.project.model.android
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.*

@RunWith(Parameterized::class)
class TimeReportDayIsRegisteredTest(
    private val expected: Boolean,
    private val timeIntervals: List<TimeInterval>
) {
    @Test
    fun isRegistered() {
        val day = timeReportDay(Date(), timeIntervals)

        assertEquals(expected, day.isRegistered)
    }

    companion object {
        private val NOT_REGISTERED_TIME = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(1)
            builder.start = Milliseconds(1)
            builder.isRegistered = false
        }
        private val REGISTERED_TIME = timeInterval(android.id) { builder ->
            builder.id = TimeIntervalId(2)
            builder.start = Milliseconds(1)
            builder.stop = Milliseconds(10)
            builder.isRegistered = true
        }

        @Suppress("unused")
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                arrayOf(
                    true,
                    listOf(REGISTERED_TIME)
                ),
                arrayOf(
                    false,
                    listOf(NOT_REGISTERED_TIME)
                ),
                arrayOf(
                    false,
                    listOf(NOT_REGISTERED_TIME, REGISTERED_TIME)
                ),
                arrayOf(
                    true,
                    listOf(REGISTERED_TIME, REGISTERED_TIME)
                )
            )
    }
}
