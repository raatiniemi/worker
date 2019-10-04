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

import me.raatiniemi.worker.domain.configuration.InMemoryKeyValueStore
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectViewModelTest {
    private val keyValueStore: KeyValueStore = InMemoryKeyValueStore()

    private lateinit var vm: ProjectViewModel

    @Before
    fun setUp() {
        vm = ProjectViewModel(keyValueStore)
    }

    @Test
    fun `is ongoing notification enabled with default value`() {
        val actual = vm.ongoingNotificationEnabled

        assertTrue(actual)
    }

    @Test
    fun `disable ongoing notification`() {
        vm.ongoingNotificationEnabled = false

        val actual = vm.ongoingNotificationEnabled
        assertFalse(actual)
    }

    @Test
    fun `enable ongoing notification`() {
        vm.ongoingNotificationEnabled = false

        vm.ongoingNotificationEnabled = true

        val actual = vm.ongoingNotificationEnabled
        assertTrue(actual)
    }

    @Test
    fun `is ongoing notification chronometer enabled with ongoing notification disabled`() {
        vm.ongoingNotificationEnabled = false

        vm.ongoingNotificationChronometerEnabled = true

        val actual = vm.ongoingNotificationChronometerEnabled
        assertFalse(actual)
    }

    @Test
    fun `disable ongoing notification chronometer`() {
        vm.ongoingNotificationEnabled = true

        vm.ongoingNotificationChronometerEnabled = false

        val actual = vm.ongoingNotificationChronometerEnabled
        assertFalse(actual)
    }

    @Test
    fun `enable ongoing notification chronometer`() {
        vm.ongoingNotificationEnabled = true

        vm.ongoingNotificationChronometerEnabled = true

        val actual = vm.ongoingNotificationChronometerEnabled
        assertTrue(actual)
    }
}
