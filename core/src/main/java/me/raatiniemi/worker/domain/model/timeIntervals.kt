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

package me.raatiniemi.worker.domain.model

fun timeInterval(
    projectId: ProjectId,
    configure: (TimeInterval.Builder) -> Unit
): TimeInterval {
    val builder = TimeInterval.Builder()
    configure(builder)

    val id = builder.id ?: throw MissingTimeIntervalIdException()
    val start = builder.start ?: throw MissingTimeIntervalStartException()

    return TimeInterval.Default(
        id = id,
        projectId = projectId,
        start = start,
        stop = builder.stop,
        isRegistered = builder.isRegistered
    )
}

fun timeInterval(timeInterval: TimeInterval, configure: (TimeInterval.Builder) -> Unit) =
    timeInterval(timeInterval.projectId) {
        it.id = timeInterval.id
        it.start = timeInterval.start
        it.stop = timeInterval.stop
        it.isRegistered = timeInterval.isRegistered

        configure(it)
    }

fun isActive(timeInterval: TimeInterval) = timeInterval.stop == null

fun calculateTime(timeInterval: TimeInterval): Milliseconds {
    if (isActive(timeInterval)) {
        return Milliseconds.empty
    }

    val stop = timeInterval.stop ?: Milliseconds.empty
    return calculateInterval(timeInterval, stop)
}

fun calculateInterval(
    timeInterval: TimeInterval,
    stopForActive: Milliseconds = Milliseconds.now
): Milliseconds {
    val stop = timeInterval.stop
    return if (stop == null) {
        stopForActive - timeInterval.start
    } else {
        stop - timeInterval.start
    }
}
