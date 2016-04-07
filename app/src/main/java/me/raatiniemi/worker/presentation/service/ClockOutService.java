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

package me.raatiniemi.worker.presentation.service;

import android.content.Intent;
import android.util.Log;

import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.presentation.notification.ErrorNotification;

public class ClockOutService extends OngoingService {
    private static final String TAG = "ClockOutService";

    public ClockOutService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long projectId = getProjectId(intent);

        try {
            ClockOut clockOut = buildClockOutUseCase();
            clockOut.execute(projectId, new Date());

            dismissPauseNotification(projectId);
            updateUserInterface(projectId);
        } catch (Exception e) {
            Log.w(TAG, "Unable to clock out project: " + e.getMessage());

            sendErrorNotification(projectId);
        }
    }

    protected ClockOut buildClockOutUseCase() {
        return new ClockOut(getTimeRepository());
    }

    private void dismissPauseNotification(long projectId) {
        dismissNotification(projectId);
    }

    private void sendErrorNotification(long projectId) {
        sendNotification(
                projectId,
                ErrorNotification.build(
                        this,
                        getString(R.string.error_notification_clock_out_title),
                        getString(R.string.error_notification_clock_out_message)
                )
        );
    }
}
