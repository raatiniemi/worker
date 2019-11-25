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

private const val SOURCE_NAME = "source"
private const val COUNT_NAME = "count"

internal sealed class EventParameter {
    abstract val key: String
    abstract val value: String

    data class Source(override val key: String, override val value: String) : EventParameter() {
        constructor(source: EventSource) : this(SOURCE_NAME, source.name)
    }

    data class Count(override val key: String, override val value: String) : EventParameter() {
        constructor(value: Int) : this(COUNT_NAME, value.toString())
    }
}
