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

package me.raatiniemi.worker.features.settings.project.presenter;

import org.greenrobot.eventbus.EventBus;

import me.raatiniemi.worker.domain.exception.InvalidStartingPointException;
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint;
import me.raatiniemi.worker.features.settings.project.exception.InvalidTimeSheetSummaryFormatException;
import me.raatiniemi.worker.features.settings.project.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.features.settings.project.view.ProjectView;
import me.raatiniemi.worker.features.shared.presenter.BasePresenter;
import me.raatiniemi.worker.util.KeyValueStore;
import me.raatiniemi.worker.util.KeyValueStoreKt;
import timber.log.Timber;

public class ProjectPresenter extends BasePresenter<ProjectView> {
    private final KeyValueStore keyValueStore;
    private final EventBus eventBus;

    public ProjectPresenter(KeyValueStore keyValueStore, EventBus eventBus) {
        this.keyValueStore = keyValueStore;
        this.eventBus = eventBus;
    }

    public void changeTimeSummaryStartingPoint(int newStartingPoint) {
        try {
            int currentStartingPoint = keyValueStore.startingPointForTimeSummary();
            if (currentStartingPoint == newStartingPoint) {
                return;
            }

            switch (TimeIntervalStartingPoint.from(newStartingPoint)) {
                case WEEK:
                    keyValueStore.useWeekForTimeSummaryStartingPoint();
                    break;
                case MONTH:
                    keyValueStore.useMonthForTimeSummaryStartingPoint();
                    break;
                default:
                    throw new InvalidStartingPointException(
                            "Starting point '" + newStartingPoint + "' is not valid"
                    );
            }

            eventBus.post(new TimeSummaryStartingPointChangeEvent());

            performWithView(view -> {
                if (TimeIntervalStartingPoint.WEEK.getRawValue() == newStartingPoint) {
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
        int currentFormat = keyValueStore.timeSheetSummaryFormat();
        if (currentFormat == newFormat) {
            return;
        }

        try {
            switch (newFormat) {
                case KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK:
                    keyValueStore.useDigitalClockAsTimeSheetSummaryFormat();
                    break;

                case KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_FRACTION:
                    keyValueStore.useFractionAsTimeSheetSummaryFormat();
                    break;

                default:
                    throw new InvalidTimeSheetSummaryFormatException(
                            "Summary format '" + newFormat + "' is not valid"
                    );
            }

            performWithView(view -> {
                if (KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK == newFormat) {
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
