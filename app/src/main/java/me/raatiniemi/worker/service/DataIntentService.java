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

package me.raatiniemi.worker.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import de.greenrobot.event.EventBus;
import me.raatiniemi.worker.service.data.BackupStrategy;
import me.raatiniemi.worker.service.data.RestoreStrategy;

/**
 * Service for running data operations.
 */
public class DataIntentService extends IntentService {
    /**
     * Intent action for running the backup operation.
     */
    public static final String INTENT_ACTION_BACKUP = "backup";

    /**
     * Intent action for running the restore operation.
     */
    public static final String INTENT_ACTION_RESTORE = "restore";

    /**
     * Tag for logging.
     */
    private static final String TAG = "DataIntentService";

    /**
     * Type of running data operation.
     */
    private static RUNNING sRunning = RUNNING.NONE;

    private final EventBus mEventBus;

    /**
     * Constructor.
     */
    public DataIntentService() {
        super(TAG);

        mEventBus = EventBus.getDefault();
    }

    /**
     * Get the type of data operation that is running. If the RUNNING.NONE is
     * returned no data operation is currently running.
     *
     * @return Type of running data operation.
     */
    public static RUNNING getRunning() {
        return sRunning;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // If an operation is already running, we should not allow another to
        // start. We wouldn't want backup and restore running simultaneously.
        if (RUNNING.NONE != getRunning()) {
            Log.w(TAG, "Data operation is already running, exiting");
            return;
        }

        String action = intent.getAction();
        switch (action) {
            case INTENT_ACTION_BACKUP:
                sRunning = RUNNING.BACKUP;
                BackupStrategy backupStrategy = new BackupStrategy(getApplicationContext(), mEventBus);
                backupStrategy.execute();
                break;
            case INTENT_ACTION_RESTORE:
                sRunning = RUNNING.RESTORE;
                RestoreStrategy restoreStrategy = new RestoreStrategy(getApplicationContext(), mEventBus);
                restoreStrategy.execute();
                break;
            default:
                Log.w(TAG, "Received unknown action: " + action);
                break;
        }
        sRunning = RUNNING.NONE;
    }

    /**
     * Available types of runnable data operations.
     */
    public enum RUNNING {
        NONE,
        BACKUP,
        RESTORE
    }
}
