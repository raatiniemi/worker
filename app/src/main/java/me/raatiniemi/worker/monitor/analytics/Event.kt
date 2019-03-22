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

package me.raatiniemi.worker.monitor.analytics

sealed class Event(val name: String, val parameters: Map<String, String> = emptyMap()) {
    object TapOpenProject : Event(TAP_OPEN_PROJECT_NAME)
    object TapToggleProject : Event(TAP_TOGGLE_PROJECT_NAME)
    object TapAtProject : Event(TAP_AT_PROJECT_NAME)
    object TapRemoveProject : Event(TAP_REMOVE_PROJECT_NAME)

    companion object {
        private const val TAP_OPEN_PROJECT_NAME = "tap_open_project"
        private const val TAP_TOGGLE_PROJECT_NAME = "tap_toggle_project"
        private const val TAP_AT_PROJECT_NAME = "tap_at_project"
        private const val TAP_REMOVE_PROJECT_NAME = "tap_remove_project"
    }
}
