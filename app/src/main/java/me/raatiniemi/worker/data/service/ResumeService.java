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

package me.raatiniemi.worker.data.service;

import android.content.Intent;
import android.util.Log;

import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.interactor.ClockIn;
import me.raatiniemi.worker.domain.interactor.GetProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.notification.ErrorNotification;
import me.raatiniemi.worker.presentation.notification.PauseNotification;

public class ResumeService extends OngoingService {
    private static final String TAG = "ResumeService";

    public ResumeService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long projectId = getProjectId(intent);

        try {
            ClockIn clockIn = buildClockInUseCase();
            clockIn.execute(projectId, new Date());

            updateUserInterface(projectId);

            if (isOngoingNotificationEnabled()) {
                GetProject getProject = buildGetProjectUseCase();
                Project project = getProject.execute(projectId);

                sendPauseNotification(project);
                return;
            }

            dismissResumeNotification(projectId);
        } catch (Exception e) {
            Log.w(TAG, "Unable to resume project", e);

            sendErrorNotification(projectId);
        }
    }

    ClockIn buildClockInUseCase() {
        return new ClockIn(getTimeRepository());
    }

    GetProject buildGetProjectUseCase() {
        return new GetProject(getProjectRepository());
    }

    private void sendPauseNotification(Project project) {
        sendNotification(
                project.getId(),
                PauseNotification.build(this, project)
        );
    }

    private void dismissResumeNotification(long projectId) {
        dismissNotification(projectId);
    }

    private void sendErrorNotification(long projectId) {
        sendNotification(
                projectId,
                ErrorNotification.build(
                        this,
                        getString(R.string.error_notification_resume_title),
                        getString(R.string.error_notification_resume_message)
                )
        );
    }
}
