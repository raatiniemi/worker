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

package me.raatiniemi.worker.service.data;

import android.content.Context;

import de.greenrobot.event.EventBus;

/**
 * Backup strategy for storage device.
 */
public class StorageBackupStrategy implements BackupStrategy {
    /**
     * Application context.
     */
    private final Context mContext;

    /**
     * Event bus used for notification.
     */
    private final EventBus mEventBus;

    /**
     * Constructor.
     *
     * @param context  Application context.
     * @param eventBus Event bus used for notification.
     */
    public StorageBackupStrategy(Context context, EventBus eventBus) {
        mContext = context;
        mEventBus = eventBus;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void execute() {
    }
}
