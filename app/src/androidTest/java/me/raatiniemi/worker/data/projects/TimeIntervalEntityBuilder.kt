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

package me.raatiniemi.worker.data.projects

internal data class TimeIntervalEntityBuilder(
    var id: Long = 0,
    var projectId: Long = 1,
    var startInMilliseconds: Long = 1,
    var stopInMilliseconds: Long = 2,
    var registered: Boolean = false
)

internal fun timeIntervalEntity(
    configure: (TimeIntervalEntityBuilder.() -> Unit)? = null
): TimeIntervalEntity {
    val builder = TimeIntervalEntityBuilder()
    configure?.let { builder.it() }

    return TimeIntervalEntity(
        id = builder.id,
        projectId = builder.projectId,
        startInMilliseconds = builder.startInMilliseconds,
        stopInMilliseconds = builder.stopInMilliseconds,
        registered = if (builder.registered) {
            1L
        } else {
            0L
        }
    )
}
