/*
 * Copyright (C) 2017 Worker Project
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

import org.greenrobot.eventbus.EventBus;

import me.raatiniemi.worker.domain.exception.InvalidStartingPointException;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.presentation.presenter.BasePresenter;
import me.raatiniemi.worker.presentation.settings.exception.InvalidTimeSheetSummaryFormatException;
import me.raatiniemi.worker.presentation.settings.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.presentation.settings.view.ProjectView;
import me.raatiniemi.worker.presentation.util.Settings;
import me.raatiniemi.worker.presentation.util.TimeSheetSummaryFormatPreferences;
import me.raatiniemi.worker.presentation.util.TimeSummaryPreferences;
import timber.log.Timber;

public class ProjectPresenter extends BasePresenter<ProjectView> {
    private final TimeSummaryPreferences timeSummaryPreferences;
    private final TimeSheetSummaryFormatPreferences timeSheetSummaryFormatPreferences;
    private final EventBus eventBus;

    public ProjectPresenter(
            TimeSummaryPreferences timeSummaryPreferences,
            TimeSheetSummaryFormatPreferences timeSheetSummaryFormatPreferences,
            EventBus eventBus
    ) {
        this.timeSummaryPreferences = timeSummaryPreferences;
        this.timeSheetSummaryFormatPreferences = timeSheetSummaryFormatPreferences;
        this.eventBus = eventBus;
    }

    public void changeTimeSummaryStartingPoint(int newStartingPoint) {
        try {
            int currentStartingPoint = timeSummaryPreferences.getStartingPointForTimeSummary();
            if (currentStartingPoint == newStartingPoint) {
                return;
            }

            switch (newStartingPoint) {
                case GetProjectTimeSince.WEEK:
                    timeSummaryPreferences.useWeekForTimeSummaryStartingPoint();
                    break;
                case GetProjectTimeSince.MONTH:
                    timeSummaryPreferences.useMonthForTimeSummaryStartingPoint();
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

    public void changeTimeSheetSummaryFormat(int newFormat) {
        int currentFormat = timeSheetSummaryFormatPreferences.getTimeSheetSummaryFormat();
        if (currentFormat == newFormat) {
            return;
        }

        try {
            switch (newFormat) {
                case Settings.TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK:
                    timeSheetSummaryFormatPreferences.useDigitalClockAsTimeSheetSummaryFormat();
                    break;

                case Settings.TIME_SHEET_SUMMARY_FORMAT_FRACTION:
                    timeSheetSummaryFormatPreferences.useFractionAsTimeSheetSummaryFormat();
                    break;

                default:
                    throw new InvalidTimeSheetSummaryFormatException(
                            "Summary format '" + newFormat + "' is not valid"
                    );
            }

            performWithView(view -> {
                if (Settings.TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK == newFormat) {
                    view.showChangeTimeSheetSummaryToDigitalClockSuccessMessage();
                    return;
                }

                view.showChangeTimeSheetSummaryToFractionSuccessMessage();
            });
        } catch (InvalidTimeSheetSummaryFormatException e) {
            Timber.w(e, "Unable to set new format");

            performWithView(ProjectView::showChangeTimeSheetSummaryFormatErrorMessage);
        }
    }
}
