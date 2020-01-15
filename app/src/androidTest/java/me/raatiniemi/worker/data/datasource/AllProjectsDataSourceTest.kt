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

package me.raatiniemi.worker.data.datasource

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.project.model.*
import me.raatiniemi.worker.domain.project.usecase.CreateProject
import me.raatiniemi.worker.koin.androidTestKoinModules
import me.raatiniemi.worker.koin.module.inMemorySharedTest
import me.raatiniemi.worker.util.CoroutineTestRule
import me.raatiniemi.worker.util.TestCoroutineDispatchProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.koin.test.inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AllProjectsDataSourceTest : AutoCloseKoinTest() {
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val createProject by inject<CreateProject>()

    private lateinit var dataSource: AllProjectsDataSource

    @Before
    fun setUp() {
        stopKoin()
        startKoin {
            loadKoinModules(androidTestKoinModules + inMemorySharedTest)
        }

        val factory = AllProjectsDataSource.Factory(
            scope = coroutineTestRule.testScope,
            dispatcherProvider = TestCoroutineDispatchProvider(coroutineTestRule.testDispatcher),
            countProjects = get(),
            findProjects = get()
        )
        dataSource = factory.create()
    }

    @Test
    fun loadInitial_withoutProjects() = runBlocking {
        val expected = PositionalDataSourceResult.Initial<Project>(emptyList(), 0)

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withProject() = runBlocking {
        val projects = listOf(
            createProject(android.name)
        )
        val expected = PositionalDataSourceResult.Initial(
            data = projects,
            position = 0,
            totalCount = projects.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withProjects() = runBlocking {
        val projects = listOf(
            createProject(android.name),
            createProject(cli.name),
            createProject(ios.name),
            createProject(web.name)
        )
        val expected = PositionalDataSourceResult.Initial(
            data = projects,
            position = 0,
            totalCount = projects.size
        )

        dataSource.loadInitial(loadInitialParams(), loadInitialCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadInitial_withProjectsBeyondPageSize() = runBlocking {
        val projects = listOf(
            createProject(android.name),
            createProject(cli.name),
            createProject(ios.name),
            createProject(web.name)
        )
        val expected = PositionalDataSourceResult.Initial(
            data = projects.take(2),
            position = 0,
            totalCount = projects.size
        )

        dataSource.loadInitial(
            loadInitialParams(requestedStartPosition = 0, requestedLoadSize = 2),
            loadInitialCallback {
                assertEquals(expected, it)
            }
        )
    }

    @Test
    fun loadInitial_withProjectsAndPosition() = runBlocking {
        val projects = listOf(
            createProject(android.name),
            createProject(cli.name),
            createProject(ios.name),
            createProject(web.name)
        )
        val expected = PositionalDataSourceResult.Initial(
            data = projects.drop(2),
            position = 2,
            totalCount = projects.size
        )

        dataSource.loadInitial(
            loadInitialParams(requestedStartPosition = 2, requestedLoadSize = 2),
            loadInitialCallback {
                assertEquals(expected, it)
            }
        )
    }

    @Test
    fun loadRange_withoutProjects() {
        val expected = PositionalDataSourceResult.Range<Project>(emptyList())

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withProject() = runBlocking {
        val projects = listOf(
            createProject(android.name)
        )
        val expected = PositionalDataSourceResult.Range(projects)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withProjects() = runBlocking {
        val projects = listOf(
            createProject(android.name),
            createProject(cli.name),
            createProject(ios.name),
            createProject(web.name)
        )
        val expected = PositionalDataSourceResult.Range(projects)

        dataSource.loadRange(loadRangeParams(), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withProjectsBeforePosition() = runBlocking {
        val projects = listOf(
            createProject(android.name),
            createProject(cli.name),
            createProject(ios.name),
            createProject(web.name)
        )
        val expected = PositionalDataSourceResult.Range(projects.drop(2))

        dataSource.loadRange(loadRangeParams(startPosition = 2), loadRangeCallback {
            assertEquals(expected, it)
        })
    }

    @Test
    fun loadRange_withProjectsBeyondPageSize() = runBlocking {
        val projects = listOf(
            createProject(android.name),
            createProject(cli.name),
            createProject(ios.name),
            createProject(web.name)
        )
        val expected = PositionalDataSourceResult.Range(
            projects.take(2)
        )

        dataSource.loadRange(loadRangeParams(loadSize = 2), loadRangeCallback {
            assertEquals(expected, it)
        })
    }
}
