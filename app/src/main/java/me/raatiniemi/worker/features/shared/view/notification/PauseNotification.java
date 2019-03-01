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
import android.content.Context;
import android.content.Intent;

import java.util.Date;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.Repositories;
import me.raatiniemi.worker.data.service.ongoing.ClockOutService;
import me.raatiniemi.worker.data.service.ongoing.PauseService;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;
import timber.log.Timber;

/**
 * Notification for pausing or clocking out an active project.
 */
public class PauseNotification extends OngoingNotification {
    private static final int SMALL_ICON = R.drawable.ic_pause_notification;

    private static final int PAUSE_ICON = 0;

    private static final int CLOCK_OUT_ICON = 0;

    private final Repositories repositories = new Repositories();
    private TimeIntervalRepository repository = repositories.getTimeInterval();

    private boolean useChronometer;
    private long registeredTime;

    private PauseNotification(Context context, Project project, boolean useChronometer) {
        super(context, project);

        this.useChronometer = useChronometer;
        if (this.useChronometer) {
            populateRegisteredTime();
        }
    }

    public static Notification build(Context context, Project project, boolean useChronometer) {
        PauseNotification notification = new PauseNotification(context, project, useChronometer);
        return notification.build();
    }

    private static GetProjectTimeSince buildRegisteredTimeUseCase(TimeIntervalRepository repository) {
        return new GetProjectTimeSince(repository);
    }

    private void populateRegisteredTime() {
        useChronometer = true;

        try {
            long accumulatedTime = 0L;

            for (TimeInterval timeInterval : getRegisteredTime()) {
                accumulatedTime += timeInterval.getTime();
            }

            registeredTime = includeActiveTime(accumulatedTime);
        } catch (DomainException e) {
            Timber.w(e, "Unable to populate registered time");
            useChronometer = false;
        }
    }

    private List<TimeInterval> getRegisteredTime() {
        GetProjectTimeSince registeredTimeUseCase = buildRegisteredTimeUseCase(repository);

        return registeredTimeUseCase.invoke(
                getProject(),
                TimeIntervalStartingPoint.DAY
        );
    }

    private long includeActiveTime(long registeredTime) {
        TimeInterval activeTimeInterval = getActiveTimeIntervalForProject();
        if (activeTimeInterval != null) {
            return registeredTime + activeTimeInterval.getInterval();
        }

        return registeredTime;
    }

    @Nullable
    private TimeInterval getActiveTimeIntervalForProject() {
        return repository.findActiveByProjectId(getProject().getId());
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
