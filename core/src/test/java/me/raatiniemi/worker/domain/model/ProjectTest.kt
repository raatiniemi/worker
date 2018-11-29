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

package me.raatiniemi.worker.domain.model

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectTest {
    @Test
    fun builder_withDefaultValues() {
        val project = Project.from("Project name")

        assertNull(project.id)
        assertEquals("Project name", project.name)
    }

    @Test
    fun builder_withValues() {
        val project = Project.from(2L, "Project name")

        assertEquals("Project name", project.name)
        assertEquals(java.lang.Long.valueOf(2L), project.id)
    }

    @Test(expected = InvalidProjectNameException::class)
    fun project_withEmptyName() {
        Project.from("")
    }

    @Test
    fun getName() {
        val project = Project.from("Project name")

        assertEquals("Project name", project.name)
    }
}
