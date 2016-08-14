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

package me.raatiniemi.worker.presentation.view.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;

import me.raatiniemi.worker.data.WorkerContract;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.project.view.ProjectActivity;

abstract class OngoingNotification {
    private static final int PENDING_INTENT_FLAG = PendingIntent.FLAG_UPDATE_CURRENT;

    private final Context context;
    private final Project project;

    private final NotificationCompat.Builder builder;

    protected OngoingNotification(Context context, Project project) {
        this.context = context;
        this.project = project;

        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(getSmallIcon())
                .setContentTitle(project.getName())
                .setContentIntent(buildContentAction())
                .setOngoing(true);
    }

    @DrawableRes
    protected abstract int getSmallIcon();

    private PendingIntent buildContentAction() {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(ProjectActivity.MESSAGE_PROJECT_ID, project.getId());

        return buildPendingIntentWithActivity(intent);
    }

    private PendingIntent buildPendingIntentWithActivity(Intent intent) {
        return PendingIntent.getActivity(context, 0, intent, PENDING_INTENT_FLAG);
    }

    protected Intent buildIntentWithService(Class serviceClass) {
        Intent intent = new Intent(context, serviceClass);
        intent.setData(getDataUri());

        return intent;
    }

    private Uri getDataUri() {
        return WorkerContract.ProjectContract.getItemUri(project.getId());
    }

    protected PendingIntent buildPendingIntentWithService(Intent intent) {
        return PendingIntent.getService(context, 0, intent, PENDING_INTENT_FLAG);
    }

    protected String getStringWithResourceId(@StringRes int resourceId) {
        return context.getString(resourceId);
    }

    protected Notification buildWithActions(
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
