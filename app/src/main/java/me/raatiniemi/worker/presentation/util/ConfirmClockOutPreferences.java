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

package me.raatiniemi.worker.presentation.util;

public interface ConfirmClockOutPreferences {
    /**
     * Check if clock out require confirmation.
     *
     * @return 'true' if clock out require confirmation, otherwise 'false'.
     */
    boolean shouldConfirmClockOut();

    /**
     * Set the preference for clock out confirmation.
     *
     * @param newValue 'true' if clock out requires confirmation, otherwise 'false'.
     */
    void setConfirmClockOut(boolean newValue);
}
