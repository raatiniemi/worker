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

import me.raatiniemi.worker.domain.date.hours
import me.raatiniemi.worker.domain.model.Milliseconds
import java.lang.Math.abs

fun before(startingPoint: TimeIntervalStartingPoint, offset: Long = 1.hours): Milliseconds {
    return startingPoint.calculateMilliseconds() - abs(offset)
}

fun after(startingPoint: TimeIntervalStartingPoint, offset: Long = 1.hours): Milliseconds {
    return startingPoint.calculateMilliseconds() + abs(offset)
}
