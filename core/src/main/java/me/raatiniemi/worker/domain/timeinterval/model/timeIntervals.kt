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

import me.raatiniemi.worker.domain.model.Milliseconds
import me.raatiniemi.worker.domain.project.model.ProjectId

fun timeInterval(
    projectId: ProjectId,
    configure: (TimeInterval.Builder) -> Unit
): TimeInterval {
    val builder = TimeInterval.Builder()
    configure(builder)

    val id = builder.id ?: throw MissingIdForTimeIntervalException()
    val start = builder.start ?: throw MissingStartForTimeIntervalException()
    val stop = builder.stop

    if (stop == null) {
        if (builder.isRegistered) {
            throw MissingStopForRegisteredTimeIntervalException()
        }

        return TimeInterval.Active(
            id = id,
            projectId = projectId,
            start = start
        )
    }

    return if (builder.isRegistered) {
        TimeInterval.Registered(
            id = id,
            projectId = projectId,
            start = start,
            stop = stop
        )
    } else {
        TimeInterval.Inactive(
            id = id,
            projectId = projectId,
            start = start,
            stop = stop
        )
    }
}

fun timeInterval(timeInterval: TimeInterval, configure: (TimeInterval.Builder) -> Unit) =
    timeInterval(timeInterval.projectId) {
        it.id = timeInterval.id
        it.start = timeInterval.start
        it.stop = when (timeInterval) {
            is TimeInterval.Inactive -> timeInterval.stop
            is TimeInterval.Registered -> timeInterval.stop
            else -> null
        }
        it.isRegistered = timeInterval is TimeInterval.Registered

        configure(it)
    }

fun isActive(timeInterval: TimeInterval) = timeInterval is TimeInterval.Active

fun calculateTime(timeInterval: TimeInterval) = when (timeInterval) {
    is TimeInterval.Active -> Milliseconds.empty
    is TimeInterval.Inactive -> calculateInterval(timeInterval, timeInterval.stop)
    is TimeInterval.Registered -> calculateInterval(timeInterval, timeInterval.stop)
}

fun calculateInterval(
    timeInterval: TimeInterval,
    stopForActive: Milliseconds = Milliseconds.now
): Milliseconds {
    val stop = when (timeInterval) {
        is TimeInterval.Active -> stopForActive
        is TimeInterval.Inactive -> timeInterval.stop
        is TimeInterval.Registered -> timeInterval.stop
    }

    return calculateInterval(timeInterval.start, stop)
}

private fun calculateInterval(start: Milliseconds, stop: Milliseconds) = stop - start
