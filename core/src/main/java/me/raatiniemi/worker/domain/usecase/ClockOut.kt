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

package me.raatiniemi.worker.domain.usecase

import me.raatiniemi.worker.domain.exception.InactiveProjectException
import me.raatiniemi.worker.domain.model.Milliseconds
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import java.util.*

/**
 * Use case for clocking out.
 */
class ClockOut(private val repository: TimeIntervalRepository) {
    operator fun invoke(project: Project, date: Date): TimeInterval.Inactive {
        val active = findActiveTimeInterval(project)

        return clockOut(active, date)
    }

    private fun findActiveTimeInterval(project: Project): TimeInterval.Active {
        return repository.findActiveByProjectId(project.id)
            ?: throw InactiveProjectException()
    }

    private fun clockOut(active: TimeInterval.Active, date: Date): TimeInterval.Inactive {
        val inactive = active.clockOut(stop = Milliseconds(date.time))

        return when (val timeInterval = repository.update(inactive)) {
            is TimeInterval.Inactive -> timeInterval
            else -> throw InvalidStateForTimeIntervalException()
        }
    }
}
