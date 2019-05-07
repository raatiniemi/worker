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

package me.raatiniemi.worker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import me.raatiniemi.worker.data.projects.*

@Database(
    entities = [
        ProjectEntity::class,
        TimeIntervalEntity::class
    ],
    version = 3,
    exportSchema = true
)
internal abstract class Database : RoomDatabase() {
    abstract fun projects(): ProjectDao
    abstract fun timeIntervals(): TimeIntervalDao
    abstract fun timeReport(): TimeReportDao
}
