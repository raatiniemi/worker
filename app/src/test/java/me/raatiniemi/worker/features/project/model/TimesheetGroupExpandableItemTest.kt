/*
 * Copyright (C) 2018 Worker Project
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

package me.raatiniemi.worker.features.project.model

import me.raatiniemi.worker.domain.model.TimesheetItem
import me.raatiniemi.worker.factory.TimeFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class TimesheetGroupExpandableItemTest {
    @Test(expected = IndexOutOfBoundsException::class)
    fun getWithIndexOutOfBounds() {
        val group = TimesheetGroup.build(Date(), TreeSet())

        group.get(1)
    }

    @Test
    fun get() {
        val time = TimeFactory.builder(1L).build()
        val items = sortedSetOf(TimesheetItem.with(time))
        val group = TimesheetGroup.build(Date(), items)

        assertEquals(items.first(), group.get(0))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun setWithIndexOutOfBounds() {
        val time = TimeFactory.builder(1L).build()
        val item = TimesheetItem.with(time)
        val group = TimesheetGroup.build(Date(), TreeSet())

        group.set(1, item)
    }

    fun set() {
        val time = TimeFactory.builder(1L).build()
        val items = sortedSetOf(TimesheetItem.with(time))
        val group = TimesheetGroup.build(Date(), items)

        group.set(1, items.first())
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun removeWithIndexOutOfBounds() {
        val group = TimesheetGroup.build(Date(), TreeSet())

        group.remove(1)
    }

    @Test
    fun remove() {
        val time = TimeFactory.builder(1L).build()
        val items = sortedSetOf(TimesheetItem.with(time))
        val group = TimesheetGroup.build(Date(), items)

        assertEquals(items.first(), group.remove(0))
    }

    @Test
    fun sizeWithoutItems() {
        val group = TimesheetGroup.build(Date(), TreeSet())

        assertEquals(0, group.size())
    }

    @Test
    fun sizeWithItem() {
        val time = TimeFactory.builder(1L).build()
        val items = sortedSetOf(TimesheetItem.with(time))
        val group = TimesheetGroup.build(Date(), items)

        assertEquals(1, group.size())
    }

    @Test
    fun sizeWithItems() {
        val time1 = TimeFactory.builder(1L)
                .id(2L)
                .startInMilliseconds(0L)
                .register()
                .build()
        val time2 = TimeFactory.builder(2L)
                .id(3L)
                .startInMilliseconds(1L)
                .stopInMilliseconds(5L)
                .build()
        val items = sortedSetOf(
                TimesheetItem.with(time1),
                TimesheetItem.with(time2)
        )
        val group = TimesheetGroup.build(Date(), items)

        assertEquals(2, group.size())
    }
}
