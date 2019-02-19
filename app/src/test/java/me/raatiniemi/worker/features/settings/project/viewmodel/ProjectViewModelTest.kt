/*
 * Copyright (C) 2019 Tobias Raatiniemi
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

package me.raatiniemi.worker.features.settings.project.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.features.settings.project.model.ProjectViewActions
import me.raatiniemi.worker.util.AppKeys
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import me.raatiniemi.worker.util.KeyValueStore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectViewModelTest {
    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private val keyValueStore: KeyValueStore = InMemoryKeyValueStore()

    private lateinit var vm: ProjectViewModel

    @Before
    fun setUp() {
        vm = ProjectViewModel(keyValueStore)
    }

    @Test
    fun `change time summary starting point with current starting point`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)

        vm.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.MONTH.rawValue)

        assertEquals(TimeIntervalStartingPoint.MONTH.rawValue, keyValueStore.int(AppKeys.TIME_SUMMARY.rawValue))
    }

    @Test
    fun `change time summary starting point with invalid starting point`() {
        vm.changeTimeSummaryStartingPoint(-1)

        vm.viewActions.observeForever {
            assertEquals(ProjectViewActions.ShowUnableToChangeTimeSummaryStartingPointErrorMessage, it)
        }
    }

    @Test
    fun `change time summary starting point from month to week`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.MONTH.rawValue)

        vm.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.WEEK.rawValue)

        vm.viewActions.observeForever {
            assertEquals(ProjectViewActions.ShowTimeSummaryStartingPointChangedToWeek, it)
        }
    }

    @Test
    fun `change time summary starting point from week to month`() {
        keyValueStore.set(AppKeys.TIME_SUMMARY.rawValue, TimeIntervalStartingPoint.WEEK.rawValue)

        vm.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.MONTH.rawValue)

        vm.viewActions.observeForever {
            assertEquals(ProjectViewActions.ShowTimeSummaryStartingPointChangedToMonth, it)
        }
    }
}
