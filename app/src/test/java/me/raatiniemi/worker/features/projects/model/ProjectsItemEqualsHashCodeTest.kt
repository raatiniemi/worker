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
import me.raatiniemi.worker.util.NullUtil.isNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class ProjectsItemEqualsHashCodeTest(
        private val message: String,
        private val expected: Boolean,
        private val projectsItem: ProjectsItem,
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
        assertEquals(message, projectsItem, compareTo)

        validateHashCodeWhenEqual()
    }

    private fun validateHashCodeWhenEqual() {
        assertEquals(message, projectsItem.hashCode(), compareTo?.hashCode())
    }

    private fun assertNotEqual() {
        assertNotEquals(message, projectsItem, compareTo)

        validateHashCodeWhenNotEqual()
    }

    private fun validateHashCodeWhenNotEqual() {
        if (isNull(compareTo)) {
            return
        }

        assertNotEquals(message, projectsItem.hashCode(), compareTo?.hashCode())
    }

    companion object {
        @JvmStatic
        val parameters: Collection<Array<Any?>>
            @Parameters
            get() {
                val project = Project.from(1L, "Name")
                val projectsItem = ProjectsItem.from(project, emptyList())

                return listOf(
                        arrayOf(
                                "With same instance",
                                true,
                                projectsItem,
                                projectsItem
                        ),
                        arrayOf(
                                "With null",
                                false,
                                projectsItem,
                                null
                        ),
                        arrayOf(
                                "With incompatible object",
                                false,
                                projectsItem,
                                ""
                        ),
                        arrayOf(
                                "With different project",
                                false,
                                projectsItem,
                                ProjectsItem.from(
                                        Project.from(2L, "Project name"),
                                        emptyList()
                                )
                        )
                )
            }
    }
}
