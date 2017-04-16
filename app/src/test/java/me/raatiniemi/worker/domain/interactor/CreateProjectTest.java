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

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CreateProjectTest {
    private ProjectRepository projectRepository;

    @Before
    public void setUp() {
        projectRepository = mock(ProjectRepository.class);
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void execute_withExistingProject() throws DomainException {
        Project project = Project.builder("Project Name")
                .build();

        when(projectRepository.findProjectByName(eq("Project Name")))
                .thenReturn(project);

        CreateProject createProject = new CreateProject(projectRepository);
        createProject.execute(project);
    }


    @Test
    public void execute() throws DomainException {
        Project project = Project.builder("Project Name")
                .build();

        when(projectRepository.findProjectByName(eq("Project Name")))
                .thenReturn(null);

        CreateProject createProject = new CreateProject(projectRepository);
        createProject.execute(project);

        verify(projectRepository).add(isA(Project.class));
    }
}
