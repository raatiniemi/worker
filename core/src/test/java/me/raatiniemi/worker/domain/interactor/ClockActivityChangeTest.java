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

package me.raatiniemi.worker.domain.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import me.raatiniemi.worker.domain.exception.ActiveProjectException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InactiveProjectException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.exception.NoProjectException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.factory.TimeFactory;
import me.raatiniemi.worker.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ClockActivityChangeTest {
    private ProjectRepository projectRepository;
    private TimeRepository timeRepository;
    private ClockIn clockIn;
    private ClockOut clockOut;
    private ClockActivityChange clockActivityChange;

    private Project buildProject() throws InvalidProjectNameException {
        return Project.builder("Project name")
                .id(1L)
                .build();
    }

    @Before
    public void setUp() {
        projectRepository = mock(ProjectRepository.class);
        timeRepository = mock(TimeRepository.class);
        clockIn = mock(ClockIn.class);
        clockOut = mock(ClockOut.class);
        clockActivityChange = new ClockActivityChange(
                projectRepository,
                timeRepository,
                clockIn,
                clockOut
        );
    }

    @Test
    public void execute_clockInProject() throws DomainException {
        Project project = buildProject();
        when(projectRepository.get(1L))
                .thenReturn(Optional.of(project));
        when(timeRepository.getProjectTimeSinceBeginningOfMonth(1L))
                .thenReturn(new ArrayList<>());

        clockActivityChange.execute(project, new Date());

        verify(clockIn).execute(eq(1L), any(Date.class));
        verify(projectRepository).get(eq(1L));
        verify(timeRepository).getProjectTimeSinceBeginningOfMonth(eq(1L));
    }

    @Test(expected = NoProjectException.class)
    public void execute_clockInWithoutValidProject() throws DomainException {
        Project project = buildProject();
        when(projectRepository.get(1L))
                .thenReturn(Optional.empty());

        clockActivityChange.execute(project, new Date());
    }

    @Test(expected = ActiveProjectException.class)
    public void execute_clockInActiveProject() throws DomainException {
        Project project = buildProject();
        doThrow(ActiveProjectException.class)
                .when(clockIn).execute(eq(1L), any(Date.class));

        clockActivityChange.execute(project, new Date());
    }

    @Test
    public void execute_clockOutProject() throws DomainException {
        Project project = buildProject();
        Time time = TimeFactory.builder()
                .stopInMilliseconds(0L)
                .build();
        project.addTime(Collections.singletonList(time));
        when(projectRepository.get(1L))
                .thenReturn(Optional.of(project));
        when(timeRepository.getProjectTimeSinceBeginningOfMonth(1L))
                .thenReturn(new ArrayList<>());

        clockActivityChange.execute(project, new Date());

        verify(clockOut).execute(eq(1L), any(Date.class));
        verify(projectRepository).get(eq(1L));
        verify(timeRepository).getProjectTimeSinceBeginningOfMonth(eq(1L));
    }

    @Test(expected = NoProjectException.class)
    public void execute_clockOutWithoutProject() throws DomainException {
        Project project = buildProject();
        Time time = TimeFactory.builder()
                .stopInMilliseconds(0L)
                .build();
        project.addTime(Collections.singletonList(time));
        when(projectRepository.get(1L))
                .thenReturn(Optional.empty());

        clockActivityChange.execute(project, new Date());
    }

    @Test(expected = InactiveProjectException.class)
    public void execute_clockOutInactiveProject() throws DomainException {
        Project project = buildProject();
        Time time = TimeFactory.builder()
                .stopInMilliseconds(0L)
                .build();
        project.addTime(Collections.singletonList(time));
        doThrow(InactiveProjectException.class)
                .when(clockOut).execute(eq(1L), any(Date.class));

        clockActivityChange.execute(project, new Date());
    }
}
