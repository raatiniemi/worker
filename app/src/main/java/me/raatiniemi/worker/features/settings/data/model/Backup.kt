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

package me.raatiniemi.worker.features.settings.data.model

import me.raatiniemi.worker.WorkerApplication
import java.io.File
import java.util.*

class Backup(private val directory: File?) {
    private val timestamp: Long?
        get() {
            directory ?: return null

            val timestamp = directory.name.replaceFirst(
                    WorkerApplication.STORAGE_BACKUP_DIRECTORY_PATTERN.toRegex(),
                    "$1"
            )

            return timestamp.toLong()
        }

    val date: Date?
        get() {
            val timestamp = timestamp ?: return null

            return Date(timestamp)
        }
}
