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

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import me.raatiniemi.worker.Preferences;
import me.raatiniemi.worker.WorkerApplication;
import me.raatiniemi.worker.data.Repositories;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;
import me.raatiniemi.worker.features.shared.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.util.KeyValueStore;
import me.raatiniemi.worker.util.Notifications;
import me.raatiniemi.worker.util.OngoingUriCommunicator;
import timber.log.Timber;

public abstract class OngoingService extends IntentService {
    private final Repositories repositories = new Repositories();
    private final ProjectRepository projectRepository = repositories.getProject();
    private final TimeIntervalRepository timeIntervalRepository = repositories.getTimeInterval();

    private final Preferences preferences = new Preferences();
    private final KeyValueStore keyValueStore = preferences.getKeyValueStore();

    OngoingService(String name) {
        super(name);
    }

    private static String buildNotificationTag(long projectId) {
        return String.valueOf(projectId);
    }

    long getProjectId(Intent intent) {
        long projectId = OngoingUriCommunicator.parseFrom(intent.getData());
        if (0 == projectId) {
            throw new IllegalArgumentException("Unable to extract project id from URI");
        }

        return projectId;
    }

    ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    TimeIntervalRepository getTimeIntervalRepository() {
        return timeIntervalRepository;
    }

    void sendNotification(long projectId, Notification notification) {
        NotificationManager manager = getNotificationManager();
        if (Notifications.Companion.isOngoingChannelDisabled(manager)) {
            Timber.d("Ongoing notification channel is disabled, ignoring notification");
            return;
        }

        manager.notify(
                buildNotificationTag(projectId),
                WorkerApplication.NOTIFICATION_ON_GOING_ID,
                notification
        );
    }

    void dismissNotification(long projectId) {
        NotificationManager manager = getNotificationManager();
        manager.cancel(
                buildNotificationTag(projectId),
                WorkerApplication.NOTIFICATION_ON_GOING_ID
        );
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    boolean isOngoingNotificationEnabled() {
        return keyValueStore.ongoingNotification();
    }

    boolean isOngoingNotificationChronometerEnabled() {
        return keyValueStore.ongoingNotificationChronometer();
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
