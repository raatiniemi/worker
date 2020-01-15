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

package me.raatiniemi.worker.data.room.entity.project

import androidx.room.*

@Dao
internal interface ProjectDao {
    @Query("SELECT COUNT(*) FROM projects")
    suspend fun count(): Int

    @Query("SELECT * FROM projects ORDER BY name ASC LIMIT :position, :pageSize")
    suspend fun findAll(position: Int, pageSize: Int): List<ProjectEntity>

    @Query("SELECT * FROM projects ORDER BY name ASC")
    suspend fun findAll(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE UPPER(name) = UPPER(:name) LIMIT 1")
    suspend fun findByName(name: String): ProjectEntity?

    @Query("SELECT * FROM projects WHERE _id = :id LIMIT 1")
    suspend fun findById(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun add(project: ProjectEntity): Long

    @Delete
    suspend fun remove(project: ProjectEntity)
}
