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

package me.raatiniemi.worker.presentation.projects.viewmodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.ClockActivityChange;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItemAdapterResult;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ClockActivityViewModelTest {
    private ClockActivityChange clockActivityChange = mock(ClockActivityChange.class);
    private GetProjectTimeSince getProjectTimeSince = mock(GetProjectTimeSince.class);
    private ClockActivityViewModel.ViewModel vm;

    private TestSubscriber<ProjectsItemAdapterResult> clockInSuccess = new TestSubscriber<>();
    private TestSubscriber<Throwable> clockInError = new TestSubscriber<>();

    private TestSubscriber<ProjectsItemAdapterResult> clockOutSuccess = new TestSubscriber<>();
    private TestSubscriber<Throwable> clockOutError = new TestSubscriber<>();

    @Before
    public void setUp() {
        vm = new ClockActivityViewModel.ViewModel(clockActivityChange, getProjectTimeSince);

        vm.output.clockInSuccess().subscribe(clockInSuccess);
        vm.error.clockInError().subscribe(clockInError);

        vm.output.clockOutSuccess().subscribe(clockOutSuccess);
        vm.error.clockOutError().subscribe(clockOutError);
    }

    @Test
    public void clockIn_withError() throws DomainException {
        Project project = mockProjectWithStatus(false);
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        doThrow(ClockOutBeforeClockInException.class)
                .when(clockActivityChange).execute(any(), any());

        vm.input.clockIn(result, new Date());

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
    public void clockIn() throws DomainException {
        Project activeProject = mockProjectWithStatus(true);
        Project project = mockProjectWithStatus(false);
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(clockActivityChange.execute(any(), any()))
                .thenReturn(activeProject);

        vm.input.clockIn(result, new Date());

        clockInSuccess.assertValueCount(1);
        clockInError.assertNoValues();
        clockOutSuccess.assertNoValues();
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockInSuccess.getOnNextEvents(), true);
        verify(getProjectTimeSince)
                .execute(any(), eq(GetProjectTimeSince.MONTH));
    }

    @Test
    public void clockIn_withDifferentStartingPoint() throws DomainException {
        Project activeProject = mockProjectWithStatus(true);
        Project project = mockProjectWithStatus(false);
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(clockActivityChange.execute(any(), any()))
                .thenReturn(activeProject);

        vm.input.startingPointForTimeSummary(GetProjectTimeSince.DAY);
        vm.input.clockIn(result, new Date());

        clockInSuccess.assertValueCount(1);
        clockInError.assertNoValues();
        clockOutSuccess.assertNoValues();
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockInSuccess.getOnNextEvents(), true);
        verify(getProjectTimeSince)
                .execute(any(), eq(GetProjectTimeSince.DAY));
    }

    @Test
    public void clockIn_withInvalidStartingPoint() throws DomainException {
        Project activeProject = mockProjectWithStatus(true);
        Project project = mockProjectWithStatus(false);
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(clockActivityChange.execute(any(), any()))
                .thenReturn(activeProject);

        vm.input.startingPointForTimeSummary(-1);
        vm.input.clockIn(result, new Date());

        clockInSuccess.assertValueCount(1);
        clockInError.assertNoValues();
        clockOutSuccess.assertNoValues();
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockInSuccess.getOnNextEvents(), true);
        verify(getProjectTimeSince)
                .execute(any(), eq(GetProjectTimeSince.MONTH));
    }

    @Test
    public void clockOut_withError() throws DomainException {
        Project project = mockProjectWithStatus(true);
        ProjectsItem projectsItem = new ProjectsItem(project);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        doThrow(ClockOutBeforeClockInException.class)
                .when(clockActivityChange).execute(any(), any());

        vm.input.clockOut(result, new Date());

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
    public void clockOut() throws DomainException {
        Project activeProject = mockProjectWithStatus(true);
        Project project = mockProjectWithStatus(false);
        ProjectsItem projectsItem = new ProjectsItem(activeProject);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(clockActivityChange.execute(any(), any()))
                .thenReturn(project);

        vm.input.clockOut(result, new Date());

        clockInSuccess.assertNoValues();
        clockInError.assertNoValues();
        clockOutSuccess.assertValueCount(1);
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockOutSuccess.getOnNextEvents(), false);
        verify(getProjectTimeSince)
                .execute(any(), eq(GetProjectTimeSince.MONTH));
    }

    @Test
    public void clockOut_withDifferentStartingPoint() throws DomainException {
        Project activeProject = mockProjectWithStatus(true);
        Project project = mockProjectWithStatus(false);
        ProjectsItem projectsItem = new ProjectsItem(activeProject);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(clockActivityChange.execute(any(), any()))
                .thenReturn(project);

        vm.input.startingPointForTimeSummary(GetProjectTimeSince.DAY);
        vm.input.clockOut(result, new Date());

        clockInSuccess.assertNoValues();
        clockInError.assertNoValues();
        clockOutSuccess.assertValueCount(1);
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockOutSuccess.getOnNextEvents(), false);
        verify(getProjectTimeSince)
                .execute(any(), eq(GetProjectTimeSince.DAY));
    }

    @Test
    public void clockOut_withInvalidStartingPoint() throws DomainException {
        Project activeProject = mockProjectWithStatus(true);
        Project project = mockProjectWithStatus(false);
        ProjectsItem projectsItem = new ProjectsItem(activeProject);
        ProjectsItemAdapterResult result = ProjectsItemAdapterResult.build(0, projectsItem);
        when(clockActivityChange.execute(any(), any()))
                .thenReturn(project);

        vm.input.startingPointForTimeSummary(-1);
        vm.input.clockOut(result, new Date());

        clockInSuccess.assertNoValues();
        clockInError.assertNoValues();
        clockOutSuccess.assertValueCount(1);
        clockOutError.assertNoValues();
        clockInSuccess.assertNoTerminalEvent();
        clockInError.assertNoTerminalEvent();
        clockOutSuccess.assertNoTerminalEvent();
        clockOutError.assertNoTerminalEvent();
        verifyProjectStatus(clockOutSuccess.getOnNextEvents(), false);
        verify(getProjectTimeSince)
                .execute(any(), eq(GetProjectTimeSince.MONTH));
    }

    private Project mockProjectWithStatus(boolean isActive) throws DomainException {
        Project project = mock(Project.class);
        when(project.isActive())
                .thenReturn(isActive);

        return project;
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
