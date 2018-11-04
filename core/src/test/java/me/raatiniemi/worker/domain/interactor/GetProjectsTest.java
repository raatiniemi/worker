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

import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository;
import me.raatiniemi.worker.domain.repository.ProjectRepository;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class GetProjectsTest {
    private ProjectRepository repository;
    private GetProjects useCase;

    @Before
    public void setUp() {
        repository = new ProjectInMemoryRepository();
        useCase = new GetProjects(repository);
    }

    @Test
    public void execute_withoutProjects() {
        List<Project> actual = useCase.execute();

        assertTrue(actual.isEmpty());
    }

    @Test
    public void execute() {
        repository.add(Project.from("Project #1"));
        repository.add(Project.from("Project #2"));
        List<Project> expected = new ArrayList<>();
        expected.add(Project.from(1L, "Project #1"));
        expected.add(Project.from(2L, "Project #2"));

        List<Project> actual = useCase.execute();

        assertEquals(expected, actual);
    }
}
