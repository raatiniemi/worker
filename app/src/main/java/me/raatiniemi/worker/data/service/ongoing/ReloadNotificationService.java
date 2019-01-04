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

import android.content.Context;
import android.content.Intent;

import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.interactor.IsProjectActive;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.features.shared.view.notification.PauseNotification;
import timber.log.Timber;

public class ReloadNotificationService extends OngoingService {
    public ReloadNotificationService() {
        super("ReloadNotificationService");
    }

    public static void startServiceWithContext(Context context) {
        Intent intent = new Intent(context, ReloadNotificationService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (isOngoingNotificationDisabled()) {
            return;
        }

        GetProjects getProjects = buildGetProjectsUseCase();
        IsProjectActive isProjectActive = buildIsProjectActiveUseCase();
        try {
            List<Project> projects = getProjects.invoke();
            if (projects.isEmpty()) {
                return;
            }

            //noinspection Convert2streamapi
            for (Project project : projects) {
                if (isProjectActive.execute(project.getId())) {
                    sendPauseNotification(project);
                }
            }
        } catch (DomainException e) {
            Timber.e(e, "Unable to reload notifications");
        }
    }

    private boolean isOngoingNotificationDisabled() {
        return !isOngoingNotificationEnabled();
    }

    GetProjects buildGetProjectsUseCase() {
        return new GetProjects(getProjectRepository());
    }

    private IsProjectActive buildIsProjectActiveUseCase() {
        return new IsProjectActive(getTimeIntervalRepository());
    }

    private void sendPauseNotification(Project project) {
        sendNotification(
                project.getId(),
                PauseNotification.build(this, project, isOngoingNotificationChronometerEnabled())
        );
    }
}
