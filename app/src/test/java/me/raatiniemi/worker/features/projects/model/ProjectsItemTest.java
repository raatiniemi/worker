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

package me.raatiniemi.worker.features.projects.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

@RunWith(JUnit4.class)
public class ProjectsItemTest {
    @Test
    public void asProject() throws InvalidProjectNameException {
        Project project = Project.builder("Project name")
                .build();
        ProjectsItem projectsItem = ProjectsItem.from(project);

        assertSame(project, projectsItem.asProject());
    }

    @Test
    public void getTitle() throws InvalidProjectNameException {
        Project project = Project.builder("Project name")
                .build();
        ProjectsItem projectsItem = ProjectsItem.from(project);

        assertEquals("Project name", projectsItem.getTitle());
    }
}
