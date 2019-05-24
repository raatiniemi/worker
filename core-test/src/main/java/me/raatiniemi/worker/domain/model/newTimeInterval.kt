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

import java.util.*

data class NewTimeIntervalBuilder(
    var projectId: Long = 1,
    var start: Date = Date(),
    var stop: Date? = null,
    var isRegistered: Boolean = false
) {
    fun build(): NewTimeInterval {
        return NewTimeInterval(
            projectId = projectId,
            start = Milliseconds(start.time),
            stop = stop?.let { Milliseconds(it.time) },
            isRegistered = isRegistered
        )
    }
}

fun newTimeInterval(configure: NewTimeIntervalBuilder.() -> Unit): NewTimeInterval {
    val builder = NewTimeIntervalBuilder()
    builder.configure()

    return builder.build()
}

fun newTimeIntervalStartBefore(
    startingPoint: TimeIntervalStartingPoint,
    configure: NewTimeIntervalBuilder.() -> Unit
): NewTimeInterval {
    val builder = NewTimeIntervalBuilder()
    builder.configure()

    val startInMilliseconds = before(startingPoint)
    val stopInMilliseconds = startInMilliseconds + (builder.stop?.time ?: 0)

    return builder.let {
        it.start = Date(startInMilliseconds)
        it.stop = Date(stopInMilliseconds)

        it.build()
    }
}

fun newTimeIntervalStartAfter(
    startingPoint: TimeIntervalStartingPoint,
    configure: NewTimeIntervalBuilder.() -> Unit
): NewTimeInterval {
    val builder = NewTimeIntervalBuilder()
    builder.configure()

    val startInMilliseconds = after(startingPoint)
    val stopInMilliseconds = startInMilliseconds + (builder.stop?.time ?: 0)

    return builder.let {
        it.start = Date(startInMilliseconds)
        it.stop = Date(stopInMilliseconds)

        it.build()
    }
}
