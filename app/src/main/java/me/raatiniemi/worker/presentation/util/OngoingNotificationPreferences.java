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

public interface OngoingNotificationPreferences {
    /**
     * Check if ongoing notification is enabled.
     *
     * @return 'true' if ongoing notification is enabled, otherwise 'false'.
     */
    boolean isOngoingNotificationEnabled();

    /**
     * Enable ongoing notification.
     */
    void enableOngoingNotification();

    /**
     * Disable ongoing notification.
     */
    void disableOngoingNotification();

    /**
     * Check if the ongoing notification chronometer is enabled.
     *
     * @return 'true' if the ongoing notification chronometer is enabled, otherwise 'false'.
     */
    boolean isOngoingNotificationChronometerEnabled();

    /**
     * Enable the ongoing notification chronometer.
     */
    void enableOngoingNotificationChronometer();

    /**
     * Disable the ongoing notification chronometer.
     */
    void disableOngoingNotificationChronometer();
}
