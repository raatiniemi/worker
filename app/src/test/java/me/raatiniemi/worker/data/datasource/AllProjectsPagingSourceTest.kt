/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

package me.raatiniemi.worker.data.datasource

import androidx.paging.PagingSource.LoadParams.Refresh
import androidx.paging.PagingSource.LoadResult.Page
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.project.model.android
import me.raatiniemi.worker.domain.project.model.cli
import me.raatiniemi.worker.domain.project.model.ios
import me.raatiniemi.worker.domain.project.model.web
import me.raatiniemi.worker.domain.project.usecase.CreateProject
import me.raatiniemi.worker.feature.projects.all.model.ProjectsItem
import me.raatiniemi.worker.koin.testKoinModules
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.koin.test.inject

@RunWith(JUnit4::class)
class AllProjectsPagingSourceTest : AutoCloseKoinTest() {
    private val createProject by inject<CreateProject>()

    private lateinit var pagingSource: AllProjectsPagingSource

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            loadKoinModules(testKoinModules)
        }

        pagingSource = AllProjectsPagingSource(
            keyValueStore = get(),
            countProjects = get(),
            findProjects = get(),
            getProjectTimeSince = get()
        )
    }

    // Load

    @Test
    fun `load without projects`() {
        val expected = Page(
            data = emptyList<ProjectsItem>(),
            prevKey = null,
            nextKey = null
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `load with project`() {
        runBlocking {
            createProject(android.name)
        }
        val expected = Page(
            data = listOf(
                ProjectsItem(android, emptyList())
            ),
            prevKey = null,
            nextKey = null
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `load with projects`() {
        runBlocking {
            createProject(android.name)
            createProject(cli.name)
            createProject(ios.name)
            createProject(web.name)
        }
        val expected = Page(
            data = listOf(
                ProjectsItem(android, emptyList()),
                ProjectsItem(cli, emptyList()),
                ProjectsItem(ios, emptyList()),
                ProjectsItem(web, emptyList()),
            ),
            prevKey = null,
            nextKey = null
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = null,
                    loadSize = 4,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `load with projects before range`() {
        runBlocking {
            createProject(android.name)
            createProject(cli.name)
            createProject(ios.name)
            createProject(web.name)
        }
        val expected = Page(
            data = listOf(
                ProjectsItem(ios, emptyList()),
                ProjectsItem(web, emptyList())
            ),
            prevKey = null,
            nextKey = null
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = 2,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }

    @Test
    fun `load with projects after range`() {
        runBlocking {
            createProject(android.name)
            createProject(cli.name)
            createProject(ios.name)
            createProject(web.name)
        }
        val expected = Page(
            data = listOf(
                ProjectsItem(android, emptyList()),
                ProjectsItem(cli, emptyList())
            ),
            prevKey = null,
            nextKey = 2
        )

        val actual = runBlocking {
            pagingSource.load(
                Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected, actual)
    }
}
