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

package me.raatiniemi.worker.presentation.model;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class ProjectsModelTest {
    @DataProvider
    public static Object[][] getTimeSummary_dataProvider()
            throws ClockOutBeforeClockInException {
        return new Object[][]{
                {
                        "1h 0m",
                        createTimeForGetTimeSummaryTest(3600)
                },
                {
                        "2h 30m",
                        createTimeForGetTimeSummaryTest(9000)
                }
        };
    }

    private static Time createTimeForGetTimeSummaryTest(long intervalInSeconds)
            throws ClockOutBeforeClockInException {
        return new Time.Builder(1L)
                .stopInMilliseconds(intervalInSeconds * 1000)
                .build();
    }

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

    @Test
    public void getDescription()
            throws InvalidProjectNameException {
        Project project = createProjectBuilder("Project name")
                .describe("Project description")
                .build();

        ProjectsModel model = new ProjectsModel(project);

        assertEquals("Project description", model.getDescription());
    }

    @Test
    @UseDataProvider("getTimeSummary_dataProvider")
    public void getTimeSummary(String expected, Time time)
            throws InvalidProjectNameException, ClockOutBeforeClockInException {
        Project project = createProjectBuilder("Project name")
                .build();
        List<Time> times = new ArrayList<>();
        times.add(time);
        project.addTime(times);

        ProjectsModel model = new ProjectsModel(project);

        assertEquals(expected, model.getTimeSummary());
    }
}
