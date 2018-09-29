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

package me.raatiniemi.worker.data.projects

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProjectDaoTest : BaseDaoTest() {
    @Test
    fun findAll_withoutProjects() {
        val actual = projects.findAll()

        assertTrue(actual.isEmpty())
    }

    @Test
    fun findAll_withProject() {
        projects.add(projectEntity())
        val expected = listOf(projectEntity { id = 1 })

        val actual = projects.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withProjects() {
        projects.add(projectEntity { name = "Name #3" })
        projects.add(projectEntity { name = "Name #1" })
        projects.add(projectEntity { name = "Name #2" })
        val expected = listOf(
                projectEntity {
                    id = 2
                    name = "Name #1"
                },
                projectEntity {
                    id = 3
                    name = "Name #2"
                },
                projectEntity {
                    id = 1
                    name = "Name #3"
                }
        )

        val actual = projects.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun findByName_withoutProjects() {
        val actual = projects.findByName(name = "Name")

        assertNull(actual)
    }

    @Test
    fun findByName_withProject() {
        projects.add(projectEntity())
        val expected = projectEntity { id = 1 }

        val actual = projects.findByName(name = "Name")

        assertEquals(expected, actual)
    }

    @Test
    fun findByName_withProjects() {
        projects.add(projectEntity { name = "Name #3" })
        projects.add(projectEntity { name = "Name #1" })
        projects.add(projectEntity { name = "Name #2" })
        val expected = projectEntity {
            id = 3
            name = "Name #2"
        }

        val actual = projects.findByName(name = "Name #2")

        assertEquals(expected, actual)
    }

    @Test
    fun findById_withoutProjects() {
        val actual = projects.findById(id = 1)

        assertNull(actual)
    }

    @Test
    fun findById_withProject() {
        projects.add(projectEntity())
        val expected = projectEntity { id = 1 }

        val actual = projects.findById(id = 1)

        assertEquals(expected, actual)
    }

    @Test
    fun findById_withProjects() {
        projects.add(projectEntity { name = "Name #3" })
        projects.add(projectEntity { name = "Name #1" })
        projects.add(projectEntity { name = "Name #2" })
        val expected = projectEntity {
            id = 3
            name = "Name #2"
        }

        val actual = projects.findById(id = 3)

        assertEquals(expected, actual)
    }

    @Test
    fun remove_withoutProjects() {
        val entity = projectEntity()

        projects.remove(entity)
    }

    @Test
    fun remove_withProject() {
        projects.add(projectEntity())
        val entity = projectEntity {
            id = 1
            name = "Name"
        }

        projects.remove(entity)

        val actual = projects.findAll()
        assertTrue(actual.isEmpty())
    }

    @Test
    fun remove_withProjects() {
        projects.add(projectEntity { name = "Name #3" })
        projects.add(projectEntity { name = "Name #1" })
        projects.add(projectEntity { name = "Name #2" })
        val entity = projectEntity {
            id = 3
            name = "Name #2"
        }
        val expected = listOf(
                projectEntity {
                    id = 2
                    name = "Name #1"
                },
                projectEntity {
                    id = 1
                    name = "Name #3"
                }
        )

        projects.remove(entity)

        val actual = projects.findAll()
        assertEquals(expected, actual)
    }
}
