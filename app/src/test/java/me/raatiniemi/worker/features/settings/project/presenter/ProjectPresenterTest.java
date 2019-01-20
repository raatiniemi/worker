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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint;
import me.raatiniemi.worker.features.settings.project.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.features.settings.project.view.ProjectView;
import me.raatiniemi.worker.util.InMemoryKeyValueStore;
import me.raatiniemi.worker.util.KeyValueStore;
import me.raatiniemi.worker.util.KeyValueStoreKt;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class ProjectPresenterTest {
    private final KeyValueStore keyValueStore = new InMemoryKeyValueStore();
    private EventBus eventBus;
    private ProjectPresenter presenter;
    private ProjectView view;

    @Before
    public void setUp() {
        eventBus = mock(EventBus.class);
        presenter = new ProjectPresenter(keyValueStore, eventBus);
        view = mock(ProjectView.class);
    }

    @Test
    public void changeTimeSummaryStartingPoint_withMonth() {
        keyValueStore.useWeekForTimeSummaryStartingPoint();
        presenter.attachView(view);

        presenter.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.MONTH.getRawValue());

        assertEquals(TimeIntervalStartingPoint.MONTH.getRawValue(), keyValueStore.startingPointForTimeSummary());
        verify(eventBus).post(any(TimeSummaryStartingPointChangeEvent.class));
        verify(view).showChangeTimeSummaryStartingPointToMonthSuccessMessage();
        verify(view, never()).showChangeTimeSummaryStartingPointToWeekSuccessMessage();
        verify(view, never()).showChangeTimeSummaryStartingPointErrorMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_withWeek() {
        presenter.attachView(view);

        presenter.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.WEEK.getRawValue());

        assertEquals(TimeIntervalStartingPoint.WEEK.getRawValue(), keyValueStore.startingPointForTimeSummary());
        verify(eventBus).post(any(TimeSummaryStartingPointChangeEvent.class));
        verify(view, never()).showChangeTimeSummaryStartingPointToMonthSuccessMessage();
        verify(view).showChangeTimeSummaryStartingPointToWeekSuccessMessage();
        verify(view, never()).showChangeTimeSummaryStartingPointErrorMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_withPreviousValue() {
        presenter.attachView(view);

        presenter.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.MONTH.getRawValue());

        assertEquals(TimeIntervalStartingPoint.MONTH.getRawValue(), keyValueStore.startingPointForTimeSummary());
        verify(eventBus, never()).post(any(TimeSummaryStartingPointChangeEvent.class));
        verify(view, never()).showChangeTimeSummaryStartingPointToMonthSuccessMessage();
        verify(view, never()).showChangeTimeSummaryStartingPointToWeekSuccessMessage();
        verify(view, never()).showChangeTimeSummaryStartingPointErrorMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_withoutAttachedView() {
        presenter.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.WEEK.getRawValue());

        verify(eventBus).post(any(TimeSummaryStartingPointChangeEvent.class));
        verify(view, never()).showChangeTimeSummaryStartingPointToMonthSuccessMessage();
        verify(view, never()).showChangeTimeSummaryStartingPointToWeekSuccessMessage();
        verify(view, never()).showChangeTimeSummaryStartingPointErrorMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_invalidStartingPoint() {
        presenter.attachView(view);

        presenter.changeTimeSummaryStartingPoint(0);

        verify(view).showChangeTimeSummaryStartingPointErrorMessage();
    }

    @Test
    public void changeTimeSummaryStartingPoint_invalidStartingPointWithoutAttachedView() {
        presenter.changeTimeSummaryStartingPoint(0);

        verify(view, never()).showChangeTimeSummaryStartingPointErrorMessage();
    }

    @Test
    public void changeTimeReportSummaryFormat_withDigitalClock() {
        keyValueStore.useFractionAsTimeSheetSummaryFormat();
        presenter.attachView(view);

        presenter.changeTimeReportSummaryFormat(KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK);

        assertEquals(KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK, keyValueStore.timeSheetSummaryFormat());
        verify(view).showChangeTimeReportSummaryToDigitalClockSuccessMessage();
        verify(view, never()).showChangeTimeReportSummaryToFractionSuccessMessage();
        verify(view, never()).showChangeTimeReportSummaryFormatErrorMessage();
    }

    @Test
    public void changeTimeReportSummaryFormat_withFraction() {
        presenter.attachView(view);

        presenter.changeTimeReportSummaryFormat(KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_FRACTION);

        assertEquals(KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_FRACTION, keyValueStore.timeSheetSummaryFormat());
        verify(view, never()).showChangeTimeReportSummaryToDigitalClockSuccessMessage();
        verify(view).showChangeTimeReportSummaryToFractionSuccessMessage();
        verify(view, never()).showChangeTimeReportSummaryFormatErrorMessage();
    }

    @Test
    public void changeTimeReportSummaryFormat_withPreviousValue() {
        presenter.attachView(view);

        presenter.changeTimeReportSummaryFormat(KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK);

        assertEquals(KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_DIGITAL_CLOCK, keyValueStore.timeSheetSummaryFormat());
        verify(view, never()).showChangeTimeReportSummaryToDigitalClockSuccessMessage();
        verify(view, never()).showChangeTimeReportSummaryToFractionSuccessMessage();
        verify(view, never()).showChangeTimeReportSummaryFormatErrorMessage();
    }

    @Test
    public void changeTimeReportSummaryFormat_withoutAttachedView() {
        presenter.changeTimeReportSummaryFormat(KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_FRACTION);

        assertEquals(KeyValueStoreKt.TIME_SHEET_SUMMARY_FORMAT_FRACTION, keyValueStore.timeSheetSummaryFormat());
        verify(view, never()).showChangeTimeReportSummaryToDigitalClockSuccessMessage();
        verify(view, never()).showChangeTimeReportSummaryToFractionSuccessMessage();
        verify(view, never()).showChangeTimeReportSummaryFormatErrorMessage();
    }

    @Test
    public void changeTimeReportSummaryFormat_invalidFormat() {
        presenter.attachView(view);

        presenter.changeTimeReportSummaryFormat(0);

        verify(view, never()).showChangeTimeReportSummaryToDigitalClockSuccessMessage();
        verify(view, never()).showChangeTimeReportSummaryToFractionSuccessMessage();
        verify(view).showChangeTimeReportSummaryFormatErrorMessage();
    }

    @Test
    public void changeTimeReportSummaryFormat_invalidFormatWithoutAttachedView() {
        presenter.changeTimeReportSummaryFormat(0);

        verify(view, never()).showChangeTimeReportSummaryToDigitalClockSuccessMessage();
        verify(view, never()).showChangeTimeReportSummaryToFractionSuccessMessage();
        verify(view, never()).showChangeTimeReportSummaryFormatErrorMessage();
    }
}
