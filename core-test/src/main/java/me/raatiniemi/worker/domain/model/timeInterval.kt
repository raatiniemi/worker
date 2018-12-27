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
        var id: Long? = null,
        var projectId: Long = 1,
        var startInMilliseconds: Long = 0,
        var stopInMilliseconds: Long = 0,
        var isRegistered: Boolean = false
)

fun timeInterval(configure: TimeIntervalBuilder.() -> Unit): TimeInterval {
    val builder = TimeIntervalBuilder()
    builder.configure()

    return TimeInterval(
            id = builder.id,
            projectId = builder.projectId,
            startInMilliseconds = builder.startInMilliseconds,
            stopInMilliseconds = builder.stopInMilliseconds,
            isRegistered = builder.isRegistered
    )
}
