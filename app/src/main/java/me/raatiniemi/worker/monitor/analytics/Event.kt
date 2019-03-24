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
    object TapProjectOpen : Event(TAP_PROJECT_OPEN_NAME)
    object TapProjectToggle : Event(TAP_PROJECT_TOGGLE_NAME)
    object TapProjectAt : Event(TAP_PROJECT_AT_NAME)
    object TapProjectRemove : Event(TAP_PROJECT_REMOVE_NAME)

    companion object {
        private const val TAP_PROJECT_OPEN_NAME = "tap_project_open"
        private const val TAP_PROJECT_TOGGLE_NAME = "tap_project_toggle"
        private const val TAP_PROJECT_AT_NAME = "tap_project_at"
        private const val TAP_PROJECT_REMOVE_NAME = "tap_project_remove"
    }
}
