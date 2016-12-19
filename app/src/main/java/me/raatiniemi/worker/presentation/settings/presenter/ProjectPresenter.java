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

package me.raatiniemi.worker.presentation.settings.presenter;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import me.raatiniemi.worker.domain.exception.InvalidStartingPointException;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.presentation.presenter.BasePresenter;
import me.raatiniemi.worker.presentation.settings.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.presentation.settings.view.ProjectView;
import me.raatiniemi.worker.presentation.util.TimeSummaryPreferences;
import timber.log.Timber;

public class ProjectPresenter extends BasePresenter<ProjectView> {
    private final TimeSummaryPreferences preferences;
    private final EventBus eventBus;

    public ProjectPresenter(Context context, TimeSummaryPreferences preferences, EventBus eventBus) {
        super(context);

        this.preferences = preferences;
        this.eventBus = eventBus;
    }

    public void changeTimeSummaryStartingPoint(int newStartingPoint) {
        try {
            int currentStartingPoint = preferences.getStartingPointForTimeSummary();
            if (currentStartingPoint == newStartingPoint) {
                return;
            }

            switch (newStartingPoint) {
                case GetProjectTimeSince.WEEK:
                    preferences.useWeekForTimeSummaryStartingPoint();
                    break;
                case GetProjectTimeSince.MONTH:
                    preferences.useMonthForTimeSummaryStartingPoint();
                    break;
                default:
                    throw new InvalidStartingPointException(
                            "Starting point '" + newStartingPoint + "' is not valid"
                    );
            }

            eventBus.post(new TimeSummaryStartingPointChangeEvent());

            performWithView(view -> {
                if (GetProjectTimeSince.WEEK == newStartingPoint) {
                    view.showChangeTimeSummaryStartingPointToWeekSuccessMessage();
                    return;
                }

                view.showChangeTimeSummaryStartingPointToMonthSuccessMessage();
            });
        } catch (InvalidStartingPointException e) {
            Timber.w(e, "Unable to set new starting point");

            performWithView(ProjectView::showChangeTimeSummaryStartingPointErrorMessage);
        }
    }
}
