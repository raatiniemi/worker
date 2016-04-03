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

import me.raatiniemi.worker.data.WorkerContract;
import me.raatiniemi.worker.data.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.repository.ProjectResolverRepository;
import me.raatiniemi.worker.data.repository.TimeResolverRepository;
import me.raatiniemi.worker.domain.interactor.ClockIn;
import me.raatiniemi.worker.domain.interactor.GetProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.presentation.model.OnGoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.notification.PauseNotification;
import me.raatiniemi.worker.util.Settings;
import me.raatiniemi.worker.util.Worker;

public class ResumeService extends IntentService {
    private static final String TAG = "ResumeService";

    public ResumeService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            long projectId = getProjectId(intent);

            ClockIn clockIn = new ClockIn(getTimeRepository());
            clockIn.execute(projectId, new Date());

            GetProject getProject = new GetProject(getProjectRepository());
            Project project = getProject.execute(projectId);

            updateUserInterface(projectId);

            if (Settings.isOngoingNotificationEnabled(this)) {
                sendPauseNotification(project);
                return;
            }

            dismissResumeNotification();
        } catch (Exception e) {
            Log.w(TAG, "Unable to resume project: " + e.getMessage());
        }
    }

    private long getProjectId(Intent intent) {
        String itemId = WorkerContract.ProjectContract.getItemId(intent.getData());
        long projectId = Long.valueOf(itemId);
        if (0 == projectId) {
            throw new IllegalArgumentException("Unable to extract project id from URI");
        }

        return projectId;
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

    private void dismissResumeNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Worker.NOTIFICATION_ON_GOING_ID);
    }

    private void sendPauseNotification(Project project) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(
                Worker.NOTIFICATION_ON_GOING_ID,
                PauseNotification.build(this, project)
        );
    }

    private void updateUserInterface(long projectId) {
        EventBus eventBus = EventBus.getDefault();
        eventBus.post(new OnGoingNotificationActionEvent(projectId));
    }
}
