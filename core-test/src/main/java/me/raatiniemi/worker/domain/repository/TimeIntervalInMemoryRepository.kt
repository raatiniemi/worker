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

package me.raatiniemi.worker.domain.repository

import me.raatiniemi.worker.domain.model.*
import java.util.concurrent.atomic.AtomicLong

class TimeIntervalInMemoryRepository : TimeIntervalRepository {
    private val incrementedId = AtomicLong()
    private val timeIntervals = mutableListOf<TimeInterval>()

    override fun findAll(project: Project, milliseconds: Milliseconds): List<TimeInterval> {
        return timeIntervals.filter {
            it.projectId == project.id && it.start >= milliseconds
        }
    }

    override fun findById(id: TimeIntervalId): TimeInterval? =
        timeIntervals.firstOrNull { it.id == id.value }

    override fun findActiveByProjectId(projectId: ProjectId): TimeInterval? =
        timeIntervals.firstOrNull { it.projectId == projectId && isActive(it) }

    override fun add(newTimeInterval: NewTimeInterval): TimeInterval {
        val timeInterval = TimeInterval(
            id = incrementedId.incrementAndGet(),
            projectId = newTimeInterval.projectId,
            start = newTimeInterval.start,
            stop = newTimeInterval.stop,
            isRegistered = newTimeInterval.isRegistered
        )
        timeIntervals.add(timeInterval)
        return timeInterval
    }

    override fun update(timeInterval: TimeInterval) =
        findById(TimeIntervalId(timeInterval.id))?.let {
            val index = timeIntervals.indexOf(it)
            timeIntervals[index] = timeInterval

            timeInterval
        }

    override fun update(timeIntervals: List<TimeInterval>) =
        timeIntervals.mapNotNull { update(it) }

    override fun remove(id: TimeIntervalId) {
        timeIntervals.removeIf { it.id == id.value }
    }

    override fun remove(timeIntervals: List<TimeInterval>) {
        timeIntervals.map { TimeIntervalId(it.id) }
            .forEach { remove(it) }
    }
}
