/*
 * Copyright (C) 2016 Worker Project
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

package me.raatiniemi.worker.presentation.projects.view;

import android.support.annotation.NonNull;

import me.raatiniemi.worker.presentation.projects.model.ProjectsModel;

/**
 * Interface for project actions.
 */
interface OnProjectActionListener {
    /**
     * Toggle the clock activity change.
     *
     * @param project Project to change the clock activity.
     */
    void onClockActivityToggle(@NonNull ProjectsModel project);

    /**
     * Toggle the clock activity change, with date and time.
     *
     * @param project Project to change the clock activity.
     */
    void onClockActivityAt(@NonNull ProjectsModel project);

    /**
     * Handle project delete action from user.
     *
     * @param project Project to delete.
     */
    void onDelete(@NonNull ProjectsModel project);
}
