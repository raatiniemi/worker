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

package me.raatiniemi.worker.domain.timeinterval.usecase

import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.time.days
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import kotlin.math.abs

/**
 * Use case for clocking out.
 */
class ClockOut(private val timeIntervals: TimeIntervalRepository) {
    suspend operator fun invoke(project: Project, milliseconds: Milliseconds): TimeInterval.Inactive {
        val active = findActiveTimeInterval(project)
        if (isElapsedPastAllowed(milliseconds, active.start)) {
            throw ElapsedTimePastAllowedException()
        }

        return clockOut(active, milliseconds)
    }

    private suspend fun findActiveTimeInterval(project: Project): TimeInterval.Active {
        return timeIntervals.findActiveByProjectId(project.id)
            ?: throw InactiveProjectException()
    }

    private fun isElapsedPastAllowed(milliseconds: Milliseconds, active: Milliseconds): Boolean {
        val elapsedTime = milliseconds - active
        val elapsedTimeInMilliseconds = abs(elapsedTime.value)

        return elapsedTimeInMilliseconds >= 1.days
    }

    private suspend fun clockOut(
        active: TimeInterval.Active,
        milliseconds: Milliseconds
    ): TimeInterval.Inactive {
        val inactive = active.clockOut(stop = milliseconds)

        return when (val timeInterval = timeIntervals.update(inactive)) {
            is TimeInterval.Inactive -> timeInterval
            else -> throw InvalidStateForTimeIntervalException()
        }
    }
}
