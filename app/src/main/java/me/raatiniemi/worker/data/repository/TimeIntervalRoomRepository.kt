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
import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository

internal class TimeIntervalRoomRepository(private val timeIntervals: TimeIntervalDao) :
    TimeIntervalRepository {
    override fun findAll(project: Project, milliseconds: Milliseconds): List<TimeInterval> {
        return timeIntervals.findAll(project.id.value, milliseconds.value)
            .map { it.toTimeInterval() }
            .toList()
    }

    override fun findById(id: TimeIntervalId): TimeInterval? {
        return timeIntervals.find(id.value)
            ?.run { toTimeInterval() }
    }

    override fun findActiveByProjectId(projectId: ProjectId): TimeInterval? {
        return timeIntervals.findActiveTime(projectId.value)
            ?.run { toTimeInterval() }
    }

    override fun add(newTimeInterval: NewTimeInterval): TimeInterval {
        val id = timeIntervals.add(newTimeInterval.toEntity())

        return findById(TimeIntervalId(id)) ?: throw UnableToFindNewTimeIntervalException()
    }

    override fun update(timeInterval: TimeInterval): TimeInterval? {
        return timeInterval.toEntity()
            .also { timeIntervals.update(listOf(it)) }
            .run { findById(TimeIntervalId(id)) }
    }

    override fun update(timeIntervals: List<TimeInterval>): List<TimeInterval> {
        val entities = timeIntervals.map { it.toEntity() }.toList()
        this.timeIntervals.update(entities)

        return timeIntervals.map { it.id }
            .mapNotNull { this.timeIntervals.find(it.value) }
            .map { it.toTimeInterval() }
            .toList()
    }

    override fun remove(id: TimeIntervalId) {
        val entity = timeIntervals.find(id.value) ?: return

        timeIntervals.remove(listOf(entity))
    }

    override fun remove(timeIntervals: List<TimeInterval>) {
        val entities = timeIntervals.map { it.toEntity() }.toList()

        this.timeIntervals.remove(entities)
    }
}
