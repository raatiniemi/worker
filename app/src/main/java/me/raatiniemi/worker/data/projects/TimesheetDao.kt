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

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TimesheetDao {
    @Query("""SELECT
        MIN(start_in_milliseconds) AS dateInMilliseconds,
        GROUP_CONCAT(_id) as ids
        FROM time_intervals
        WHERE project_id = :projectId
        GROUP BY strftime('%Y%m%d', start_in_milliseconds / 1000, 'unixepoch')
        ORDER BY start_in_milliseconds DESC, stop_in_milliseconds DESC
        LIMIT :offset, :maxResult""")
    fun findAll(projectId: Long, offset: Int, maxResult: Int): List<TimesheetDay>

    @Query("""SELECT
        MIN(start_in_milliseconds) AS dateInMilliseconds,
        GROUP_CONCAT(_id) as ids
        FROM time_intervals
        WHERE project_id = :projectId
            AND registered = 0
        GROUP BY strftime('%Y%m%d', start_in_milliseconds / 1000, 'unixepoch')
        ORDER BY start_in_milliseconds DESC, stop_in_milliseconds DESC
        LIMIT :offset, :maxResult""")
    fun findAllUnregistered(projectId: Long, offset: Int, maxResult: Int): List<TimesheetDay>
}
