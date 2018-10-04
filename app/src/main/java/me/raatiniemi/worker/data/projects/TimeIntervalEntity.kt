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

package me.raatiniemi.worker.data.projects

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

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
data class TimeIntervalEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        val id: Long = 0,
        @ColumnInfo(name = "project_id")
        val projectId: Long,
        @ColumnInfo(name = "start_in_milliseconds")
        val startInMilliseconds: Long,
        @ColumnInfo(name = "stop_in_milliseconds")
        val stopInMilliseconds: Long = 0,
        val registered: Long = 0
)
