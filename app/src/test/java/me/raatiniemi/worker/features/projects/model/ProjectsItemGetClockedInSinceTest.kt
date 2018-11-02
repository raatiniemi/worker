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

package me.raatiniemi.worker.features.projects.model

import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.util.NullUtil.isNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*

@RunWith(Parameterized::class)
class ProjectsItemGetClockedInSinceTest(
        private val message: String,
        private val expected: String?,
        private val timeIntervals: List<TimeInterval>?
) : ProjectsItemResourceTest() {
    private val project = Project.from("Project name")

    @Test
    fun getClockedInSince() {
        if (isNull(timeIntervals)) {
            assertWithoutTimeIntervals()
            return
        }

        assertWithTimeIntervals()
    }

    private fun assertWithoutTimeIntervals() {
        val projectsItem = ProjectsItem.from(project, emptyList())

        assertNull(message, projectsItem.getClockedInSince(resources))
    }

    private fun assertWithTimeIntervals() {
        val projectsItem = ProjectsItem.from(project, timeIntervals)

        assertEquals(expected, projectsItem.getClockedInSince(resources))
    }

    companion object {
        @JvmStatic
        val parameters: Collection<Array<Any?>>
            @Parameters
            get() = listOf<Array<Any?>>(
                    arrayOf(
                            "Without registered time",
                            null,
                            null
                    ),
                    arrayOf(
                            "Without active time",
                            null,
                            listOf(
                                    TimeInterval.builder(1L)
                                            .stopInMilliseconds(1L)
                                            .build()
                            )
                    ),
                    arrayOf(
                            "With an hour elapsed",
                            "Since 15:14 (1h 0m)",
                            listOf(
                                    mockActiveTimeWithElapsedTimeInSecondsAndClockedInTime(
                                            3600L,
                                            GregorianCalendar(2016, 1, 28, 15, 14)
                                    )
                            )
                    ),
                    arrayOf(
                            "With half an hour elapsed",
                            "Since 20:25 (30m)",
                            listOf(
                                    mockActiveTimeWithElapsedTimeInSecondsAndClockedInTime(
                                            1800L,
                                            GregorianCalendar(2016, 1, 28, 20, 25)
                                    )
                            )
                    )
            )

        private fun mockActiveTimeWithElapsedTimeInSecondsAndClockedInTime(
                elapsedTimeInSeconds: Long,
                clockedInTime: Calendar
        ): TimeInterval {
            val timeInterval = mock(TimeInterval::class.java)

            `when`(timeInterval.isActive)
                    .thenReturn(true)

            `when`(timeInterval.interval)
                    .thenReturn(elapsedTimeInSeconds * 1000)

            `when`(timeInterval.startInMilliseconds)
                    .thenReturn(clockedInTime.timeInMillis)

            return timeInterval
        }
    }
}
