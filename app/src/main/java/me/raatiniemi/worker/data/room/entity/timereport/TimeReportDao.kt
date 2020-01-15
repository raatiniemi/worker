/*
 * Copyright (C) 2020 Tobias Raatiniemi
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

package me.raatiniemi.worker.data.room.entity.timereport

import androidx.room.Dao
import androidx.room.Query

@Dao
internal interface TimeReportDao {
    @Query(
        """SELECT COUNT(*)
            FROM (SELECT _id
                FROM time_intervals
                WHERE project_id = :projectId
                GROUP BY date(date(start_in_milliseconds / 1000, 'unixepoch', 'localtime'), '-6 days', 'weekday 1', 'localtime'))"""
    )
    suspend fun countWeeks(projectId: Long): Int

    @Query(
        """SELECT COUNT(*)
            FROM (SELECT _id
                FROM time_intervals
                WHERE project_id = :projectId
                    AND registered = 0
                GROUP BY date(date(start_in_milliseconds / 1000, 'unixepoch', 'localtime'), '-6 days', 'weekday 1', 'localtime'))"""
    )
    suspend fun countNotRegisteredWeeks(projectId: Long): Int

    @Query(
        """SELECT
            MIN(start_in_milliseconds) AS dateInMilliseconds,
            GROUP_CONCAT(_id) as ids
            FROM time_intervals
            WHERE project_id = :projectId
            GROUP BY date(date(start_in_milliseconds / 1000, 'unixepoch', 'localtime'), '-6 days', 'weekday 1', 'localtime')
            ORDER BY start_in_milliseconds DESC, stop_in_milliseconds DESC
            LIMIT :position, :pageSize"""
    )
    suspend fun findWeeks(projectId: Long, position: Int, pageSize: Int): List<TimeReportQueryGroup>

    @Query(
        """SELECT
            MIN(start_in_milliseconds) AS dateInMilliseconds,
            GROUP_CONCAT(_id) as ids
            FROM time_intervals
            WHERE project_id = :projectId
                AND registered = 0
            GROUP BY date(date(start_in_milliseconds / 1000, 'unixepoch', 'localtime'), '-6 days', 'weekday 1', 'localtime')
            ORDER BY start_in_milliseconds DESC, stop_in_milliseconds DESC
            LIMIT :position, :pageSize"""
    )
    suspend fun findNotRegisteredWeeks(
        projectId: Long,
        position: Int,
        pageSize: Int
    ): List<TimeReportQueryGroup>
}
