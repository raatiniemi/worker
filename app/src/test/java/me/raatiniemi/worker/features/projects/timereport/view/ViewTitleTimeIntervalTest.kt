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

package me.raatiniemi.worker.features.projects.timereport.view

import me.raatiniemi.worker.domain.model.Milliseconds
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeIntervalId
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.project.model.android
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.*

@RunWith(Parameterized::class)
class ViewTitleTimeIntervalTest(
    private val message: String,
    private val expected: String,
    private val timeInterval: TimeInterval
) {
    @Test
    fun getTitle() {
        assertEquals(message, expected, title(timeInterval))
    }

    companion object {
        private val START = GregorianCalendar(2016, 1, 28, 8, 0)
        private val STOP = GregorianCalendar(2016, 1, 28, 11, 30)

        @Suppress("unused")
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                arrayOf(
                    "active time interval",
                    "08:00",
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = Milliseconds(START.timeInMillis)
                    }
                ),
                arrayOf(
                    "inactive time interval",
                    "08:00 - 11:30",
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(2)
                        builder.start = Milliseconds(START.timeInMillis)
                        builder.stop = Milliseconds(STOP.timeInMillis)
                    }
                ),
                arrayOf(
                    "registered time interval",
                    "08:00 - 11:30",
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(3)
                        builder.start = Milliseconds(START.timeInMillis)
                        builder.stop = Milliseconds(STOP.timeInMillis)
                        builder.isRegistered = true
                    }
                )
            )
    }
}
