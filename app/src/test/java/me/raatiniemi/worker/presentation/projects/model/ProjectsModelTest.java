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

package me.raatiniemi.worker.presentation.projects.model;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class ProjectsModelTest {
    private Project.Builder createProjectBuilder(String projectName) {
        return new Project.Builder(projectName);
    }

    @Test
    public void asProject()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder("Project name")
                .build();

        ProjectsModel model = new ProjectsModel(project);

        assertTrue(project == model.asProject());
    }

    @Test
    public void getTitle() throws InvalidProjectNameException {
        Project project = createProjectBuilder("Project name")
                .build();

        ProjectsModel model = new ProjectsModel(project);

        assertEquals("Project name", model.getTitle());
    }
}
