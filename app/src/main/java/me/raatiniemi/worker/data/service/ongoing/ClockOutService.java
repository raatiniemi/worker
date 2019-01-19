/*
 * Copyright (C) 2018 Tobias Raatiniemi
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

package me.raatiniemi.worker.data.service.ongoing;

import android.content.Intent;

import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.exception.InactiveProjectException;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.features.shared.view.notification.ErrorNotification;
import timber.log.Timber;

public class ClockOutService extends OngoingService {
    public ClockOutService() {
        super("ClockOutService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long projectId = getProjectId(intent);

        try {
            clockOutProjectNow(projectId);

            dismissPauseNotification(projectId);
            updateUserInterface(projectId);
        } catch (Exception e) {
            Timber.w(e, "Unable to clock out project");

            sendErrorNotification(projectId);
        }
    }

    private void clockOutProjectNow(long projectId) {
        try {
            ClockOut clockOut = buildClockOutUseCase();
            clockOut.invoke(projectId, new Date());
        } catch (InactiveProjectException e) {
            Timber.e(e, "Clock out service called with inactive project");
        }
    }

    ClockOut buildClockOutUseCase() {
        return new ClockOut(getTimeIntervalRepository());
    }

    private void dismissPauseNotification(long projectId) {
        dismissNotification(projectId);
    }

    private void sendErrorNotification(long projectId) {
        sendNotification(
                projectId,
                ErrorNotification.buildOngoing(
                        this,
                        getString(R.string.error_notification_clock_out_title),
                        getString(R.string.error_notification_clock_out_message)
                )
        );
    }
}
