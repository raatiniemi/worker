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
import me.raatiniemi.worker.domain.time.milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.NewTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
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

        return elapsedTimeInMilliseconds > 1.days
    }

    private suspend fun clockOut(
        active: TimeInterval.Active,
        stop: Milliseconds
    ): TimeInterval.Inactive {
        if (isOnSameDay(active.start, stop)) {
            return save(active, stop)
        }

        val startOfNextDay = calculateStartOfNextDay(active)
        save(active, startOfNextDay - 1.milliseconds)

        val timeInterval = timeIntervals.add(
            NewTimeInterval(active.projectId, startOfNextDay)
        )
        return clockOut(timeInterval, stop)
    }

    private fun isOnSameDay(start: Milliseconds, stop: Milliseconds): Boolean {
        return ChronoUnit.DAYS.between(localDate(start), localDate(stop)) == 0L
    }

    private fun localDate(milliseconds: Milliseconds): LocalDate? {
        return Instant.ofEpochMilli(milliseconds.value)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    private suspend fun save(
        active: TimeInterval.Active,
        milliseconds: Milliseconds
    ): TimeInterval.Inactive {
        val inactive = TimeInterval.Inactive(
            id = active.id,
            projectId = active.projectId,
            start = active.start,
            stop = milliseconds
        )
        return inactive.also {
            timeIntervals.update(it)
        }
    }

    private fun calculateStartOfNextDay(active: TimeInterval.Active): Milliseconds {
        return Instant.ofEpochMilli(active.start.value)
            .atZone(ZoneId.systemDefault())
            .truncatedTo(ChronoUnit.DAYS)
            .toInstant()
            .let { Milliseconds(it.toEpochMilli()) + 1.days }
    }
}
