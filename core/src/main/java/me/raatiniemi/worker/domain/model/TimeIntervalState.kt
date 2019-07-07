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

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException

/**
 * Represent a time interval registered to a project.
 */
sealed class TimeIntervalState {
    abstract val id: TimeIntervalId
    abstract val projectId: ProjectId
    abstract val start: Milliseconds
    abstract val stop: Milliseconds?
    abstract val isRegistered: Boolean
}

data class TimeInterval internal constructor(
    override val id: TimeIntervalId,
    override val projectId: ProjectId,
    override val start: Milliseconds,
    override val stop: Milliseconds? = null,
    override val isRegistered: Boolean = false
) : TimeIntervalState() {
    init {
        if (stop != null && stop < start) {
            throw ClockOutBeforeClockInException()
        }
    }
}

data class TimeIntervalStateBuilder internal constructor(
    var id: TimeIntervalId? = null,
    var projectId: ProjectId? = null,
    var start: Milliseconds? = null,
    var stop: Milliseconds? = null,
    var isRegistered: Boolean = false
)

fun timeInterval(configure: (TimeIntervalStateBuilder) -> Unit): TimeInterval {
    val builder = TimeIntervalStateBuilder()
    configure(builder)

    return TimeInterval(
        id = requireNotNull(builder.id),
        projectId = requireNotNull(builder.projectId),
        start = requireNotNull(builder.start),
        stop = builder.stop,
        isRegistered = builder.isRegistered
    )
}
