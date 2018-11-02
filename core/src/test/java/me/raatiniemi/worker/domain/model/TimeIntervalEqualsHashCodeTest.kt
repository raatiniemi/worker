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

import me.raatiniemi.worker.util.NullUtil.isNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class TimeIntervalEqualsHashCodeTest(
        private val message: String,
        private val expected: Boolean,
        private val timeInterval: TimeInterval,
        private val compareTo: Any?
) {
    @Test
    fun equals() {
        if (shouldBeEqual()) {
            assertEqual()
            return
        }

        assertNotEqual()
    }

    private fun shouldBeEqual(): Boolean {
        return expected
    }

    private fun assertEqual() {
        assertEquals(message, timeInterval, compareTo)

        validateHashCodeWhenEqual()
    }

    private fun validateHashCodeWhenEqual() {
        assertEquals(message, timeInterval.hashCode(), compareTo.hashCode())
    }

    private fun assertNotEqual() {
        assertNotEquals(message, timeInterval, compareTo)

        validateHashCodeWhenNotEqual()
    }

    private fun validateHashCodeWhenNotEqual() {
        if (isNull(compareTo)) {
            return
        }

        assertNotEquals(message, timeInterval.hashCode(), compareTo.hashCode())
    }

    companion object {
        @JvmStatic
        val parameters: Collection<Array<Any?>>
            @Parameters
            get() {
                val timeInterval = TimeInterval.builder(1L).build()

                return listOf<Array<Any?>>(
                        arrayOf(
                                "With same instance",
                                true,
                                timeInterval,
                                timeInterval
                        ),
                        arrayOf(
                                "With null",
                                false,
                                timeInterval,
                                null
                        ),
                        arrayOf(
                                "With incompatible object",
                                false,
                                timeInterval,
                                ""
                        ),
                        arrayOf(
                                "With different project id",
                                false,
                                timeInterval,
                                TimeInterval.builder(2L)
                                        .build()
                        ),
                        arrayOf(
                                "With different id",
                                false,
                                timeInterval,
                                TimeInterval.builder(1L)
                                        .id(2L)
                                        .build()
                        ),
                        arrayOf(
                                "With different start in milliseconds",
                                false,
                                timeInterval,
                                TimeInterval.builder(1L)
                                        .startInMilliseconds(2L)
                                        .build()
                        ),
                        arrayOf(
                                "With different stop in milliseconds",
                                false,
                                timeInterval,
                                TimeInterval.builder(1L)
                                        .stopInMilliseconds(1L)
                                        .build()
                        ),
                        arrayOf(
                                "With different register status",
                                false,
                                timeInterval,
                                TimeInterval.builder(1L)
                                        .register()
                                        .build()
                        )
                )
            }
    }
}
