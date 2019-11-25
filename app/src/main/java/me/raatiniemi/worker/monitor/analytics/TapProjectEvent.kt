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

internal sealed class TapProjectEvent(override val value: String) : EventName {
    object Open : TapProjectEvent("tap_project_open")
    object Toggle : TapProjectEvent("tap_project_toggle")
    object At : TapProjectEvent("tap_project_at")
    object Remove : TapProjectEvent("tap_project_remove")
}
