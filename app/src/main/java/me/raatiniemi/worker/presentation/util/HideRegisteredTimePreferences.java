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

package me.raatiniemi.worker.presentation.util;

public interface HideRegisteredTimePreferences {
    /**
     * Check if the registered time should be hidden.
     *
     * @return 'true' if registered time should be hidden, otherwise 'false'.
     */
    boolean shouldHideRegisteredTime();

    /**
     * Set the preference indicating whether the registered time should be hidden.
     *
     * @param newValue 'true' if registered should be hidden, otherwise 'false'.
     */
    void setHideRegisteredTime(boolean newValue);
}
