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

package me.raatiniemi.worker.data.projects

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import me.raatiniemi.worker.domain.project.model.ProjectId
import me.raatiniemi.worker.domain.time.Milliseconds
import me.raatiniemi.worker.domain.timeinterval.model.NewTimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeInterval
import me.raatiniemi.worker.domain.timeinterval.model.TimeIntervalId
import me.raatiniemi.worker.domain.timeinterval.model.timeInterval

@Entity(
    tableName = "time_intervals",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["_id"],
            childColumns = ["project_id"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(name = "index_project_id", value = ["project_id"])
    ]
)
internal data class TimeIntervalEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "project_id")
    val projectId: Long,
    @ColumnInfo(name = "start_in_milliseconds")
    val startInMilliseconds: Long,
    @ColumnInfo(name = "stop_in_milliseconds")
    val stopInMilliseconds: Long,
    val registered: Long
)

internal fun timeInterval(entity: TimeIntervalEntity): TimeInterval {
    return timeInterval(ProjectId(entity.projectId)) { builder ->
        builder.id = TimeIntervalId(entity.id)
        builder.start = Milliseconds(entity.startInMilliseconds)
        builder.stop = entity.stopInMilliseconds.takeUnless { it == 0L }
            ?.let { Milliseconds(it) }
        builder.isRegistered = entity.registered == 1L
    }
}

internal fun timeIntervalEntity(newTimeInterval: NewTimeInterval): TimeIntervalEntity {
    return TimeIntervalEntity(
        id = 0,
        projectId = newTimeInterval.projectId.value,
        startInMilliseconds = newTimeInterval.start.value,
        stopInMilliseconds = 0,
        registered = 0
    )
}

internal fun timeIntervalEntity(timeInterval: TimeInterval): TimeIntervalEntity {
    return TimeIntervalEntity(
        id = timeInterval.id.value,
        projectId = timeInterval.projectId.value,
        startInMilliseconds = timeInterval.start.value,
        stopInMilliseconds = when (timeInterval) {
            is TimeInterval.Active -> 0
            is TimeInterval.Inactive -> timeInterval.stop.value
            is TimeInterval.Registered -> timeInterval.stop.value
        },
        registered = if (timeInterval is TimeInterval.Registered) {
            1
        } else {
            0
        }
    )
}
