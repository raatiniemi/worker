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
import me.raatiniemi.worker.presentation.service.ClockOutService;
import me.raatiniemi.worker.presentation.service.PauseService;

/**
 * Notification for pausing or clocking out an active project.
 */
public class PauseNotification extends OngoingNotification {
    private static final int sSmallIcon = R.drawable.ic_timer_black_24dp;

    private static final int sPauseIcon = 0;

    private static final int sClockOutIcon = 0;

    private PauseNotification(Context context, Project project) {
        super(context, project);
    }

    public static Notification build(Context context, Project project) {
        PauseNotification notification = new PauseNotification(context, project);
        return notification.build();
    }

    @Override
    @DrawableRes
    protected int getSmallIcon() {
        return sSmallIcon;
    }

    private NotificationCompat.Action buildPauseAction() {
        Intent intent = buildIntentWithService(PauseService.class);

        return new NotificationCompat.Action(
                sPauseIcon,
                getTextForPauseAction(),
                buildPendingIntentWithService(intent)
        );
    }

    private String getTextForPauseAction() {
        return getStringWithResourceId(R.string.notification_pause_action_pause);
    }

    private NotificationCompat.Action buildClockOutAction() {
        Intent intent = buildIntentWithService(ClockOutService.class);

        return new NotificationCompat.Action(
                sClockOutIcon,
                getTextForClockOutAction(),
                buildPendingIntentWithService(intent)
        );
    }

    private String getTextForClockOutAction() {
        return getStringWithResourceId(R.string.notification_pause_action_clock_out);
    }

    @Override
    protected boolean shouldUseChronometer() {
        return false;
    }

    @Override
    protected long getWhenForChronometer() {
        return 0;
    }

    @Override
    protected Notification build() {
        return buildWithActions(
                buildPauseAction(),
                buildClockOutAction()
        );
    }
}
