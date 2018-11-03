/*
 * Copyright (C) 2017 Worker Project
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
import java.util.*

/**
 * Represent a time interval registered to a project.
 */
data class TimeInterval(
        val id: Long?,
        val projectId: Long,
        val startInMilliseconds: Long,
        val stopInMilliseconds: Long,
        val isRegistered: Boolean
) {
    val isActive: Boolean
        get() = 0L == stopInMilliseconds

    val time: Long
        get() = if (isActive) {
            0L
        } else calculateInterval(stopInMilliseconds)

    val interval: Long
        get() = if (isActive) {
            calculateInterval(Date().time)
        } else calculateInterval(stopInMilliseconds)

    init {
        if (stopInMilliseconds > 0) {
            if (stopInMilliseconds < startInMilliseconds) {
                throw ClockOutBeforeClockInException()
            }
        }
    }

    fun markAsRegistered(): TimeInterval {
        return if (isRegistered) {
            this
        } else copy(isRegistered = true)
    }

    fun unmarkRegistered(): TimeInterval {
        return if (!isRegistered) {
            this
        } else copy(isRegistered = false)
    }

    /**
     * Set the clock out timestamp at given date.
     *
     * @param date Date at which to clock out.
     * @throws NullPointerException If date argument is null.
     */
    fun clockOutAt(date: Date): TimeInterval {
        return copy(stopInMilliseconds = date.time)
    }

    private fun calculateInterval(stopInMilliseconds: Long): Long {
        return stopInMilliseconds - startInMilliseconds
    }

    class Builder(internal val projectId: Long) {
        internal var id: Long? = null
        internal var startInMilliseconds = 0L
        internal var stopInMilliseconds = 0L
        internal var registered = false

        fun id(id: Long?): Builder {
            this.id = id
            return this
        }

        fun startInMilliseconds(startInMilliseconds: Long): Builder {
            this.startInMilliseconds = startInMilliseconds
            return this
        }

        fun stopInMilliseconds(stopInMilliseconds: Long): Builder {
            this.stopInMilliseconds = stopInMilliseconds
            return this
        }

        fun register(): Builder {
            registered = true
            return this
        }

        fun build(): TimeInterval {
            return TimeInterval(id, projectId, startInMilliseconds, stopInMilliseconds, registered)
        }
    }

    companion object {
        @JvmStatic
        fun builder(projectId: Long): Builder {
            return Builder(projectId)
        }
    }
}
