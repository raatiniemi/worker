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
    object OpenProject : Event(OPEN_PROJECT_NAME)
    object ToggleProject : Event(TOGGLE_PROJECT_NAME)

    companion object {
        private const val OPEN_PROJECT_NAME = "open_project"
        private const val TOGGLE_PROJECT_NAME = "toggle_project"
    }
}
