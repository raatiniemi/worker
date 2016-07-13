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
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.service.ResumeService;

/**
 * Notification for resuming an inactive project.
 */
public class ResumeNotification extends OngoingNotification {
    private static final int sSmallIcon = R.drawable.ic_timer_off_black_24dp;

    private static final int sResumeIcon = 0;

    private ResumeNotification(Context context, Project project) {
        super(context, project);
    }

    public static Notification build(Context context, Project project) {
        ResumeNotification notification = new ResumeNotification(context, project);
        return notification.build();
    }

    @Override
    @DrawableRes
    protected int getSmallIcon() {
        return sSmallIcon;
    }

    private NotificationCompat.Action buildResumeAction() {
        Intent intent = buildIntentWithService(ResumeService.class);

        return new NotificationCompat.Action(
                sResumeIcon,
                getTextForResumeAction(),
                buildPendingIntentWithService(intent)
        );
    }

    private String getTextForResumeAction() {
        return getStringWithResourceId(R.string.notification_pause_action_resume);
    }

    @Override
    protected Notification build() {
        return buildWithActions(
                buildResumeAction()
        );
    }
}
