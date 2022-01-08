/*
 * Copyright (C) 2022 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.configuration

import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalStartingPoint
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class InMemoryKeyValueStoreTest {
    private lateinit var keyValueStore: KeyValueStore

    @Before
    fun setUp() {
        keyValueStore = InMemoryKeyValueStore()
    }

    @Test
    fun `bool without value for key`() {
        val expected = false

        val actual = keyValueStore.bool(AppKeys.HIDE_REGISTERED_TIME, expected)

        assertEquals(expected, actual)
    }

    @Test
    fun `bool with invalid type for key`() {
        val expected = false
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, 1)

        val actual = keyValueStore.bool(AppKeys.HIDE_REGISTERED_TIME, expected)

        assertEquals(expected, actual)
    }

    @Test
    fun `bool with value for key`() {
        val expected = true
        keyValueStore.set(AppKeys.HIDE_REGISTERED_TIME, true)

        val actual = keyValueStore.bool(AppKeys.HIDE_REGISTERED_TIME, false)

        assertEquals(expected, actual)
    }

    @Test
    fun `int without value for key`() {
        val expected = TimeIntervalStartingPoint.WEEK.rawValue

        val actual = keyValueStore.int(AppKeys.TIME_SUMMARY, expected)

        assertEquals(expected, actual)
    }

    @Test
    fun `int with invalid type for key`() {
        val expected = TimeIntervalStartingPoint.WEEK.rawValue
        keyValueStore.set(AppKeys.TIME_SUMMARY, false)

        val actual = keyValueStore.int(AppKeys.TIME_SUMMARY, expected)

        assertEquals(expected, actual)
    }

    @Test
    fun `int with value for key`() {
        val expected = TimeIntervalStartingPoint.WEEK.rawValue
        keyValueStore.set(AppKeys.TIME_SUMMARY, expected)

        val actual = keyValueStore.int(
            AppKeys.TIME_SUMMARY,
            TimeIntervalStartingPoint.MONTH.rawValue
        )

        assertEquals(expected, actual)
    }
}
