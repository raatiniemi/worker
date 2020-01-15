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

package me.raatiniemi.worker.domain.timeinterval.repository

import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.ProjectId
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.NewTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval
import java.util.concurrent.atomic.AtomicLong

class TimeIntervalInMemoryRepository : TimeIntervalRepository {
    private val incrementedId = AtomicLong()
    private val timeIntervals = mutableListOf<TimeInterval>()

    override suspend fun findAll(project: Project, milliseconds: Milliseconds): List<TimeInterval> {
        return timeIntervals.filter {
            it.projectId == project.id && (it.start >= milliseconds || it is TimeInterval.Active)
        }
    }

    override suspend fun findById(id: TimeIntervalId): TimeInterval? {
        return timeIntervals.firstOrNull { it.id == id }
    }

    override suspend fun findActiveByProjectId(projectId: ProjectId): TimeInterval.Active? {
        return timeIntervals.filter { it.projectId == projectId }
            .filterIsInstance<TimeInterval.Active>()
            .firstOrNull()
    }

    override suspend fun add(newTimeInterval: NewTimeInterval): TimeInterval.Active {
        val timeInterval = timeInterval(newTimeInterval.projectId) { builder ->
            builder.id = TimeIntervalId(incrementedId.incrementAndGet())
            builder.start = newTimeInterval.start
        }
        if (timeInterval is TimeInterval.Active) {
            timeIntervals.add(timeInterval)
            return timeInterval
        }

        // This should never happen due to `timeInterval` always creating an
        // `TimeInterval.Active` when no `stop` have been supplied.
        throw InvalidActiveTimeIntervalException()
    }

    override suspend fun update(timeInterval: TimeInterval): TimeInterval? {
        return findById(timeInterval.id)?.let {
            val index = timeIntervals.indexOf(it)
            timeIntervals[index] = timeInterval

            timeInterval
        }
    }

    override suspend fun update(timeIntervals: List<TimeInterval>): List<TimeInterval> {
        return timeIntervals.mapNotNull { update(it) }
    }

    override suspend fun remove(id: TimeIntervalId) {
        timeIntervals.removeIf { it.id == id }
    }

    override suspend fun remove(timeIntervals: List<TimeInterval>) {
        timeIntervals.map { it.id }
            .forEach { remove(it) }
    }
}
