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

package me.raatiniemi.worker.features.projects.view

import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult

/**
 * Interface for project actions.
 */
internal interface OnProjectActionListener {
    /**
     * Toggle the clock activity change.
     *
     * @param result Project to change the clock activity.
     */
    fun onClockActivityToggle(result: ProjectsItemAdapterResult)

    /**
     * Toggle the clock activity change, with date and time.
     *
     * @param result Project to change the clock activity.
     */
    fun onClockActivityAt(result: ProjectsItemAdapterResult)

    /**
     * Handle project delete action from user.
     *
     * @param result Project to delete.
     */
    fun onDelete(result: ProjectsItemAdapterResult)
}
