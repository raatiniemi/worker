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

package me.raatiniemi.worker.features.projects.viewmodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.interactor.ClockIn;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint;
import me.raatiniemi.worker.features.projects.model.ProjectsItem;
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ClockActivityViewModelTest {
    private final ClockIn clockIn = mock(ClockIn.class);
    private final ClockOut clockOut = mock(ClockOut.class);
    private final GetProjectTimeSince getProjectTimeSince = mock(GetProjectTimeSince.class);
    private ClockActivityViewModel.ViewModel vm;

    private final TestSubscriber<ProjectsItemAdapterResult> clockInSuccess = new TestSubscriber<>();
    private final TestSubscriber<Throwable> clockInError = new TestSubscriber<>();

    private final TestSubscriber<ProjectsItemAdapterResult> clockOutSuccess = new TestSubscriber<>();
    private final TestSubscriber<Throwable> clockOutError = new TestSubscriber<>();

    @Before
    public void setUp() {
        vm = new ClockActivityViewModel.ViewModel(clockIn, clockOut, getProjectTimeSince);

        vm.output().clockInSuccess().subscribe(clockInSuccess);
        vm.error().clockInError().subscribe(clockInError);

        vm.output().clockOutSuccess().subscribe(clockOutSuccess);
        vm.error().clockOutError().subscribe(clockOutError);
    }

    @Test
    public void clockIn_withError() {
        Project project = Project.from(1L, "Project #1");
        ProjectsItem projectsItem = ProjectsItem.from(project, getActiveTimeIntervals());
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        doThrow(ClockOutBeforeClockInException.class).when(clockIn).execute(eq(1L), any());

        vm.input().clockIn(result, new Date());

        clockInSuccess.assertNoValues();
        clockInError.assertValueCount(1);
        clockOutSuccess.assertNoValues();
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
    }

    @Test
    public void clockIn() {
        Project project = Project.from(1L, "Project #1");
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.emptyList());
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(getProjectTimeSince.execute(eq(project), eq(TimeIntervalStartingPoint.MONTH)))
                .thenReturn(getActiveTimeIntervals());

        vm.input().clockIn(result, new Date());

        clockInSuccess.assertValueCount(1);
        clockInError.assertNoValues();
        clockOutSuccess.assertNoValues();
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockInSuccess.getOnNextEvents(), true);
    }

    @Test
    public void clockIn_withDifferentStartingPoint() {
        Project project = Project.from(1L, "Project #1");
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.emptyList());
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(getProjectTimeSince.execute(eq(project), eq(TimeIntervalStartingPoint.DAY)))
                .thenReturn(getActiveTimeIntervals());

        vm.input().startingPointForTimeSummary(TimeIntervalStartingPoint.DAY);
        vm.input().clockIn(result, new Date());

        clockInSuccess.assertValueCount(1);
        clockInError.assertNoValues();
        clockOutSuccess.assertNoValues();
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockInSuccess.getOnNextEvents(), true);
    }

    @Test
    public void clockIn_withInvalidStartingPoint() {
        Project project = Project.from(1L, "Project #1");
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.emptyList());
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(getProjectTimeSince.execute(eq(project), eq(TimeIntervalStartingPoint.MONTH)))
                .thenReturn(getActiveTimeIntervals());

        vm.input().startingPointForTimeSummary(-1);
        vm.input().clockIn(result, new Date());

        clockInSuccess.assertValueCount(1);
        clockInError.assertNoValues();
        clockOutSuccess.assertNoValues();
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockInSuccess.getOnNextEvents(), true);
    }

    @Test
    public void clockOut_withError() {
        Project project = Project.from(1L, "Project #1");
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.emptyList());
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        doThrow(ClockOutBeforeClockInException.class).when(clockOut).execute(eq(1L), any());

        vm.input().clockOut(result, new Date());

        clockInSuccess.assertNoValues();
        clockInError.assertNoValues();
        clockOutSuccess.assertNoValues();
        clockOutError.assertValueCount(1);
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
    }

    @Test
    public void clockOut() {
        Project project = Project.from(1L, "Project #1");
        ProjectsItem projectsItem = ProjectsItem.from(project, getActiveTimeIntervals());
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(getProjectTimeSince.execute(eq(project), eq(TimeIntervalStartingPoint.MONTH)))
                .thenReturn(Collections.emptyList());

        vm.input().clockOut(result, new Date());

        clockInSuccess.assertNoValues();
        clockInError.assertNoValues();
        clockOutSuccess.assertValueCount(1);
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockOutSuccess.getOnNextEvents(), false);
    }

    @Test
    public void clockOut_withDifferentStartingPoint() {
        Project project = Project.from(1L, "Project #1");
        ProjectsItem projectsItem = ProjectsItem.from(project, getActiveTimeIntervals());
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(getProjectTimeSince.execute(eq(project), eq(TimeIntervalStartingPoint.DAY)))
                .thenReturn(Collections.emptyList());

        vm.input().startingPointForTimeSummary(TimeIntervalStartingPoint.DAY);
        vm.input().clockOut(result, new Date());

        clockInSuccess.assertNoValues();
        clockInError.assertNoValues();
        clockOutSuccess.assertValueCount(1);
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockOutSuccess.getOnNextEvents(), false);
    }

    @Test
    public void clockOut_withInvalidStartingPoint() {
        Project project = Project.from(1L, "Project #1");
        ProjectsItem projectsItem = ProjectsItem.from(project, getActiveTimeIntervals());
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(getProjectTimeSince.execute(eq(project), eq(TimeIntervalStartingPoint.MONTH)))
                .thenReturn(Collections.emptyList());

        vm.input().startingPointForTimeSummary(-1);
        vm.input().clockOut(result, new Date());

        clockInSuccess.assertNoValues();
        clockInError.assertNoValues();
        clockOutSuccess.assertValueCount(1);
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockOutSuccess.getOnNextEvents(), false);
    }

    @NonNull
    private List<TimeInterval> getActiveTimeIntervals() {
        List<TimeInterval> timeIntervals = new ArrayList<>();
        timeIntervals.add(
                TimeInterval.builder(1L)
                        .startInMilliseconds(1)
                        .stopInMilliseconds(0)
                        .build()
        );

        return timeIntervals;
    }

    private void verifyProjectStatus(List<ProjectsItemAdapterResult> results, boolean isActive) {
        ProjectsItemAdapterResult result = results.get(0);
        ProjectsItem projectsItem = result.getProjectsItem();

        if (isActive) {
            assertTrue(projectsItem.isActive());
            return;
        }

        assertFalse(projectsItem.isActive());
    }
}
