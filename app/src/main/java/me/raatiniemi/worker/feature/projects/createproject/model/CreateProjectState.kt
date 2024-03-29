/*
 * Copyright (C) 2022 Tobias Raatiniemi
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

package me.raatiniemi.worker.feature.projects.createproject.model

import androidx.compose.runtime.Stable
import me.raatiniemi.worker.feature.shared.model.Error

@Stable
internal data class CreateProjectState(
    val name: String = "",
    val error: Error? = null
)

internal fun emptyCreateProjectState(): CreateProjectState {
    return CreateProjectState()
}

internal fun isError(state: CreateProjectState): Boolean {
    return state.error != null
}

internal fun isValid(state: CreateProjectState): Boolean {
    return !isError(state) && state.name.isNotBlank()
}
