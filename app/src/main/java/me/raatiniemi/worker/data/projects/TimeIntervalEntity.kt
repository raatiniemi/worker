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
import me.raatiniemi.worker.domain.model.*
import me.raatiniemi.worker.domain.project.model.ProjectId

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
) {
    fun toTimeInterval() = timeInterval(ProjectId(projectId)) { builder ->
        builder.id = TimeIntervalId(id)
        builder.start = Milliseconds(startInMilliseconds)
        builder.stop = stopInMilliseconds.takeUnless { it == 0L }
            ?.let { Milliseconds(it) }
        builder.isRegistered = registered == 1L
    }
}

internal fun NewTimeInterval.toEntity() = TimeIntervalEntity(
    id = 0,
    projectId = projectId.value,
    startInMilliseconds = start.value,
    stopInMilliseconds = 0,
    registered = 0
)

internal fun TimeInterval.toEntity() = TimeIntervalEntity(
    id = id.value,
    projectId = projectId.value,
    startInMilliseconds = start.value,
    stopInMilliseconds = when (this) {
        is TimeInterval.Active -> 0
        is TimeInterval.Inactive -> stop.value
        is TimeInterval.Registered -> stop.value
    },
    registered = if (this is TimeInterval.Registered) {
        1
    } else {
        0
    }
)
