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

import java.util.Date;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.service.ResumeService;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.util.Settings;

/**
 * Notification for resuming an inactive project.
 */
public class ResumeNotification extends OngoingNotification {
    private static final int SMALL_ICON = R.drawable.ic_resume_notification;

    private static final int RESUME_ICON = 0;

    private boolean useChronometer;

    private ResumeNotification(Context context, Project project) {
        super(context, project);

        useChronometer = Settings.isOngoingNotificationChronometerEnabled(context);
    }

    public static Notification build(Context context, Project project) {
        ResumeNotification notification = new ResumeNotification(context, project);
        return notification.build();
    }

    @Override
    @DrawableRes
    protected int getSmallIcon() {
        return SMALL_ICON;
    }

    private NotificationCompat.Action buildResumeAction() {
        Intent intent = buildIntentWithService(ResumeService.class);

        return new NotificationCompat.Action(
                RESUME_ICON,
                getTextForResumeAction(),
                buildPendingIntentWithService(intent)
        );
    }

    private String getTextForResumeAction() {
        return getStringWithResourceId(R.string.notification_pause_action_resume);
    }

    @Override
    protected boolean shouldUseChronometer() {
        return useChronometer;
    }

    @Override
    protected long getWhenForChronometer() {
        return new Date().getTime();
    }

    @Override
    protected Notification build() {
        return buildWithActions(
                buildResumeAction()
        );
    }
}
