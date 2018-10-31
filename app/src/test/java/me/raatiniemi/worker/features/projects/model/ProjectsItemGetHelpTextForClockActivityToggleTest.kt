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
import me.raatiniemi.worker.factory.TimeIntervalFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.*

@RunWith(Parameterized::class)
class ProjectsItemGetHelpTextForClockActivityToggleTest(
        private val expected: String,
        private val timeIntervals: List<TimeInterval>
) : ProjectsItemResourceTest() {
    @Test
    fun getHelpTextForClockActivityToggle() {
        val project = Project.builder("Project #1").build()
        val projectsItem = ProjectsItem.from(project, timeIntervals)

        assertEquals(expected, projectsItem.getHelpTextForClockActivityToggle(resources))
    }

    companion object {
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = Arrays.asList(
                    arrayOf(
                            "Clock in %s",
                            getTimeIntervals(false)
                    ),
                    arrayOf(
                            "Clock out %s",
                            getTimeIntervals(true)
                    )
            )

        private fun getTimeIntervals(isProjectActive: Boolean): List<TimeInterval> {
            if (isProjectActive) {
                return listOf(
                        TimeIntervalFactory.builder(1L)
                                .startInMilliseconds(1)
                                .stopInMilliseconds(0)
                                .build()
                )
            }

            return emptyList()
        }
    }
}