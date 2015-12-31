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

package me.raatiniemi.worker.service.data;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import de.greenrobot.event.EventBus;

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
    private static boolean sRunning = false;

    private final EventBus mEventBus;

    /**
     * Constructor.
     */
    public DataIntentService() {
        super(TAG);

        mEventBus = EventBus.getDefault();
    }

    /**
     * Check whether a data operation is running.
     *
     * @return True if data operation is running, otherwise false.
     */
    public static boolean isRunning() {
        return sRunning;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // If an operation is already running, we should not allow another to
            // start. We wouldn't want backup and restore running simultaneously.
            if (DataIntentService.isRunning()) {
                throw new IllegalStateException("Data operation is already running");
            }

            DataStrategy strategy;

            // Check which data strategy we should use.
            String action = intent.getAction();
            switch (action) {
                case INTENT_ACTION_BACKUP:
                    strategy = new BackupStrategy(getApplicationContext(), mEventBus);
                    break;
                case INTENT_ACTION_RESTORE:
                    strategy = new RestoreStrategy(getApplicationContext(), mEventBus);
                    break;
                default:
                    throw new IllegalStateException("Received unknown action: " + action);
            }

            // Execute the data operation.
            sRunning = true;
            strategy.execute();
            sRunning = false;
        } catch (IllegalStateException e) {
            // TODO: Post event `DataOperationFailure`.
            Log.w(TAG, e.getMessage());
        } catch (Exception e) {
            // TODO: Post event `DataOperationFailure`.
            Log.w(TAG, e.getMessage());

            // In case the data operation throw an exception we need to reset
            // the running flag, otherwise we might prevent actions to run.
            sRunning = false;
        }
    }
}
