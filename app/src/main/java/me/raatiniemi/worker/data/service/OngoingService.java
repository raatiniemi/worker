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

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.data.WorkerContract;
import me.raatiniemi.worker.data.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.repository.ProjectResolverRepository;
import me.raatiniemi.worker.data.repository.TimeResolverRepository;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.util.Settings;

abstract class OngoingService extends IntentService {
    OngoingService(String name) {
        super(name);
    }

    private static String buildNotificationTag(long projectId) {
        return String.valueOf(projectId);
    }

    long getProjectId(Intent intent) {
        String itemId = WorkerContract.ProjectContract.getItemId(intent.getData());
        long projectId = Long.parseLong(itemId);
        if (0 == projectId) {
            throw new IllegalArgumentException("Unable to extract project id from URI");
        }

        return projectId;
    }

    TimeRepository getTimeRepository() {
        return new TimeResolverRepository(
                getContentResolver(),
                new TimeCursorMapper(),
                new TimeContentValuesMapper()
        );
    }

    ProjectRepository getProjectRepository() {
        return new ProjectResolverRepository(
                getContentResolver(),
                new ProjectCursorMapper(),
                new ProjectContentValuesMapper()
        );
    }

    void sendNotification(long projectId, Notification notification) {
        NotificationManager manager = getNotificationManager();
        manager.notify(
                buildNotificationTag(projectId),
                Worker.NOTIFICATION_ON_GOING_ID,
                notification
        );
    }

    void dismissNotification(long projectId) {
        NotificationManager manager = getNotificationManager();
        manager.cancel(
                buildNotificationTag(projectId),
                Worker.NOTIFICATION_ON_GOING_ID
        );
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    boolean isOngoingNotificationEnabled() {
        return Settings.isOngoingNotificationEnabled(this);
    }

    void updateUserInterface(long projectId) {
        getEventBus().post(
                new OngoingNotificationActionEvent(projectId)
        );
    }

    EventBus getEventBus() {
        return EventBus.getDefault();
    }
}
