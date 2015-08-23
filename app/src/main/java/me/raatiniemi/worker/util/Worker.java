/*
 * Copyright (C) 2015 Worker Project
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

package me.raatiniemi.worker.util;

/**
 * Stores application constants.
 */
public final class Worker {
    /**
     * Package for the application.
     */
    public static final String PACKAGE = "me.raatiniemi.worker";

    /**
     * Name of the application database.
     */
    public static final String DATABASE_NAME = "worker";

    /**
     * Version of the application database structure.
     */
    public static final int DATABASE_VERSION = 2;

    /**
     * Id for the notifications from the DataIntentService-class.
     */
    public static final int NOTIFICATION_DATA_INTENT_SERVICE_ID = 1;

    /**
     * Prefix for backup directories.
     */
    public static final String STORAGE_BACKUP_DIRECTORY_PREFIX = "backup-";

    /**
     * Pattern for the backup directories.
     */
    public static final String STORAGE_BACKUP_DIRECTORY_PATTERN
            = Worker.STORAGE_BACKUP_DIRECTORY_PREFIX + "(\\d+)";

    /**
     * Intent action for restarting the application.
     */
    public static final String INTENT_ACTION_RESTART = "action_restart";
}
