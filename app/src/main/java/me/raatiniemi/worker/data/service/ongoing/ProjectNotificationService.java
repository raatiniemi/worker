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

import android.content.Context;
import android.content.Intent;

import me.raatiniemi.worker.domain.interactor.GetProject;
import me.raatiniemi.worker.domain.interactor.IsProjectActive;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.features.shared.view.notification.PauseNotification;
import me.raatiniemi.worker.util.OngoingUriCommunicator;
import timber.log.Timber;

public class ProjectNotificationService extends OngoingService {
    public ProjectNotificationService() {
        super("ProjectNotificationService");
    }

    public static void startServiceWithContext(Context context, Project project) {
        Intent intent = new Intent(context, ProjectNotificationService.class);
        intent.setData(OngoingUriCommunicator.createWith(project.getId()));
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long projectId = getProjectId(intent);

        try {
            if (!isOngoingNotificationEnabled()) {
                dismissNotification(projectId);
                return;
            }

            IsProjectActive isProjectActive = buildIsProjectActiveUseCase();
            if (isProjectActive.execute(projectId)) {
                GetProject getProject = buildGetProjectUseCase();
                Project project = getProject.execute(projectId);

                sendNotification(
                        project.getId(),
                        PauseNotification.build(this, project, isOngoingNotificationChronometerEnabled())
                );
                return;
            }

            dismissNotification(projectId);
        } catch (Exception e) {
            Timber.w(e, "Unable to pause project");
        }
    }

    IsProjectActive buildIsProjectActiveUseCase() {
        return new IsProjectActive(getTimeIntervalRepository());
    }

    GetProject buildGetProjectUseCase() {
        return new GetProject(getProjectRepository());
    }
}
