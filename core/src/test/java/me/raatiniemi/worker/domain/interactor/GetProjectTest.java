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

import me.raatiniemi.worker.domain.exception.NoProjectException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.util.Optional;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class GetProjectTest {
    private ProjectRepository projectRepository;
    private GetProject getProject;

    @Before
    public void setUp() {
        projectRepository = mock(ProjectRepository.class);
        getProject = new GetProject(projectRepository);
    }

    @Test
    public void execute() {
        Project project = Project.from("Name");
        when(projectRepository.findById(eq(1L)))
                .thenReturn(Optional.of(project));

        project = getProject.execute(1L);

        assertNotNull(project);
    }

    @Test(expected = NoProjectException.class)
    public void execute_withoutProject() {
        when(projectRepository.findById(eq(1L)))
                .thenReturn(Optional.empty());

        getProject.execute(1L);
    }
}
