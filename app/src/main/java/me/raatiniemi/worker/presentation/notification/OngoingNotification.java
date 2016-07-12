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

package me.raatiniemi.worker.presentation.notification;

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
import me.raatiniemi.worker.presentation.view.activity.ProjectActivity;
import me.raatiniemi.worker.presentation.view.fragment.ProjectsFragment;

abstract class OngoingNotification {
    private static final int sPendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;

    private final Context mContext;
    private final Project mProject;

    private final NotificationCompat.Builder mBuilder;

    protected OngoingNotification(Context context, Project project) {
        mContext = context;
        mProject = project;

        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(getSmallIcon())
                .setContentTitle(project.getName())
                .setContentIntent(buildContentAction());
    }

    @DrawableRes
    protected abstract int getSmallIcon();

    private PendingIntent buildContentAction() {
        Intent intent = new Intent(mContext, ProjectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(ProjectsFragment.MESSAGE_PROJECT_ID, mProject.getId());

        return buildPendingIntentWithActivity(intent);
    }

    private PendingIntent buildPendingIntentWithActivity(Intent intent) {
        return PendingIntent.getActivity(mContext, 0, intent, sPendingIntentFlag);
    }

    protected Intent buildIntentWithService(Class serviceClass) {
        Intent intent = new Intent(mContext, serviceClass);
        intent.setData(getDataUri());

        return intent;
    }

    private Uri getDataUri() {
        return WorkerContract.ProjectContract.getItemUri(mProject.getId());
    }

    protected PendingIntent buildPendingIntentWithService(Intent intent) {
        return PendingIntent.getService(mContext, 0, intent, sPendingIntentFlag);
    }

    protected String getStringWithResourceId(@StringRes int resourceId) {
        return mContext.getString(resourceId);
    }

    protected Notification buildWithActions(
            NotificationCompat.Action... actions
    ) {
        if (shouldUseChronometer()) {
            mBuilder.setWhen(getWhenForChronometer())
                    .setUsesChronometer(shouldUseChronometer());
        }

        for (NotificationCompat.Action action : actions) {
            mBuilder.addAction(action);
        }

        return mBuilder.build();
    }

    protected abstract boolean shouldUseChronometer();

    protected abstract long getWhenForChronometer();

    protected abstract Notification build();
}
