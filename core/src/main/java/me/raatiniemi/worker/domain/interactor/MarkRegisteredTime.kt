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

package me.raatiniemi.worker.domain.interactor

import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository

/**
 * Use case for marking time as registered.
 *
 * TODO: Rename use case to something more descriptive.
 * The use case can also mark time as not registered.
 */
open class MarkRegisteredTime(private val repository: TimeIntervalRepository) {
    private fun collectTimeToUpdate(timeIntervals: List<TimeInterval>): List<TimeInterval> {
        val shouldMarkAsRegistered = shouldMarkAsRegistered(timeIntervals)

        return timeIntervals.map {
            if (shouldMarkAsRegistered) {
                it.markAsRegistered()
            } else {
                it.unmarkRegistered()
            }
        }
    }

    private fun shouldMarkAsRegistered(timeIntervals: List<TimeInterval>): Boolean {
        val timeInterval = timeIntervals.firstOrNull() ?: return false

        return !timeInterval.isRegistered
    }

    operator fun invoke(timeIntervals: List<TimeInterval>) =
        repository.update(collectTimeToUpdate(timeIntervals))
}
