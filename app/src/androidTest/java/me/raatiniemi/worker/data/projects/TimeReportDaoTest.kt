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

package me.raatiniemi.worker.data.projects

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class TimeReportDaoTest : BaseDaoTest() {
    @Before
    override fun setUp() {
        super.setUp()

        projects.add(projectEntity())
    }

    @Test
    fun count_withoutTimeIntervals() {
        val expected = 0

        val actual = timeReport.count(1)

        assertEquals(expected, actual)
    }

    @Test
    fun count_withTimeInterval() {
        val expected = 1
        timeIntervals.add(timeIntervalEntity { })

        val actual = timeReport.count(1)

        assertEquals(expected, actual)
    }

    @Test
    fun count_withTimeIntervalsOnSameDay() {
        val expected = 1
        timeIntervals.add(timeIntervalEntity { })
        timeIntervals.add(timeIntervalEntity { })

        val actual = timeReport.count(1)

        assertEquals(expected, actual)
    }

    @Test
    fun count_withTimeIntervalsOnDifferentDays() {
        val expected = 2
        timeIntervals.add(timeIntervalEntity { })
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = Date().time
        })

        val actual = timeReport.count(1)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegistered_withoutTimeIntervals() {
        val expected = 0

        val actual = timeReport.countNotRegistered(1)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegistered_withTimeInterval() {
        val expected = 1
        timeIntervals.add(timeIntervalEntity { })

        val actual = timeReport.countNotRegistered(1)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegistered_withTimeIntervalsOnSameDay() {
        val expected = 1
        timeIntervals.add(timeIntervalEntity { })
        timeIntervals.add(timeIntervalEntity { })

        val actual = timeReport.countNotRegistered(1)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegistered_withRegisteredTimeIntervalOnDifferentDays() {
        val expected = 1
        timeIntervals.add(timeIntervalEntity { })
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = Date().time
            registered = true
        })

        val actual = timeReport.countNotRegistered(1)

        assertEquals(expected, actual)
    }

    @Test
    fun countNotRegistered_withTimeIntervalsOnDifferentDays() {
        val expected = 2
        timeIntervals.add(timeIntervalEntity { })
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = Date().time
        })

        val actual = timeReport.countNotRegistered(1)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withoutTimeIntervals() {
        val actual = timeReport.findAll(1, 0, 10)

        assertEquals(emptyList<TimeReportQueryGroup>(), actual)
    }

    @Test
    fun findAll_withoutTimeIntervalForProject() {
        projects.add(projectEntity {
            id = 2
            name = "Name #2"
        })
        timeIntervals.add(timeIntervalEntity { projectId = 2 })

        val actual = timeReport.findAll(1, 0, 10)

        assertEquals(emptyList<TimeReportQueryGroup>(), actual)
    }

    @Test
    fun findAll_withTimeInterval() {
        timeIntervals.add(timeIntervalEntity())
        val expected = listOf(
                TimeReportQueryGroup(1, "1")
        )

        val actual = timeReport.findAll(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalOnSameDay() {
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = 10
            stopInMilliseconds = 100
        })
        val expected = listOf(
                TimeReportQueryGroup(1, "1,2")
        )

        val actual = timeReport.findAll(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalOnDifferentDays() {
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = 90000000
            stopInMilliseconds = 90000010
        })
        val expected = listOf(
                TimeReportQueryGroup(90000000, "2"),
                TimeReportQueryGroup(1, "1")
        )

        val actual = timeReport.findAll(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalWithPosition() {
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = 90000000
            stopInMilliseconds = 90000010
        })
        val expected = listOf(
                TimeReportQueryGroup(1, "1")
        )

        val actual = timeReport.findAll(1, 1, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withTimeIntervalWithPageSize() {
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = 90000000
            stopInMilliseconds = 90000010
        })
        val expected = listOf(
                TimeReportQueryGroup(90000000, "2")
        )

        val actual = timeReport.findAll(1, 0, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegistered_withoutTimeIntervals() {
        val actual = timeReport.findNotRegistered(1, 0, 10)

        assertEquals(emptyList<TimeReportQueryGroup>(), actual)
    }

    @Test
    fun findNotRegistered_withoutTimeIntervalForProject() {
        projects.add(projectEntity {
            id = 2
            name = "Name #2"
        })
        timeIntervals.add(timeIntervalEntity { projectId = 2 })

        val actual = timeReport.findNotRegistered(1, 0, 10)

        assertEquals(emptyList<TimeReportQueryGroup>(), actual)
    }

    @Test
    fun findNotRegistered_withTimeInterval() {
        timeIntervals.add(timeIntervalEntity())
        val expected = listOf(
                TimeReportQueryGroup(1, "1")
        )

        val actual = timeReport.findNotRegistered(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegistered_withTimeIntervalOnSameDay() {
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = 10
            stopInMilliseconds = 100
        })
        val expected = listOf(
                TimeReportQueryGroup(1, "1,2")
        )

        val actual = timeReport.findNotRegistered(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegistered_withTimeIntervalOnDifferentDays() {
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = 90000000
            stopInMilliseconds = 90000010
        })
        val expected = listOf(
                TimeReportQueryGroup(90000000, "2"),
                TimeReportQueryGroup(1, "1")
        )

        val actual = timeReport.findNotRegistered(1, 0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegistered_withTimeIntervalWithPosition() {
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = 90000000
            stopInMilliseconds = 90000010
        })
        val expected = listOf(
                TimeReportQueryGroup(1, "1")
        )

        val actual = timeReport.findNotRegistered(1, 1, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegistered_withTimeIntervalWithPageSize() {
        timeIntervals.add(timeIntervalEntity())
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = 90000000
            stopInMilliseconds = 90000010
        })
        val expected = listOf(
                TimeReportQueryGroup(90000000, "2")
        )

        val actual = timeReport.findNotRegistered(1, 0, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun findNotRegistered_withRegisteredTimeInterval() {
        timeIntervals.add(timeIntervalEntity { registered = true })
        timeIntervals.add(timeIntervalEntity {
            startInMilliseconds = 90000000
            stopInMilliseconds = 90000010
        })
        val expected = listOf(
                TimeReportQueryGroup(90000000, "2")
        )

        val actual = timeReport.findNotRegistered(1, 0, 10)

        assertEquals(expected, actual)
    }
}