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
import android.support.v4.app.NotificationCompat;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.WorkerContract;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.service.ResumeService;
import me.raatiniemi.worker.presentation.view.activity.ProjectActivity;
import me.raatiniemi.worker.presentation.view.fragment.ProjectsFragment;

/**
 * Notification for resuming an inactive project.
 */
public class ResumeNotification {
    private static final int sSmallIcon = R.drawable.ic_timer_off_black_24dp;

    private static final int sResumeIcon = 0;

    public static Notification build(Context context, Project project) {
        return new NotificationCompat.Builder(context)
                .setContentTitle(project.getName())
                .setSmallIcon(sSmallIcon)
                .addAction(buildResumeAction(context, project))
                .setContentIntent(buildContentAction(context, project))
                .build();
    }

    private static NotificationCompat.Action buildResumeAction(
            Context context,
            Project project
    ) {
        Intent intent = new Intent(context, ResumeService.class);
        intent.setData(getDataUri(project));

        return new NotificationCompat.Action(
                sResumeIcon,
                context.getString(R.string.notification_pause_action_resume),
                buildPendingIntentWithService(context, intent)
        );
    }

    private static Uri getDataUri(Project project) {
        return WorkerContract.ProjectContract.getItemUri(project.getId());
    }

    private static PendingIntent buildPendingIntentWithService(
            Context context,
            Intent intent
    ) {
        return PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private static PendingIntent buildContentAction(Context context, Project project) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(ProjectsFragment.MESSAGE_PROJECT_ID, project.getId());

        return buildPendingIntentWithActivity(context, intent);
    }

    private static PendingIntent buildPendingIntentWithActivity(
            Context context,
            Intent intent
    ) {
        return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}
