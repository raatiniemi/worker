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

package me.raatiniemi.worker.domain.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeRepository;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ClockActivityChangeTest {
    private ProjectRepository projectRepository;
    private TimeRepository timeRepository;
    private ClockActivityChange clockActivityChange;

    private Project buildProject() throws InvalidProjectNameException {
        return new Project.Builder("Project name")
                .id(1L)
                .build();
    }

    @Before
    public void setUp() {
        projectRepository = mock(ProjectRepository.class);
        timeRepository = mock(TimeRepository.class);
        clockActivityChange = new ClockActivityChange(
                projectRepository,
                timeRepository
        );
    }

    @Test
    public void execute_clockInProject() throws DomainException {
        Project project = buildProject();
        when(projectRepository.get(1L)).thenReturn(project);
        when(timeRepository.getProjectTimeSinceBeginningOfMonth(1L))
                .thenReturn(new ArrayList<Time>());

        clockActivityChange.execute(project, new Date());

        verify(timeRepository).add(isA(Time.class));
        verify(projectRepository).get(eq(1L));
        verify(timeRepository).getProjectTimeSinceBeginningOfMonth(eq(1L));
    }

    @Test
    public void execute_clockOutProject() throws DomainException {
        Time time = new Time.Builder(1L)
                .startInMilliseconds(1L)
                .build();
        List<Time> registeredTime = new ArrayList<>();
        registeredTime.add(time);
        Project project = buildProject();
        project.addTime(registeredTime);
        when(projectRepository.get(1L)).thenReturn(project);
        when(timeRepository.getProjectTimeSinceBeginningOfMonth(1L))
                .thenReturn(new ArrayList<Time>());

        clockActivityChange.execute(project, new Date());

        verify(timeRepository).update(isA(Time.class));
        verify(projectRepository).get(eq(1L));
        verify(timeRepository).getProjectTimeSinceBeginningOfMonth(eq(1L));
    }
}
