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

package me.raatiniemi.worker.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class TimesheetItemIsRegisteredTest(
        private val message: String,
        private val expected: Boolean,
        private val timeInterval: TimeInterval
) {
    @Test
    fun isRegistered() {
        val item = TimesheetItem.with(timeInterval)

        assertEquals(message, expected, item.isRegistered)
    }

    companion object {
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                    arrayOf(
                            "is registered",
                            true,
                            TimeInterval.builder(1L)
                                    .register()
                                    .build()
                    ),
                    arrayOf(
                            "is not registered",
                            false,
                            TimeInterval.builder(1L)
                                    .build()
                    )
            )
    }
}
