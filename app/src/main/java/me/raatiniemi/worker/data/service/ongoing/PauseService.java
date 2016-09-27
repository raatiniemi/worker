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

package me.raatiniemi.worker.data.service.ongoing;

import android.content.Intent;
import android.util.Log;

import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InactiveProjectException;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.domain.interactor.GetProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.view.notification.ErrorNotification;
import me.raatiniemi.worker.presentation.view.notification.ResumeNotification;

public class PauseService extends OngoingService {
    private static final String TAG = "PauseService";

    public PauseService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long projectId = getProjectId(intent);

        try {
            clockOutProjectNow(projectId);

            updateUserInterface(projectId);

            if (isOngoingNotificationEnabled()) {
                GetProject getProject = buildGetProjectUseCase();
                Project project = getProject.execute(projectId);

                sendResumeNotification(project);
                return;
            }

            dismissPauseNotification(projectId);
        } catch (Exception e) {
            Log.w(TAG, "Unable to pause project", e);

            sendErrorNotification(projectId);
        }
    }

    private void clockOutProjectNow(long projectId) throws DomainException {
        try {
            ClockOut clockOut = buildClockOutUseCase();
            clockOut.execute(projectId, new Date());
        } catch (InactiveProjectException e) {
            Log.e(TAG, "Pause service called with inactive project", e);
        }
    }

    ClockOut buildClockOutUseCase() {
        return new ClockOut(getTimeRepository());
    }

    GetProject buildGetProjectUseCase() {
        return new GetProject(getProjectRepository());
    }

    private void sendResumeNotification(Project project) {
        sendNotification(
                project.getId(),
                ResumeNotification.build(this, project)
        );
    }

    private void dismissPauseNotification(long projectId) {
        dismissNotification(projectId);
    }

    private void sendErrorNotification(long projectId) {
        sendNotification(
                projectId,
                ErrorNotification.build(
                        this,
                        getString(R.string.error_notification_pause_title),
                        getString(R.string.error_notification_pause_message)
                )
        );
    }
}
