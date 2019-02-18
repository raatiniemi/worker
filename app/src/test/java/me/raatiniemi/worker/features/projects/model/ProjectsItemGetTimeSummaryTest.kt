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

package me.raatiniemi.worker.features.projects.model

import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class ProjectsItemGetTimeSummaryTest(
        private val expected: String,
        private val timeIntervals: List<TimeInterval>
) {
    @Test
    fun getTimeSummary() {
        val project = Project(1, "Project name")
        val projectsItem = ProjectsItem(project, timeIntervals)

        assertEquals(expected, projectsItem.timeSummary)
    }

    companion object {
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                    arrayOf(
                            "1h 0m",
                            listOf(
                                    TimeInterval.builder(1L, 1L)
                                            .stopInMilliseconds(3600000)
                                            .build()
                            )
                    ),
                    arrayOf(
                            "2h 30m",
                            listOf(
                                    TimeInterval.builder(1L, 1L)
                                            .stopInMilliseconds(9000000)
                                            .build()
                            )
                    ),
                    arrayOf(
                            "3h 30m",
                            listOf(
                                    TimeInterval.builder(1L, 1L)
                                            .stopInMilliseconds(3600000)
                                            .build(),
                                    TimeInterval.builder(1L, 1L)
                                            .stopInMilliseconds(9000000)
                                            .build()
                            )
                    )
            )
    }
}
