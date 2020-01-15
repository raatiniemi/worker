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

package me.raatiniemi.worker.data.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration2To3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `projects` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)")
        database.execSQL("CREATE UNIQUE INDEX `index_projects_name` ON `projects` (`name`)")
        database.execSQL("INSERT INTO `projects` (`_id`, `name`) SELECT `_id`, `name` FROM `project`")
        database.execSQL("DROP TABLE `project`")

        database.execSQL("CREATE TABLE IF NOT EXISTS `time_intervals` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `project_id` INTEGER NOT NULL, `start_in_milliseconds` INTEGER NOT NULL, `stop_in_milliseconds` INTEGER NOT NULL, `registered` INTEGER NOT NULL, FOREIGN KEY(`project_id`) REFERENCES `projects`(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("CREATE INDEX `index_project_id` ON `time_intervals` (`project_id`)")
        database.execSQL("INSERT INTO `time_intervals` (`_id`, `project_id`, `start_in_milliseconds`, `stop_in_milliseconds`, `registered`) SELECT `_id`, `project_id`, `start`, `stop`, `registered` FROM `time`")
        database.execSQL("DROP TABLE `time`")
    }
}
