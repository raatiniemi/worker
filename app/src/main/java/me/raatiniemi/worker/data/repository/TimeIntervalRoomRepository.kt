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

package me.raatiniemi.worker.data.repository

import me.raatiniemi.worker.data.projects.TimeIntervalDao
import me.raatiniemi.worker.data.projects.toEntity
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import me.raatiniemi.worker.util.Optional

internal class TimeIntervalRoomRepository(private val timeIntervals: TimeIntervalDao) : TimeIntervalRepository {
    override fun findAll(project: Project, milliseconds: Long): List<TimeInterval> {
        return timeIntervals.findAll(projectId = project.id, startInMilliseconds = milliseconds)
                .map { it.toTimeInterval() }
                .toList()
    }

    override fun findById(id: Long): Optional<TimeInterval> {
        val entity = timeIntervals.find(id) ?: return Optional.empty()

        return Optional.ofNullable(entity.toTimeInterval())
    }

    override fun findActiveByProjectId(projectId: Long): Optional<TimeInterval> {
        val entity = timeIntervals.findActiveTime(projectId) ?: return Optional.empty()

        return Optional.of(entity.toTimeInterval())
    }

    override fun add(timeInterval: TimeInterval): Optional<TimeInterval> {
        val id = timeIntervals.add(timeInterval.toEntity())

        return findById(id)
    }

    override fun update(timeInterval: TimeInterval): Optional<TimeInterval> {
        val entity = timeInterval.toEntity()
        timeIntervals.update(listOf(entity))

        return findById(entity.id)
    }

    override fun update(timeIntervals: List<TimeInterval>): List<TimeInterval> {
        val entities = timeIntervals.map { it.toEntity() }.toList()
        this.timeIntervals.update(entities)

        return timeIntervals.mapNotNull { it.id }
                .mapNotNull { this.timeIntervals.find(it) }
                .map { it.toTimeInterval() }
                .toList()
    }

    override fun remove(id: Long) {
        val entity = timeIntervals.find(id) ?: return

        timeIntervals.remove(listOf(entity))
    }

    override fun remove(timeIntervals: List<TimeInterval>) {
        val entities = timeIntervals.map { it.toEntity() }.toList()

        this.timeIntervals.remove(entities)
    }
}
