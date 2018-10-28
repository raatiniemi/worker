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

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.factory.TimeIntervalFactory;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ProjectsItemGetTimeSummaryTest {
    private final String expected;
    private final TimeInterval[] timeIntervals;

    public ProjectsItemGetTimeSummaryTest(String expected, TimeInterval... timeIntervals) {
        this.expected = expected;
        this.timeIntervals = timeIntervals;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                "1h 0m",
                                new TimeInterval[]{
                                        TimeIntervalFactory.builder()
                                                .stopInMilliseconds(3600000)
                                                .build()
                                }
                        },
                        {
                                "2h 30m",
                                new TimeInterval[]{
                                        TimeIntervalFactory.builder()
                                                .stopInMilliseconds(9000000)
                                                .build()
                                }
                        },
                        {
                                "3h 30m",
                                new TimeInterval[]{
                                        TimeIntervalFactory.builder()
                                                .stopInMilliseconds(3600000)
                                                .build(),
                                        TimeIntervalFactory.builder()
                                                .stopInMilliseconds(9000000)
                                                .build()
                                }
                        }
                }
        );
    }

    @Test
    public void getTimeSummary() throws InvalidProjectNameException {
        Project project = Project.builder("Project name")
                .build();
        project.addTime(Arrays.asList(timeIntervals));
        ProjectsItem projectsItem = ProjectsItem.from(project);

        assertEquals(expected, projectsItem.getTimeSummary());
    }
}
