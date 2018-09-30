/*
 * Copyright (C) 2018 Worker Project
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
import me.raatiniemi.worker.data.projects.TimeIntervalEntity
import me.raatiniemi.worker.domain.model.Project
import me.raatiniemi.worker.domain.model.TimeInterval
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository
import me.raatiniemi.worker.util.Optional

class TimeIntervalRoomRepository(private val timeIntervals: TimeIntervalDao) : TimeIntervalRepository {
    private fun transform(entity: TimeIntervalEntity) = TimeInterval(
            id = entity.id,
            projectId = entity.projectId,
            startInMilliseconds = entity.startInMilliseconds,
            stopInMilliseconds = entity.stopInMilliseconds,
            isRegistered = entity.registered == 1L
    )

    private fun transform(timeInterval: TimeInterval) = TimeIntervalEntity(
            id = timeInterval.id ?: 0,
            projectId = timeInterval.projectId,
            startInMilliseconds = timeInterval.startInMilliseconds,
            stopInMilliseconds = timeInterval.stopInMilliseconds,
            registered = if (timeInterval.isRegistered) {
                1
            } else {
                0
            }
    )

    override fun findAll(project: Project, milliseconds: Long): List<TimeInterval> {
        return timeIntervals.findAll(projectId = project.id!!, startInMilliseconds = milliseconds)
                .map { transform(it) }
                .toList()
    }

    override fun findById(id: Long): Optional<TimeInterval> {
        val entity = timeIntervals.find(id) ?: return Optional.empty()

        return Optional.ofNullable(transform(entity))
    }

    override fun findActiveByProjectId(projectId: Long): Optional<TimeInterval> {
        val entity = timeIntervals.findActiveTime(projectId) ?: return Optional.empty()

        return Optional.of(transform(entity))
    }

    override fun add(timeInterval: TimeInterval): Optional<TimeInterval> {
        val id = timeIntervals.add(transform(timeInterval))

        return findById(id)
    }

    override fun update(timeInterval: TimeInterval): Optional<TimeInterval> {
        val entity = transform(timeInterval)
        timeIntervals.update(listOf(entity))

        return findById(entity.id)
    }

    override fun update(timeIntervals: List<TimeInterval>): List<TimeInterval> {
        val entities = timeIntervals.map { transform(it) }.toList()
        this.timeIntervals.update(entities)

        return timeIntervals.mapNotNull { it.id }
                .mapNotNull { this.timeIntervals.find(it) }
                .map { transform(it) }
                .toList()
    }

    override fun remove(id: Long) {
        val entity = timeIntervals.find(id) ?: return

        timeIntervals.remove(listOf(entity))
    }

    override fun remove(timeIntervals: List<TimeInterval>) {
        val entities = timeIntervals.map { transform(it) }.toList()

        this.timeIntervals.remove(entities)
    }
}
