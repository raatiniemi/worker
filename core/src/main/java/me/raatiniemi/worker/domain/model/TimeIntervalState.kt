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
sealed class TimeInterval {
    abstract val id: TimeIntervalId
    abstract val projectId: ProjectId
    abstract val start: Milliseconds
    abstract val stop: Milliseconds?
    abstract val isRegistered: Boolean

    abstract fun clockOut(stop: Milliseconds): TimeInterval

    data class Default internal constructor(
        override val id: TimeIntervalId,
        override val projectId: ProjectId,
        override val start: Milliseconds,
        override val stop: Milliseconds? = null,
        override val isRegistered: Boolean = false
    ) : TimeInterval() {
        init {
            if (stop != null && stop < start) {
                throw ClockOutBeforeClockInException()
            }
        }

        override fun clockOut(stop: Milliseconds): TimeInterval = Inactive(
            id = id,
            projectId = projectId,
            start = start,
            stop = stop,
            isRegistered = isRegistered
        )
    }

    data class Inactive internal constructor(
        override val id: TimeIntervalId,
        override val projectId: ProjectId,
        override val start: Milliseconds,
        override val stop: Milliseconds? = null,
        override val isRegistered: Boolean = false
    ) : TimeInterval() {
        init {
            if (stop != null && stop < start) {
                throw ClockOutBeforeClockInException()
            }
        }

        override fun clockOut(stop: Milliseconds): TimeInterval {
            throw UnsupportedOperationException()
        }
    }

    data class Registered internal constructor(
        override val id: TimeIntervalId,
        override val projectId: ProjectId,
        override val start: Milliseconds,
        override val stop: Milliseconds? = null,
        override val isRegistered: Boolean = true
    ) : TimeInterval() {
        init {
            if (stop != null && stop < start) {
                throw ClockOutBeforeClockInException()
            }
        }

        override fun clockOut(stop: Milliseconds): TimeInterval {
            throw UnsupportedOperationException()
        }
    }

    data class Builder internal constructor(
        var id: TimeIntervalId? = null,
        var start: Milliseconds? = null,
        var stop: Milliseconds? = null,
        var isRegistered: Boolean = false
    )
}
