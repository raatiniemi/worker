/*
 * Copyright (C) 2017 Worker Project
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

package me.raatiniemi.worker.domain.model

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException
import me.raatiniemi.worker.domain.validator.ProjectName.isValid

data class Project(val id: Long?, val name: String) {
    init {
        if (!isValid(name)) {
            throw InvalidProjectNameException()
        }
    }

    companion object {
        @JvmStatic
        fun from(id: Long?, projectName: String): Project {
            return Project(id, projectName)
        }

        @JvmStatic
        fun from(projectName: String): Project {
            return Project(null, projectName)
        }
    }
}
