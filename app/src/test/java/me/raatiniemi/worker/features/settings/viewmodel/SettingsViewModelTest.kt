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

package me.raatiniemi.worker.features.settings.viewmodel

import me.raatiniemi.worker.domain.configuration.InMemoryKeyValueStore
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SettingsViewModelTest {
    private val keyValueStore: KeyValueStore = InMemoryKeyValueStore()

    private lateinit var vm: SettingsViewModel

    @Before
    fun setUp() {
        vm = SettingsViewModel(keyValueStore)
    }

    // Confirm clock out

    @Test
    fun `confirm clock out with default value`() {
        val actual = vm.confirmClockOut

        assertTrue(actual)
    }

    @Test
    fun `confirm clock out when disabled`() {
        vm.confirmClockOut = false

        val actual = vm.confirmClockOut

        assertFalse(actual)
    }

    @Test
    fun `confirm clock out when enabled`() {
        vm.confirmClockOut = true

        val actual = vm.confirmClockOut

        assertTrue(actual)
    }
}
