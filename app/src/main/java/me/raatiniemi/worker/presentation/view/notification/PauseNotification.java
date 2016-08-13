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
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.mapper.TimeContentValuesMapper;
import me.raatiniemi.worker.data.mapper.TimeCursorMapper;
import me.raatiniemi.worker.data.repository.TimeResolverRepository;
import me.raatiniemi.worker.data.service.ClockOutService;
import me.raatiniemi.worker.data.service.PauseService;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.presentation.util.Settings;

/**
 * Notification for pausing or clocking out an active project.
 */
public class PauseNotification extends OngoingNotification {
    private static final String TAG = "PauseNotification";
    private static final int SMALL_ICON = R.drawable.ic_pause_notification;

    private static final int PAUSE_ICON = 0;

    private static final int CLOCK_OUT_ICON = 0;

    private boolean useChronometer;
    private long registeredTime;

    private PauseNotification(Context context, Project project) {
        super(context, project);

        useChronometer = Settings.isOngoingNotificationChronometerEnabled(context);
        if (useChronometer) {
            populateRegisteredTime(context, project);
        }
    }

    public static Notification build(Context context, Project project) {
        PauseNotification notification = new PauseNotification(context, project);
        return notification.build();
    }

    private void populateRegisteredTime(Context context, Project project) {
        useChronometer = true;

        try {
            List<Time> registeredTime = getRegisteredTime(context, project);
            for (Time time : registeredTime) {
                this.registeredTime += time.getTime();
            }
        } catch (DomainException e) {
            Log.w(TAG, "Unable to populate registered time", e);
            useChronometer = false;
        }
    }

    private List<Time> getRegisteredTime(
            Context context,
            Project project
    ) throws DomainException {
        TimeRepository repository = buildTimeRepository(context);
        GetProjectTimeSince registeredTimeUseCase = buildRegisteredTimeUseCase(repository);

        return registeredTimeUseCase.execute(
                project,
                GetProjectTimeSince.DAY
        );
    }

    private TimeRepository buildTimeRepository(Context context) {
        return new TimeResolverRepository(
                context.getContentResolver(),
                new TimeCursorMapper(),
                new TimeContentValuesMapper()
        );
    }

    private GetProjectTimeSince buildRegisteredTimeUseCase(TimeRepository repository) {
        return new GetProjectTimeSince(repository);
    }

    @Override
    @DrawableRes
    protected int getSmallIcon() {
        return SMALL_ICON;
    }

    private NotificationCompat.Action buildPauseAction() {
        Intent intent = buildIntentWithService(PauseService.class);

        return new NotificationCompat.Action(
                PAUSE_ICON,
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
                CLOCK_OUT_ICON,
                getTextForClockOutAction(),
                buildPendingIntentWithService(intent)
        );
    }

    private String getTextForClockOutAction() {
        return getStringWithResourceId(R.string.notification_pause_action_clock_out);
    }

    @Override
    protected boolean shouldUseChronometer() {
        return useChronometer;
    }

    @Override
    protected long getWhenForChronometer() {
        long currentTimestamp = new Date().getTime();
        return currentTimestamp - registeredTime;
    }

    @Override
    protected Notification build() {
        return buildWithActions(
                buildPauseAction(),
                buildClockOutAction()
        );
    }
}
