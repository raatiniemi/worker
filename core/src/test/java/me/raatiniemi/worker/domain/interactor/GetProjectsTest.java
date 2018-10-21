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
import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class GetProjectsTest {
    private ProjectRepository projectRepository;
    private TimeIntervalRepository timeIntervalRepository;

    @Before
    public void setUp() {
        projectRepository = mock(ProjectRepository.class);
        timeIntervalRepository = mock(TimeIntervalRepository.class);
    }

    private Project buildProject(Long id, String name)
            throws InvalidProjectNameException {
        return Project.builder(name)
                .id(id)
                .build();
    }

    @Test
    public void execute() throws DomainException {
        List<Project> projects = new ArrayList<>();
        projects.add(buildProject(1L, "Project #1"));
        projects.add(buildProject(2L, "Project #2"));

        when(projectRepository.findAll())
                .thenReturn(projects);

        GetProjects getProjects = new GetProjects(
                projectRepository,
                timeIntervalRepository
        );
        List<Project> actual = getProjects.execute();

        assertEquals(projects, actual);

        verify(projectRepository, times(1)).findAll();
        verify(timeIntervalRepository, times(2)).getProjectTimeIntervalSinceBeginningOfMonth(anyLong());
    }
}
