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

package me.raatiniemi.worker.features.settings.project.presenter

import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint
import me.raatiniemi.worker.features.settings.project.model.TimeSummaryStartingPointChangeEvent
import me.raatiniemi.worker.features.settings.project.view.ProjectView
import me.raatiniemi.worker.util.InMemoryKeyValueStore
import me.raatiniemi.worker.util.TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK
import me.raatiniemi.worker.util.TIME_REPORT_SUMMARY_FORMAT_FRACTION
import org.greenrobot.eventbus.EventBus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class ProjectPresenterTest {
    private val keyValueStore = InMemoryKeyValueStore()

    private lateinit var eventBus: EventBus
    private lateinit var view: ProjectView
    private lateinit var presenter: ProjectPresenter

    @Before
    fun setUp() {
        eventBus = mock(EventBus::class.java)
        view = mock(ProjectView::class.java)
        presenter = ProjectPresenter(keyValueStore, eventBus)
    }

    @Test
    fun changeTimeSummaryStartingPoint_withMonth() {
        keyValueStore.useWeekForTimeSummaryStartingPoint()
        presenter.attachView(view)

        presenter.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.MONTH.rawValue)

        assertEquals(TimeIntervalStartingPoint.MONTH.rawValue.toLong(), keyValueStore.startingPointForTimeSummary().toLong())
        verify<EventBus>(eventBus).post(ArgumentMatchers.any(TimeSummaryStartingPointChangeEvent::class.java))
        verify<ProjectView>(view).showChangeTimeSummaryStartingPointToMonthSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeSummaryStartingPointToWeekSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeSummaryStartingPointErrorMessage()
    }

    @Test
    fun changeTimeSummaryStartingPoint_withWeek() {
        presenter.attachView(view)

        presenter.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.WEEK.rawValue)

        assertEquals(TimeIntervalStartingPoint.WEEK.rawValue.toLong(), keyValueStore.startingPointForTimeSummary().toLong())
        verify<EventBus>(eventBus).post(ArgumentMatchers.any(TimeSummaryStartingPointChangeEvent::class.java))
        verify<ProjectView>(view, never()).showChangeTimeSummaryStartingPointToMonthSuccessMessage()
        verify<ProjectView>(view).showChangeTimeSummaryStartingPointToWeekSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeSummaryStartingPointErrorMessage()
    }

    @Test
    fun changeTimeSummaryStartingPoint_withPreviousValue() {
        presenter.attachView(view)

        presenter.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.MONTH.rawValue)

        assertEquals(TimeIntervalStartingPoint.MONTH.rawValue.toLong(), keyValueStore.startingPointForTimeSummary().toLong())
        verify<EventBus>(eventBus, never()).post(ArgumentMatchers.any(TimeSummaryStartingPointChangeEvent::class.java))
        verify<ProjectView>(view, never()).showChangeTimeSummaryStartingPointToMonthSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeSummaryStartingPointToWeekSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeSummaryStartingPointErrorMessage()
    }

    @Test
    fun changeTimeSummaryStartingPoint_withoutAttachedView() {
        presenter.changeTimeSummaryStartingPoint(TimeIntervalStartingPoint.WEEK.rawValue)

        verify<EventBus>(eventBus).post(ArgumentMatchers.any(TimeSummaryStartingPointChangeEvent::class.java))
        verify<ProjectView>(view, never()).showChangeTimeSummaryStartingPointToMonthSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeSummaryStartingPointToWeekSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeSummaryStartingPointErrorMessage()
    }

    @Test
    fun changeTimeSummaryStartingPoint_invalidStartingPoint() {
        presenter.attachView(view)

        presenter.changeTimeSummaryStartingPoint(0)

        verify<ProjectView>(view).showChangeTimeSummaryStartingPointErrorMessage()
    }

    @Test
    fun changeTimeSummaryStartingPoint_invalidStartingPointWithoutAttachedView() {
        presenter.changeTimeSummaryStartingPoint(0)

        verify<ProjectView>(view, never()).showChangeTimeSummaryStartingPointErrorMessage()
    }

    @Test
    fun changeTimeReportSummaryFormat_withDigitalClock() {
        keyValueStore.useFractionAsTimeReportSummaryFormat()
        presenter.attachView(view)

        presenter.changeTimeReportSummaryFormat(TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK)

        assertEquals(TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK, keyValueStore.timeReportSummaryFormat())
        verify<ProjectView>(view).showChangeTimeReportSummaryToDigitalClockSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryToFractionSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryFormatErrorMessage()
    }

    @Test
    fun changeTimeReportSummaryFormat_withFraction() {
        presenter.attachView(view)

        presenter.changeTimeReportSummaryFormat(TIME_REPORT_SUMMARY_FORMAT_FRACTION)

        assertEquals(TIME_REPORT_SUMMARY_FORMAT_FRACTION, keyValueStore.timeReportSummaryFormat())
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryToDigitalClockSuccessMessage()
        verify<ProjectView>(view).showChangeTimeReportSummaryToFractionSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryFormatErrorMessage()
    }

    @Test
    fun changeTimeReportSummaryFormat_withPreviousValue() {
        presenter.attachView(view)

        presenter.changeTimeReportSummaryFormat(TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK)

        assertEquals(TIME_REPORT_SUMMARY_FORMAT_DIGITAL_CLOCK, keyValueStore.timeReportSummaryFormat())
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryToDigitalClockSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryToFractionSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryFormatErrorMessage()
    }

    @Test
    fun changeTimeReportSummaryFormat_withoutAttachedView() {
        presenter.changeTimeReportSummaryFormat(TIME_REPORT_SUMMARY_FORMAT_FRACTION)

        assertEquals(TIME_REPORT_SUMMARY_FORMAT_FRACTION, keyValueStore.timeReportSummaryFormat())
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryToDigitalClockSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryToFractionSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryFormatErrorMessage()
    }

    @Test
    fun changeTimeReportSummaryFormat_invalidFormat() {
        presenter.attachView(view)

        presenter.changeTimeReportSummaryFormat(0)

        verify<ProjectView>(view, never()).showChangeTimeReportSummaryToDigitalClockSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryToFractionSuccessMessage()
        verify<ProjectView>(view).showChangeTimeReportSummaryFormatErrorMessage()
    }

    @Test
    fun changeTimeReportSummaryFormat_invalidFormatWithoutAttachedView() {
        presenter.changeTimeReportSummaryFormat(0)

        verify<ProjectView>(view, never()).showChangeTimeReportSummaryToDigitalClockSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryToFractionSuccessMessage()
        verify<ProjectView>(view, never()).showChangeTimeReportSummaryFormatErrorMessage()
    }
}
