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

package me.raatiniemi.worker.domain.model

data class TimeIntervalBuilder(
    var id: Long = 1,
    var projectId: Long = 1,
    var start: Milliseconds? = null,
    var stop: Milliseconds? = null,
    var isRegistered: Boolean = false
) {
    fun build(): TimeInterval {
        return TimeInterval(
            id = id,
            projectId = projectId,
            start = requireNotNull(start),
            stop = stop,
            isRegistered = isRegistered
        )
    }
}

fun timeInterval(configure: TimeIntervalBuilder.() -> Unit): TimeInterval {
    val builder = TimeIntervalBuilder()
    builder.configure()

    return builder.build()
}
