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

package me.raatiniemi.worker.features.projects.view;

import androidx.annotation.NonNull;
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult;

/**
 * Interface for project actions.
 */
interface OnProjectActionListener {
    /**
     * Toggle the clock activity change.
     *
     * @param result Project to change the clock activity.
     */
    void onClockActivityToggle(@NonNull ProjectsItemAdapterResult result);

    /**
     * Toggle the clock activity change, with date and time.
     *
     * @param result Project to change the clock activity.
     */
    void onClockActivityAt(@NonNull ProjectsItemAdapterResult result);

    /**
     * Handle project delete action from user.
     *
     * @param result Project to delete.
     */
    void onDelete(@NonNull ProjectsItemAdapterResult result);
}
