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

package me.raatiniemi.worker.data.service.ongoing;

import android.content.Intent;

import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.exception.ActiveProjectException;
import me.raatiniemi.worker.domain.interactor.ClockIn;
import me.raatiniemi.worker.domain.interactor.GetProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.features.shared.view.notification.ErrorNotification;
import me.raatiniemi.worker.features.shared.view.notification.PauseNotification;
import timber.log.Timber;

public class ResumeService extends OngoingService {
    public ResumeService() {
        super("ResumeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long projectId = getProjectId(intent);

        try {
            clockInProjectNow(projectId);

            updateUserInterface(projectId);

            if (isOngoingNotificationEnabled()) {
                GetProject getProject = buildGetProjectUseCase();
                Project project = getProject.execute(projectId);

                sendPauseNotification(project);
                return;
            }

            dismissResumeNotification(projectId);
        } catch (Exception e) {
            Timber.w(e, "Unable to resume project");

            sendErrorNotification(projectId);
        }
    }

    private void clockInProjectNow(long projectId) {
        try {
            ClockIn clockIn = buildClockInUseCase();
            clockIn.execute(projectId, new Date());
        } catch (ActiveProjectException e) {
            Timber.e(e, "Resume service called with active project");
        }
    }

    ClockIn buildClockInUseCase() {
        return new ClockIn(getTimeIntervalRepository());
    }

    GetProject buildGetProjectUseCase() {
        return new GetProject(getProjectRepository());
    }

    private void sendPauseNotification(Project project) {
        sendNotification(
                project.getId(),
                PauseNotification.build(this, project, isOngoingNotificationChronometerEnabled())
        );
    }

    private void dismissResumeNotification(long projectId) {
        dismissNotification(projectId);
    }

    private void sendErrorNotification(long projectId) {
        sendNotification(
                projectId,
                ErrorNotification.buildOngoing(
                        this,
                        getString(R.string.error_notification_resume_title),
                        getString(R.string.error_notification_resume_message)
                )
        );
    }
}
