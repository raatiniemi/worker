/*
 * Copyright (C) 2021 Tobias Raatiniemi
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

package me.raatiniemi.worker.domain.project.model

import me.raatiniemi.worker.domain.project.usecase.InvalidProjectNameException

fun projectName(value: String?): ProjectName {
    if (value.isNullOrBlank()) {
        throw InvalidProjectNameException()
    }

    return ProjectName(value)
}

fun isValid(name: String?): Boolean = !name.isNullOrEmpty()
