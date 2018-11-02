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

package me.raatiniemi.worker.domain.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(JUnit4.class)
public class ProjectTest {
    private static Project.Builder createProjectBuilder() {
        return Project.builder("Project name");
    }

    @Test
    public void Builder_withDefaultValues() {
        Project project = createProjectBuilder()
                .build();

        assertNull(project.getId());
        assertEquals("Project name", project.getName());
    }

    @Test
    public void Builder_withValues() {
        Project project = createProjectBuilder()
                .id(2L)
                .build();

        assertEquals("Project name", project.getName());
        assertEquals(Long.valueOf(2L), project.getId());
    }

    @Test(expected = InvalidProjectNameException.class)
    public void Project_withNullName() {
        Project.builder(null)
                .build();
    }

    @Test(expected = InvalidProjectNameException.class)
    public void Project_withEmptyName() {
        Project.builder("")
                .build();
    }

    @Test
    public void getName() {
        Project project = createProjectBuilder()
                .build();

        assertEquals("Project name", project.getName());
    }
}
