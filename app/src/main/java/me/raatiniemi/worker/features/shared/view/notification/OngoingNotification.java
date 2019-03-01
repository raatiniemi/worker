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

package me.raatiniemi.worker.features.shared.view.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.features.project.view.ProjectActivity;
import me.raatiniemi.worker.util.Notifications;
import me.raatiniemi.worker.util.OngoingUriCommunicator;

abstract class OngoingNotification {
    private static final int PENDING_INTENT_FLAG = PendingIntent.FLAG_UPDATE_CURRENT;

    private final Context context;
    private final Project project;

    private final NotificationCompat.Builder builder;

    OngoingNotification(Context context, Project project) {
        this.context = context;
        this.project = project;

        builder = Notifications.Companion.ongoingBuilder(context)
                .setSmallIcon(getSmallIcon())
                .setContentTitle(project.getName())
                .setContentIntent(buildContentAction())
                .setOngoing(true);
    }

    Context getContext() {
        return context;
    }

    Project getProject() {
        return project;
    }

    @DrawableRes
    protected abstract int getSmallIcon();

    private PendingIntent buildContentAction() {
        Intent intent = ProjectActivity.newIntent(context, project);

        return buildPendingIntentWithActivity(intent);
    }

    private PendingIntent buildPendingIntentWithActivity(Intent intent) {
        return PendingIntent.getActivity(context, 0, intent, PENDING_INTENT_FLAG);
    }

    Intent buildIntentWithService(Class serviceClass) {
        Intent intent = new Intent(context, serviceClass);
        intent.setData(getDataUri());

        return intent;
    }

    private Uri getDataUri() {
        return OngoingUriCommunicator.createWith(project.getId());
    }

    PendingIntent buildPendingIntentWithService(Intent intent) {
        return PendingIntent.getService(context, 0, intent, PENDING_INTENT_FLAG);
    }

    String getStringWithResourceId(@StringRes int resourceId) {
        return context.getString(resourceId);
    }

    Notification buildWithActions(
            NotificationCompat.Action... actions
    ) {
        if (shouldUseChronometer()) {
            builder.setWhen(getWhenForChronometer())
                    .setShowWhen(shouldUseChronometer())
                    .setUsesChronometer(shouldUseChronometer());
        }

        for (NotificationCompat.Action action : actions) {
            builder.addAction(action);
        }

        return builder.build();
    }

    protected abstract boolean shouldUseChronometer();

    protected abstract long getWhenForChronometer();

    protected abstract Notification build();
}
