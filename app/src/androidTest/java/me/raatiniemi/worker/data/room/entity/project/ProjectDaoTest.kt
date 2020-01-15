/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

package me.raatiniemi.worker.data.room.entity.project

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
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
    fun count_withoutProjects() = runBlocking {
        val expected = 0

        val actual = projects.count()

        assertEquals(expected, actual)
    }

    @Test
    fun count_withProject() = runBlocking {
        projects.add(projectEntity { name = "Project name #1" })
        val expected = 1

        val actual = projects.count()

        assertEquals(expected, actual)
    }

    @Test
    fun count_withProjects() = runBlocking {
        projects.add(projectEntity { name = "Project name #1" })
        projects.add(projectEntity { name = "Project name #2" })
        val expected = 2

        val actual = projects.count()

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithoutProjects() = runBlocking {
        val expected = emptyList<ProjectEntity>()

        val actual = projects.findAll(0, 10)

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_pagingWithProject() = runBlocking {
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
    fun findAll_pagingWithProjects() = runBlocking {
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
    fun findAll_withProjectBeforePage() = runBlocking {
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
    fun findAll_withProjectAfterPage() = runBlocking {
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
    fun findAll_sortedPage() = runBlocking {
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
    fun findAll_withoutProjects() = runBlocking {
        val actual = projects.findAll()

        assertTrue(actual.isEmpty())
    }

    @Test
    fun findAll_withProject() = runBlocking {
        projects.add(projectEntity())
        val expected = listOf(projectEntity { id = 1 })

        val actual = projects.findAll()

        assertEquals(expected, actual)
    }

    @Test
    fun findAll_withProjects() = runBlocking {
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
    fun findByName_withoutProjects() = runBlocking {
        val actual = projects.findByName(name = "Name")

        assertNull(actual)
    }

    @Test
    fun findByName_withProject() = runBlocking {
        projects.add(projectEntity())
        val expected = projectEntity { id = 1 }

        val actual = projects.findByName(name = "Name")

        assertEquals(expected, actual)
    }

    @Test
    fun findByName_withLowercaseProjectName() = runBlocking {
        projects.add(projectEntity())
        val expected = projectEntity { id = 1 }

        val actual = projects.findByName(name = "name")

        assertEquals(expected, actual)
    }

    @Test
    fun findByName_withLowercaseProjectNameUsingSpecialCharacters() = runBlocking {
        projects.add(projectEntity { name = "ÅÄÖ" })
        val expected = projectEntity {
            id = 1
            name = "ÅÄÖ"
        }

        val actual = projects.findByName(name = "åäö")

        assertEquals(expected, actual)
    }

    @Test
    fun findByName_withProjects() = runBlocking {
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
    fun findById_withoutProjects() = runBlocking {
        val actual = projects.findById(id = 1)

        assertNull(actual)
    }

    @Test
    fun findById_withProject() = runBlocking {
        projects.add(projectEntity())
        val expected = projectEntity { id = 1 }

        val actual = projects.findById(id = 1)

        assertEquals(expected, actual)
    }

    @Test
    fun findById_withProjects() = runBlocking {
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
    fun remove_withoutProjects() = runBlocking {
        val entity = projectEntity()

        projects.remove(entity)
    }

    @Test
    fun remove_withProject() = runBlocking {
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
    fun remove_withProjects() = runBlocking {
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
