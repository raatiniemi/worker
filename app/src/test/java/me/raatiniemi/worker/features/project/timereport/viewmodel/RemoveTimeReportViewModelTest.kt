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

package me.raatiniemi.worker.features.project.timereport.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import me.raatiniemi.worker.domain.interactor.RemoveTime
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.model.TimeReportItem
import me.raatiniemi.worker.domain.model.timeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository
import me.raatiniemi.worker.features.project.timereport.model.TimeReportAdapterResult
import me.raatiniemi.worker.features.project.timereport.model.TimeReportViewActions
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RemoveTimeReportViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val repository = TimeIntervalInMemoryRepository()

    private lateinit var removeTime: RemoveTime
    private lateinit var vm: RemoveTimeReportViewModel

    @Before
    fun setUp() {
        removeTime = RemoveTime(repository)
        vm = RemoveTimeReportViewModel(removeTime)
    }

    @Test
    fun `remove with single item`() = runBlocking {
        val expected = emptyList<TimeInterval>()
        val project = Project(1, "Project name #1")
        repository.add(timeInterval { })
        val timeInterval = timeInterval { id = 1 }
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem(timeInterval))
        )

        vm.remove(results)

        val actual = repository.findAll(project, 0)
        assertEquals(expected, actual)
        vm.viewActions.observeForever {
            assertEquals(TimeReportViewActions.RemoveRegistered(results), it)
        }
    }

    @Test
    fun `remove with multiple items`() = runBlocking {
        val expected = emptyList<TimeInterval>()
        val project = Project(1, "Project name #1")
        repository.add(timeInterval { })
        repository.add(timeInterval { })
        val results = listOf(
                TimeReportAdapterResult(0, 0, TimeReportItem(timeInterval { id = 1 })),
                TimeReportAdapterResult(0, 1, TimeReportItem(timeInterval { id = 2 }))
        )

        vm.remove(results)

        val actual = repository.findAll(project, 0)
        assertEquals(expected, actual)
        vm.viewActions.observeForever {
            assertEquals(TimeReportViewActions.RemoveRegistered(results.reversed()), it)
        }
    }
}
