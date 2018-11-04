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
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository;
import me.raatiniemi.worker.domain.repository.ProjectRepository;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class GetProjectTest {
    private ProjectRepository repository;
    private GetProject getProject;

    @Before
    public void setUp() {
        repository = new ProjectInMemoryRepository();
        getProject = new GetProject(repository);
    }

    @Test
    public void execute() {
        repository.add(Project.from("Project name"));
        Project expected = Project.from(1L, "Project name");

        Project actual = getProject.execute(1L);

        assertEquals(expected, actual);
    }

    @Test(expected = NoProjectException.class)
    public void execute_withoutProject() {
        getProject.execute(1L);
    }
}
