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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.factory.TimeFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ProjectTest {
    private static Project.Builder createProjectBuilder() {
        return Project.builder("Project name");
    }

    @Test
    public void Builder_withDefaultValues()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        assertNull(project.getId());
        assertEquals("Project name", project.getName());
    }

    @Test
    public void Builder_withValues()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .id(2L)
                .build();

        assertEquals("Project name", project.getName());
        assertEquals(Long.valueOf(2L), project.getId());
    }

    @Test(expected = InvalidProjectNameException.class)
    public void Project_withNullName()
            throws InvalidProjectNameException {
        Project.builder(null)
                .build();
    }

    @Test(expected = InvalidProjectNameException.class)
    public void Project_withEmptyName()
            throws InvalidProjectNameException {
        Project.builder("")
                .build();
    }

    @Test
    public void getName()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        assertEquals("Project name", project.getName());
    }

    @Test
    public void addTime_withList() throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .id(1L)
                .build();

        project.addTime(
                Arrays.asList(
                        TimeFactory.builder()
                                .build(),
                        TimeFactory.builder()
                                .build()
                )
        );

        List<Time> times = project.getRegisteredTime();
        assertEquals(2, times.size());
    }

    @Test(expected = NullPointerException.class)
    public void addTime_withNullList()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        project.addTime(null);
    }

    @Test
    public void addTime_withEmptyTimeList()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder()
                .build();

        List<Time> times = new ArrayList<>();
        project.addTime(times);

        assertTrue(project.getRegisteredTime().isEmpty());
    }
}
