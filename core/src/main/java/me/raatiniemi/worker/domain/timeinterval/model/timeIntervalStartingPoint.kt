/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

import me.raatiniemi.worker.domain.configuration.AppKeys
import me.raatiniemi.worker.domain.configuration.KeyValueStore
import me.raatiniemi.worker.domain.timeinterval.usecase.InvalidStartingPointException

fun timeIntervalStartingPoint(keyValueStore: KeyValueStore): TimeIntervalStartingPoint {
    val defaultValue = TimeIntervalStartingPoint.MONTH
    val startingPoint = keyValueStore.int(
        AppKeys.TIME_SUMMARY,
        defaultValue.rawValue
    )

    return try {
        timeIntervalStartingPoint(startingPoint)
    } catch (e: InvalidStartingPointException) {
        defaultValue
    }
}

fun timeIntervalStartingPoint(startingPoint: Int): TimeIntervalStartingPoint {
    return when (startingPoint) {
        TimeIntervalStartingPoint.DAY.rawValue -> TimeIntervalStartingPoint.DAY
        TimeIntervalStartingPoint.WEEK.rawValue -> TimeIntervalStartingPoint.WEEK
        TimeIntervalStartingPoint.MONTH.rawValue -> TimeIntervalStartingPoint.MONTH
        else -> throw InvalidStartingPointException(
            "Starting point '$startingPoint' is not valid"
        )
    }
}
