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

package me.raatiniemi.worker.feature.projects.all.model

import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class ProjectsItemIsActiveTest(
    private val expected: Boolean,
    private val timeIntervals: List<TimeInterval>
) {
    @Test
    fun isActive() {
        val projectsItem = ProjectsItem(android, timeIntervals)

        assertEquals(expected, projectsItem.isActive)
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        val parameters: Collection<Array<Any>>
            @Parameters
            get() = listOf(
                arrayOf(
                    false,
                    getTimeIntervals(false)
                ),
                arrayOf(
                    true,
                    getTimeIntervals(true)
                )
            )

        private fun getTimeIntervals(isProjectActive: Boolean): List<TimeInterval> {
            if (isProjectActive) {
                return listOf(
                    timeInterval(android.id) { builder ->
                        builder.id = TimeIntervalId(1)
                        builder.start = Milliseconds(1)
                    }
                )
            }

            return emptyList()
        }
    }
}
