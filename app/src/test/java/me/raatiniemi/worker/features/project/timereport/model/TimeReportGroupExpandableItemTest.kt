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

import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.model.timeInterval
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class TimeReportGroupExpandableItemTest {
    @Test(expected = IndexOutOfBoundsException::class)
    fun getWithIndexOutOfBounds() {
        val group = TimeReportGroup.build(Date(), TreeSet())

        group.get(1)
    }

    @Test
    fun get() {
        val expected = TimeReportItem.with(timeInterval { })
        val group = TimeReportGroup.build(Date(), sortedSetOf(expected))

        val actual = group.get(0)

        assertEquals(expected, actual)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun setWithIndexOutOfBounds() {
        val timeReportItem = TimeReportItem.with(timeInterval { })
        val group = TimeReportGroup.build(Date(), sortedSetOf())

        group.set(1, timeReportItem)
    }

    fun set() {
        val expected = TimeReportItem.with(timeInterval { })
        val group = TimeReportGroup.build(Date(), sortedSetOf())


        group.set(0, expected)

        val actual = group.get(0)
        assertEquals(expected, actual)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun removeWithIndexOutOfBounds() {
        val group = TimeReportGroup.build(Date(), sortedSetOf())

        group.remove(1)
    }

    @Test
    fun remove() {
        val expected = TimeReportItem.with(timeInterval { })
        val group = TimeReportGroup.build(Date(), sortedSetOf(expected))

        val actual = group.remove(0)

        assertEquals(expected, actual)
    }

    @Test
    fun sizeWithoutItems() {
        val group = TimeReportGroup.build(Date(), sortedSetOf())

        assertEquals(0, group.size())
    }

    @Test
    fun sizeWithItem() {
        val expected = 1
        val timeReportItem = TimeReportItem.with(timeInterval { })
        val group = TimeReportGroup.build(Date(), sortedSetOf(timeReportItem))

        val actual = group.size()

        assertEquals(expected, actual)
    }

    @Test
    fun sizeWithItems() {
        val expected = 2
        val items = sortedSetOf(
                TimeReportItem.with(timeInterval {
                    id = 2
                    isRegistered = true
                }),
                TimeReportItem.with(timeInterval {
                    id = 3
                    startInMilliseconds = 1
                    stopInMilliseconds = 5
                })
        )
        val group = TimeReportGroup.build(Date(), items)

        val actual = group.size()

        assertEquals(expected, actual)
    }
}
