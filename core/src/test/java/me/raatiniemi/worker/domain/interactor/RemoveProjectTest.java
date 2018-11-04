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

import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectInMemoryRepository;
import me.raatiniemi.worker.domain.repository.ProjectRepository;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RemoveProjectTest {
    private ProjectRepository repository;
    private RemoveProject useCase;

    @Before
    public void setUp() {
        repository = new ProjectInMemoryRepository();
        useCase = new RemoveProject(repository);
    }

    @Test
    public void execute_withProject() {
        repository.add(Project.from("Project name"));
        Project project = Project.from(1L, "Project name");

        useCase.execute(project);

        List<Project> actual = repository.findAll();
        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    public void execute_withProjects() {
        repository.add(Project.from("Project #1"));
        repository.add(Project.from("Project #2"));
        Project project = Project.from(1L, "Project #1");

        useCase.execute(project);

        List<Project> actual = repository.findAll();
        assertEquals(Collections.singletonList(Project.from(2L, "Project #2")), actual);
    }
}
