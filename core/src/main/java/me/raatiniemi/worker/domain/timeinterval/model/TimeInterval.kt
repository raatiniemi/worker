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

package me.raatiniemi.worker.domain.timeinterval.model

import me.raatiniemi.worker.domain.project.model.ProjectId
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.usecase.ClockOutBeforeClockInException

/**
 * Represent a time interval registered to a project.
 */
sealed class TimeInterval {
    abstract val id: TimeIntervalId
    abstract val projectId: ProjectId
    abstract val start: Milliseconds

    data class Active internal constructor(
        override val id: TimeIntervalId,
        override val projectId: ProjectId,
        override val start: Milliseconds
    ) : TimeInterval() {
        fun clockOut(stop: Milliseconds): TimeInterval = Inactive(
            id = id,
            projectId = projectId,
            start = start,
            stop = stop
        )
    }

    data class Inactive internal constructor(
        override val id: TimeIntervalId,
        override val projectId: ProjectId,
        override val start: Milliseconds,
        val stop: Milliseconds
    ) : TimeInterval() {
        init {
            if (stop < start) {
                throw ClockOutBeforeClockInException()
            }
        }
    }

    data class Registered internal constructor(
        override val id: TimeIntervalId,
        override val projectId: ProjectId,
        override val start: Milliseconds,
        val stop: Milliseconds
    ) : TimeInterval() {
        init {
            if (stop < start) {
                throw ClockOutBeforeClockInException()
            }
        }
    }

    data class Builder internal constructor(
        var id: TimeIntervalId? = null,
        var start: Milliseconds? = null,
        var stop: Milliseconds? = null,
        var isRegistered: Boolean = false
    )
}
