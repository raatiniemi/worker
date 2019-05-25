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
data class TimeInterval(
    val id: Long,
    val projectId: Long,
    val start: Milliseconds,
    val stop: Milliseconds? = null,
    val isRegistered: Boolean = false
) {
    val isActive = null == stop

    val time: Long
        get() = if (isActive) {
            0L
        } else calculateInterval(stop ?: Milliseconds.empty).value

    init {
        if (stop != null) {
            if (stop < start) {
                throw ClockOutBeforeClockInException()
            }
        }
    }

    fun calculateInterval(stopForActive: Milliseconds = Milliseconds.now): Milliseconds {
        if (stop == null) {
            return stopForActive - start
        }

        return stop - start
    }
}
