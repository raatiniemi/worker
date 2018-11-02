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
import java.util.*

/**
 * Represent a project.
 */
class Project private constructor(val id: Long?, val name: String) {
    init {
        if (!isValid(name)) {
            throw InvalidProjectNameException()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is Project) {
            return false
        }

        return id == other.id && name == other.name
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + Objects.hashCode(id)
        result = 31 * result + name.hashCode()

        return result
    }

    class Builder internal constructor(private val projectName: String) {
        private var id: Long? = null

        fun id(id: Long?): Builder {
            this.id = id
            return this
        }

        fun build(): Project {
            return Project(id, projectName)
        }
    }

    companion object {
        @JvmStatic
        fun builder(projectName: String): Builder {
            return Builder(projectName)
        }
    }
}
