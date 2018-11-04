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

import java.util.Collections;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository;
import me.raatiniemi.worker.domain.repository.ProjectRepository;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class CreateProjectTest {
    private ProjectRepository repository;
    private CreateProject createProject;

    @Before
    public void setUp() {
        repository = new ProjectInMemoryRepository();
        createProject = new CreateProject(repository);
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void execute_withExistingProject() {
        Project project = Project.from("Project Name");
        repository.add(project);

        createProject.execute(project);
    }

    @Test
    public void execute() {
        Project project = Project.from("Project Name");
        List<Project> expected = Collections.singletonList(Project.from(1L, "Project Name"));

        createProject.execute(project);

        List<Project> actual = repository.findAll();
        assertEquals(expected, actual);
    }
}
