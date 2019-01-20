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

package me.raatiniemi.worker.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.raatiniemi.worker.data.Database
import me.raatiniemi.worker.data.projects.TimeIntervalDao
import me.raatiniemi.worker.data.projects.TimeReportDao
import me.raatiniemi.worker.data.projects.projectEntity
import me.raatiniemi.worker.data.projects.timeIntervalEntity
import me.raatiniemi.worker.domain.comparator.TimesheetDateComparator
import me.raatiniemi.worker.domain.comparator.TimesheetItemComparator
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimesheetItem
import me.raatiniemi.worker.domain.repository.PageRequest
import me.raatiniemi.worker.domain.repository.TimesheetRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class TimesheetRoomRepositoryTest {
    private val project = Project(1, "Name")

    private lateinit var database: Database
    private lateinit var timeReport: TimeReportDao
    private lateinit var timeIntervals: TimeIntervalDao
    private lateinit var repository: TimesheetRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, Database::class.java)
                .allowMainThreadQueries()
                .build()

        database.projects()
                .add(
                        projectEntity {
                            id = project.id ?: 0
                            name = project.name
                        }
                )
        timeReport = database.timeReport()
        timeIntervals = database.timeIntervals()
        repository = TimesheetRoomRepository(timeReport, timeIntervals)
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun timesheetItemSet(timeIntervals: List<TimeInterval>): SortedSet<TimesheetItem> {
        return timeIntervals.map { TimesheetItem(it) }
                .toSortedSet(TimesheetItemComparator())
    }

    private fun timesheetItemSet(timeInterval: TimeInterval): SortedSet<TimesheetItem> {
        return timesheetItemSet(listOf(timeInterval))
    }

    @Test
    fun getTimesheet_withoutTimeIntervals() {
        val actual = repository.getTimesheet(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun getTimesheet_withoutProjectTimeInterval() {
        database.projects().add(
                projectEntity {
                    id = 2
                    name = "Name #2"
                }
        )
        val entity = timeIntervalEntity {
            projectId = 2
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        timeIntervals.add(entity)

        val actual = repository.getTimesheet(1, PageRequest.withOffset(0))

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun getTimesheet_withTimeIntervalsForSameDate() {
        val ti1 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val ti2 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 11
            stopInMilliseconds = 30
        }
        listOf(ti1, ti2).forEach { timeIntervals.add(it) }
        val timeIntervals = listOf(
                ti1.copy(id = 1).toTimeInterval(),
                ti2.copy(id = 2).toTimeInterval()
        )
        val expected = TreeMap<Date, Set<TimesheetItem>>(TimesheetDateComparator()).apply {
            put(Date(ti1.startInMilliseconds), timesheetItemSet(timeIntervals))
        }

        val actual = repository.getTimesheet(1, PageRequest.withOffset(0))

        assertEquals(expected, actual)
    }

    @Test
    fun getTimesheet_withTimeIntervalsForDifferentDates() {
        val ti1 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val ti2 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        listOf(ti1, ti2).forEach { timeIntervals.add(it) }
        val expected = TreeMap<Date, Set<TimesheetItem>>(TimesheetDateComparator()).apply {
            put(Date(ti1.startInMilliseconds), timesheetItemSet(ti1.copy(id = 1).toTimeInterval()))
            put(Date(ti2.startInMilliseconds), timesheetItemSet(ti2.copy(id = 2).toTimeInterval()))
        }

        val actual = repository.getTimesheet(1, PageRequest.withOffset(0))

        assertEquals(expected, actual)
    }

    @Test
    fun getTimesheet_withTimeIntervalsWithOffset() {
        val ti1 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val ti2 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        listOf(ti1, ti2).forEach { timeIntervals.add(it) }
        val expected = TreeMap<Date, Set<TimesheetItem>>(TimesheetDateComparator()).apply {
            put(Date(ti1.startInMilliseconds), timesheetItemSet(ti1.copy(id = 1).toTimeInterval()))
        }

        val actual = repository.getTimesheet(1, PageRequest.withOffset(1))

        assertEquals(expected, actual)
    }

    @Test
    fun getTimesheet_withTimeIntervalsWithMaxResult() {
        val ti1 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val ti2 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        listOf(ti1, ti2).forEach { timeIntervals.add(it) }
        val expected = TreeMap<Date, Set<TimesheetItem>>(TimesheetDateComparator()).apply {
            put(Date(ti2.startInMilliseconds), timesheetItemSet(ti2.copy(id = 2).toTimeInterval()))
        }

        val actual = repository.getTimesheet(1, PageRequest.withMaxResults(1))

        assertEquals(expected, actual)
    }

    @Test
    fun getTimesheetWithoutRegisteredEntries_withoutTimeIntervals() {
        val actual = repository.getTimesheetWithoutRegisteredEntries(
                1,
                PageRequest.withOffset(0)
        )

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun getTimesheetWithoutRegisteredEntries_withoutProjectTimeInterval() {
        database.projects().add(
                projectEntity {
                    id = 2
                    name = "Name #2"
                }
        )
        val entity = timeIntervalEntity {
            projectId = 2
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        timeIntervals.add(entity)

        val actual = repository.getTimesheetWithoutRegisteredEntries(
                1,
                PageRequest.withOffset(0)
        )

        assertEquals(emptyMap<Date, Set<TimeInterval>>(), actual)
    }

    @Test
    fun getTimesheetWithoutRegisteredEntries_withTimeIntervalsForSameDate() {
        val ti1 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val ti2 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 11
            stopInMilliseconds = 30
        }
        listOf(ti1, ti2).forEach { timeIntervals.add(it) }
        val timeIntervals = listOf(
                ti1.copy(id = 1).toTimeInterval(),
                ti2.copy(id = 2).toTimeInterval()
        )
        val expected = TreeMap<Date, Set<TimesheetItem>>(TimesheetDateComparator()).apply {
            put(Date(ti1.startInMilliseconds), timesheetItemSet(timeIntervals))
        }

        val actual = repository.getTimesheetWithoutRegisteredEntries(
                1,
                PageRequest.withOffset(0)
        )

        assertEquals(expected, actual)
    }

    @Test
    fun getTimesheetWithoutRegisteredEntries_withTimeIntervalsForDifferentDates() {
        val ti1 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val ti2 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        listOf(ti1, ti2).forEach { timeIntervals.add(it) }
        val expected = TreeMap<Date, Set<TimesheetItem>>(TimesheetDateComparator()).apply {
            put(Date(ti1.startInMilliseconds), timesheetItemSet(ti1.copy(id = 1).toTimeInterval()))
            put(Date(ti2.startInMilliseconds), timesheetItemSet(ti2.copy(id = 2).toTimeInterval()))
        }

        val actual = repository.getTimesheetWithoutRegisteredEntries(
                1,
                PageRequest.withOffset(0)
        )

        assertEquals(expected, actual)
    }

    @Test
    fun getTimesheetWithoutRegisteredEntries_withTimeIntervalsWithOffset() {
        val ti1 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val ti2 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        listOf(ti1, ti2).forEach { timeIntervals.add(it) }
        val expected = TreeMap<Date, Set<TimesheetItem>>(TimesheetDateComparator()).apply {
            put(Date(ti1.startInMilliseconds), timesheetItemSet(ti1.copy(id = 1).toTimeInterval()))
        }

        val actual = repository.getTimesheetWithoutRegisteredEntries(
                1,
                PageRequest.withOffset(1)
        )

        assertEquals(expected, actual)
    }

    @Test
    fun getTimesheetWithoutRegisteredEntries_withTimeIntervalsWithMaxResult() {
        val ti1 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
        }
        val ti2 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        listOf(ti1, ti2).forEach { timeIntervals.add(it) }
        val expected = TreeMap<Date, Set<TimesheetItem>>(TimesheetDateComparator()).apply {
            put(Date(ti2.startInMilliseconds), timesheetItemSet(ti2.copy(id = 2).toTimeInterval()))
        }

        val actual = repository.getTimesheetWithoutRegisteredEntries(
                1,
                PageRequest.withMaxResults(1)
        )

        assertEquals(expected, actual)
    }

    @Test
    fun getTimesheetWithoutRegisteredEntries_withRegisteredTimeInterval() {
        val ti1 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 1
            stopInMilliseconds = 10
            registered = true
        }
        val ti2 = timeIntervalEntity {
            projectId = 1
            startInMilliseconds = 90000000
            stopInMilliseconds = 93000000
        }
        listOf(ti1, ti2).forEach { timeIntervals.add(it) }
        val expected = TreeMap<Date, Set<TimesheetItem>>(TimesheetDateComparator()).apply {
            put(Date(ti2.startInMilliseconds), timesheetItemSet(ti2.copy(id = 2).toTimeInterval()))
        }

        val actual = repository.getTimesheetWithoutRegisteredEntries(
                1,
                PageRequest.withOffset(0)
        )

        assertEquals(expected, actual)
    }
}
