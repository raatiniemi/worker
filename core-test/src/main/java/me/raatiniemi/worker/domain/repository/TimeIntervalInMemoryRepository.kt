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

import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.util.Optional
import java.util.concurrent.atomic.AtomicLong

class TimeIntervalInMemoryRepository : TimeIntervalRepository {
    private val incrementedId = AtomicLong()
    private val timeIntervals = mutableListOf<TimeInterval>()

    override fun findAll(project: Project, milliseconds: Long): List<TimeInterval> {
        return timeIntervals.filter {
            it.projectId == project.id && it.startInMilliseconds >= milliseconds
        }
    }

    override fun findById(id: Long): Optional<TimeInterval> {
        val timeInterval = timeIntervals.firstOrNull { it.id == id }

        return Optional.ofNullable(timeInterval)
    }

    override fun findActiveByProjectId(projectId: Long): Optional<TimeInterval> {
        val timeInterval = timeIntervals.firstOrNull { it.projectId == projectId && it.isActive }

        return Optional.ofNullable(timeInterval)
    }

    override fun add(timeInterval: TimeInterval): Optional<TimeInterval> {
        val id = incrementedId.incrementAndGet()
        val value = timeInterval.copy(id = id)
        timeIntervals.add(value)

        return Optional.of(value)
    }

    override fun update(timeInterval: TimeInterval): Optional<TimeInterval> {
        val existingTimeInterval = timeIntervals.firstOrNull { it.id == timeInterval.id }
                ?: return Optional.empty()

        val index = timeIntervals.indexOf(existingTimeInterval)
        timeIntervals[index] = timeInterval

        return Optional.of(timeInterval)
    }

    override fun update(timeIntervals: List<TimeInterval>): List<TimeInterval> {
        return timeIntervals.filter { existingTimeInterval(it) }
                .map { update(it) }
                .filter { it.isPresent }
                .map { it.get() }
    }

    private fun existingTimeInterval(timeInterval: TimeInterval): Boolean {
        val id = timeInterval.id ?: return false

        return findById(id).isPresent
    }

    override fun remove(id: Long) {
        timeIntervals.removeIf { it.id == id }
    }

    override fun remove(timeIntervals: List<TimeInterval>) {
        timeIntervals.mapNotNull { it.id }
                .forEach { remove(it) }
    }
}
