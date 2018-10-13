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
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.model.Project;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ProjectsItemGetHelpTextForClockActivityAtTest extends ProjectsItemResourceTest {
    private final String expected;
    private final Project project;

    public ProjectsItemGetHelpTextForClockActivityAtTest(
            String expected,
            Project project
    ) {
        this.expected = expected;
        this.project = project;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "Clock in %s at",
                                mockProjectWithActiveIndicator(Boolean.FALSE)
                        },
                        {
                                "Clock out %s at",
                                mockProjectWithActiveIndicator(Boolean.TRUE)
                        }
                }
        );
    }

    private static Project mockProjectWithActiveIndicator(boolean isProjectActive) {
        Project project = mock(Project.class);
        when(project.getName()).thenReturn("project #1");
        when(project.isActive()).thenReturn(isProjectActive);

        return project;
    }

    @Test
    public void getHelpTextForClockActivityAt() {
        ProjectsItem projectsItem = new ProjectsItem(project);

        assertEquals(expected, projectsItem.getHelpTextForClockActivityAt(getResources()));
    }
}
