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

package me.raatiniemi.worker.data.room.repository

import me.raatiniemi.worker.data.room.entity.timeinterval.TimeIntervalDao
import me.raatiniemi.worker.data.room.entity.timeinterval.timeInterval
import me.raatiniemi.worker.data.room.entity.timeinterval.timeIntervalEntity
import me.raatiniemi.worker.domain.project.model.Project
import me.raatiniemi.worker.domain.project.model.ProjectId
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.NewTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.repository.InvalidActiveTimeIntervalException
import me.raatiniemi.worker.domain.timeinterval.repository.TimeIntervalRepository

internal class TimeIntervalRoomRepository(private val timeIntervals: TimeIntervalDao) :
    TimeIntervalRepository {
    override fun findAll(project: Project, milliseconds: Milliseconds): List<TimeInterval> {
        return timeIntervals.findAll(project.id.value, milliseconds.value)
            .map(::timeInterval)
            .toList()
    }

    override fun findById(id: TimeIntervalId): TimeInterval? {
        return timeIntervals.find(id.value)
            ?.let(::timeInterval)
    }

    override fun findActiveByProjectId(projectId: ProjectId): TimeInterval.Active? {
        val entity = timeIntervals.findActiveTime(projectId.value) ?: return null

        return when (val timeInterval = timeInterval(entity)) {
            is TimeInterval.Active -> timeInterval
            else -> throw InvalidActiveTimeIntervalException()
        }
    }

    override suspend fun add(newTimeInterval: NewTimeInterval): TimeInterval.Active {
        val id = timeIntervals.add(timeIntervalEntity(newTimeInterval))

        val timeInterval = findById(TimeIntervalId(id))
        if (timeInterval is TimeInterval.Active) {
            return timeInterval
        }

        throw UnableToFindNewTimeIntervalException()
    }

    override fun update(timeInterval: TimeInterval): TimeInterval? {
        return timeIntervalEntity(timeInterval)
            .also { timeIntervals.update(listOf(it)) }
            .run { findById(TimeIntervalId(id)) }
    }

    override fun update(timeIntervals: List<TimeInterval>): List<TimeInterval> {
        val entities = timeIntervals.map(::timeIntervalEntity).toList()
        this.timeIntervals.update(entities)

        return timeIntervals.map { it.id }
            .mapNotNull { this.timeIntervals.find(it.value) }
            .map(::timeInterval)
            .toList()
    }

    override fun remove(id: TimeIntervalId) {
        val entity = timeIntervals.find(id.value) ?: return

        timeIntervals.remove(listOf(entity))
    }

    override fun remove(timeIntervals: List<TimeInterval>) {
        val entities = timeIntervals.map(::timeIntervalEntity).toList()

        this.timeIntervals.remove(entities)
    }
}
