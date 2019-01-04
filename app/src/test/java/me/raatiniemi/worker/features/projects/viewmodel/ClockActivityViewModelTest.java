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

package me.raatiniemi.worker.features.projects.viewmodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.interactor.ClockIn;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint;
import me.raatiniemi.worker.domain.repository.TimeIntervalInMemoryRepository;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;
import me.raatiniemi.worker.features.projects.model.ProjectsItem;
import me.raatiniemi.worker.features.projects.model.ProjectsItemAdapterResult;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ClockActivityViewModelTest {
    private final TimeIntervalRepository timeIntervalRepository = new TimeIntervalInMemoryRepository();
    private final ClockIn clockIn = new ClockIn(timeIntervalRepository);
    private final ClockOut clockOut = new ClockOut(timeIntervalRepository);
    private final GetProjectTimeSince getProjectTimeSince = new GetProjectTimeSince(timeIntervalRepository);

    private final TestSubscriber<ProjectsItemAdapterResult> clockInSuccess = new TestSubscriber<>();
    private final TestSubscriber<Throwable> clockInError = new TestSubscriber<>();

    private final TestSubscriber<ProjectsItemAdapterResult> clockOutSuccess = new TestSubscriber<>();
    private final TestSubscriber<Throwable> clockOutError = new TestSubscriber<>();
    private final Project project = Project.from(1L, "Project #1");
    private ClockActivityViewModel.ViewModel vm;

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
        TimeInterval timeInterval = TimeInterval.builder(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(0)
                .build();
        timeIntervalRepository.add(timeInterval);
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.singletonList(timeInterval));
        ProjectsItemAdapterResult result = new ProjectsItemAdapterResult(0, projectsItem);

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
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.emptyList());
        ProjectsItemAdapterResult result = new ProjectsItemAdapterResult(0, projectsItem);

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
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.emptyList());
        ProjectsItemAdapterResult result = new ProjectsItemAdapterResult(0, projectsItem);
        vm.input().startingPointForTimeSummary(TimeIntervalStartingPoint.DAY.getRawValue());

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
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.emptyList());
        ProjectsItemAdapterResult result = new ProjectsItemAdapterResult(0, projectsItem);
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
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.emptyList());
        ProjectsItemAdapterResult result = new ProjectsItemAdapterResult(0, projectsItem);

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
        TimeInterval timeInterval = TimeInterval.builder(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(0)
                .build();
        timeIntervalRepository.add(timeInterval);
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.singletonList(timeInterval));
        ProjectsItemAdapterResult result = new ProjectsItemAdapterResult(0, projectsItem);

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
        TimeInterval timeInterval = TimeInterval.builder(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(0)
                .build();
        timeIntervalRepository.add(timeInterval);
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.singletonList(timeInterval));
        ProjectsItemAdapterResult result = new ProjectsItemAdapterResult(0, projectsItem);
        vm.input().startingPointForTimeSummary(TimeIntervalStartingPoint.DAY.getRawValue());

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
        TimeInterval timeInterval = TimeInterval.builder(1)
                .startInMilliseconds(1)
                .stopInMilliseconds(0)
                .build();
        timeIntervalRepository.add(timeInterval);
        ProjectsItem projectsItem = ProjectsItem.from(project, Collections.singletonList(timeInterval));
        ProjectsItemAdapterResult result = new ProjectsItemAdapterResult(0, projectsItem);
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
