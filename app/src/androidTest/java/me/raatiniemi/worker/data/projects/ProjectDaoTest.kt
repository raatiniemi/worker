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
import me.raatiniemi.worker.data.room.Database
import me.raatiniemi.worker.koin.androidTestKoinModules
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class ProjectDaoTest : AutoCloseKoinTest() {
    private val database by inject<Database>()

    private val projects: ProjectDao
        get() = database.projects()

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            loadKoinModules(androidTestKoinModules)
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun count_withoutProjects() {
        val expected = 0

        val actual = projects.count()

        assertEquals(expected, actual)
    }

    @Test
    fun count_withProject() {
        projects.add(projectEntity { name = "Project name #1" })
        val expected = 1

        val actual = projects.count()

        assertEquals(expected, actual)
    }

    @Test
    fun count_withProjects() {
        projects.add(projectEntity { name = "Project name #1" })
        projects.add(projectEntity { name = "Project name #2" })
        val expected = 2

        val actual = projects.count()

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithoutProjects() {
        val expected = emptyList<ProjectEntity>()

        val actual = projects.findAll(0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithProject() {
        projects.add(projectEntity { name = "Project name #1" })
        val expected = listOf(
            projectEntity {
                id = 1
                name = "Project name #1"
            }
        )

        val actual = projects.findAll(0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithProjects() {
        projects.add(projectEntity { name = "Project name #1" })
        projects.add(projectEntity { name = "Project name #2" })
        val expected = listOf(
            projectEntity {
                id = 1
                name = "Project name #1"
            },
            projectEntity {
                id = 2
                name = "Project name #2"
            }
        )

        val actual = projects.findAll(0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withProjectBeforePage() {
        projects.add(projectEntity { name = "Project name #1" })
        projects.add(projectEntity { name = "Project name #2" })
        val expected = listOf(
            projectEntity {
                id = 2
                name = "Project name #2"
            }
        )

        val actual = projects.findAll(1, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withProjectAfterPage() {
        projects.add(projectEntity { name = "Project name #1" })
        projects.add(projectEntity { name = "Project name #2" })
        val expected = listOf(
            projectEntity {
                id = 1
                name = "Project name #1"
            }
        )

        val actual = projects.findAll(0, 1)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_sortedPage() {
        projects.add(projectEntity { name = "Project name #2" })
        projects.add(projectEntity { name = "Project name #1" })
        val expected = listOf(
            projectEntity {
                id = 2
                name = "Project name #1"
            },
            projectEntity {
                id = 1
                name = "Project name #2"
            }
        )

        val actual = projects.findAll(0, 10)

        assertEquals(expected, actual)
    }


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
    fun findByName_withLowercaseProjectName() {
        projects.add(projectEntity())
        val expected = projectEntity { id = 1 }

        val actual = projects.findByName(name = "name")

        assertEquals(expected, actual)
    }

    @Test
    fun findByName_withLowercaseProjectNameUsingSpecialCharacters() {
        projects.add(projectEntity { name = "ÅÄÖ" })
        val expected = projectEntity {
            id = 1
            name = "ÅÄÖ"
        }

        val actual = projects.findByName(name = "åäö")

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
