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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.factory.TimeIntervalFactory;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ProjectsItemIsActiveTest {
    private final boolean expected;
    private final Project project;

    public ProjectsItemIsActiveTest(boolean expected, Project project) {
        this.expected = expected;
        this.project = project;
    }

    @Parameters
    public static Collection<Object[]> getParameters() throws InvalidProjectNameException {
        return Arrays.asList(
                new Object[][]{
                        {
                                Boolean.FALSE,
                                buildProjectWithActiveIndicator(Boolean.FALSE)
                        },
                        {
                                Boolean.TRUE,
                                buildProjectWithActiveIndicator(Boolean.TRUE)
                        }
                }
        );
    }

    private static Project buildProjectWithActiveIndicator(boolean isProjectActive)
            throws InvalidProjectNameException {
        Project project = Project.builder("Project #1").build();

        if (isProjectActive) {
            List<TimeInterval> timeIntervals = new ArrayList<>();
            timeIntervals.add(
                    TimeIntervalFactory.builder(1L)
                            .startInMilliseconds(1)
                            .stopInMilliseconds(0)
                            .build()
            );
            project.addTime(timeIntervals);
        }

        return project;
    }

    @Test
    public void isActive() {
        ProjectsItem projectsItem = ProjectsItem.from(project);

        assertEquals(expected, projectsItem.isActive());
    }
}
