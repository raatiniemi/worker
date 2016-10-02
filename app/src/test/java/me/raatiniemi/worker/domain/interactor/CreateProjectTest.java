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
import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.query.Criteria;

import static org.mockito.Matchers.any;
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
        Project project = new Project.Builder("Project Name")
                .build();
        List<Project> projects = new ArrayList<>();
        projects.add(project);

        when(projectRepository.matching(any(Criteria.class)))
                .thenReturn(projects);

        CreateProject createProject = new CreateProject(projectRepository);
        createProject.execute(project);
    }


    @Test
    public void execute() throws DomainException {
        Project project = new Project.Builder("Project Name")
                .build();

        when(projectRepository.matching(any(Criteria.class)))
                .thenReturn(new ArrayList<>());

        CreateProject createProject = new CreateProject(projectRepository);
        createProject.execute(project);

        verify(projectRepository).add(isA(Project.class));
    }
}
