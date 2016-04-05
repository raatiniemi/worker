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

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.WorkerContract;
import me.raatiniemi.worker.data.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.repository.ProjectResolverRepository;
import me.raatiniemi.worker.data.repository.TimeResolverRepository;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.domain.interactor.GetProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.notification.ErrorNotification;
import me.raatiniemi.worker.util.Worker;

public class ClockOutService extends IntentService {
    private static final String TAG = "ClockOutService";

    public ClockOutService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long projectId = getProjectId(intent);

        try {
            ClockOut clockOut = new ClockOut(getTimeRepository());
            clockOut.execute(projectId, new Date());

            GetProject getProject = new GetProject(getProjectRepository());
            Project project = getProject.execute(projectId);

            dismissPauseNotification(project);
            updateUserInterface(projectId);
        } catch (Exception e) {
            Log.w(TAG, "Unable to clock out project: " + e.getMessage());

            sendErrorNotification(projectId);
        }
    }

    private TimeRepository getTimeRepository() {
        return new TimeResolverRepository(
                getContentResolver(),
                new TimeCursorMapper(),
                new TimeContentValuesMapper()
        );
    }

    private ProjectRepository getProjectRepository() {
        return new ProjectResolverRepository(
                getContentResolver(),
                new ProjectCursorMapper(),
                new ProjectContentValuesMapper()
        );
    }

    private long getProjectId(Intent intent) {
        String itemId = WorkerContract.ProjectContract.getItemId(intent.getData());
        long projectId = Long.valueOf(itemId);
        if (0 == projectId) {
            throw new IllegalArgumentException("Unable to extract project id from URI");
        }

        return projectId;
    }

    private void dismissPauseNotification(Project project) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(
                String.valueOf(project.getId()),
                Worker.NOTIFICATION_ON_GOING_ID
        );
    }

    private void updateUserInterface(long projectId) {
        EventBus eventBus = EventBus.getDefault();
        eventBus.post(new OngoingNotificationActionEvent(projectId));
    }

    private void sendErrorNotification(long projectId) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(
                String.valueOf(projectId),
                Worker.NOTIFICATION_ON_GOING_ID,
                ErrorNotification.build(
                        this,
                        getString(R.string.error_notification_clock_out_title),
                        getString(R.string.error_notification_clock_out_message)
                )
        );
    }
}
