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
public class ProjectsModelGetHelpTextForClockActivityAtTest extends ProjectsModelResourceTest {
    private String expected;
    private Project project;

    public ProjectsModelGetHelpTextForClockActivityAtTest(
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
                                "Clock in at given date and time",
                                mockProjectWithActiveIndicator(Boolean.FALSE)
                        },
                        {
                                "Clock out at given date and time",
                                mockProjectWithActiveIndicator(Boolean.TRUE)
                        }
                }
        );
    }

    private static Project mockProjectWithActiveIndicator(boolean isProjectActive) {
        Project project = mock(Project.class);
        when(project.isActive()).thenReturn(isProjectActive);

        return project;
    }

    @Test
    public void getHelpTextForClockActivityAt() {
        ProjectsModel model = new ProjectsModel(project);

        assertEquals(expected, model.getHelpTextForClockActivityAt(getResources()));
    }
}
