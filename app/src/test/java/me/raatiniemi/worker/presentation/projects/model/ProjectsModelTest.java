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

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.test.mock.MockResources;
import android.view.View;
import android.widget.TextView;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class ProjectsModelTest {
    private static final Resources resources = new MockResources() {
        @NonNull
        @Override
        public String getString(int id) throws NotFoundException {
            switch (id) {
                case R.string.fragment_projects_item_clocked_in_since:
                    return "Since %s (%s)";
            }

            return super.getString(id);
        }
    };

    @DataProvider
    public static Object[][] getClockedInSince_dataProvider()
            throws ClockOutBeforeClockInException {
        return new Object[][]{
                {
                        "Without registered time",
                        null,
                        null
                },
                {
                        "Without active time",
                        null,
                        new Time[]{
                                new Time.Builder(1L)
                                        .stopInMilliseconds(1L)
                                        .build()
                        }
                },
                {
                        "With an hour elapsed",
                        "Since 15:14 (1h 0m)",
                        new Time[]{
                                createTimeForGetClockedInSinceTestWithElapsedAndClockedInTime(
                                        3600L,
                                        new GregorianCalendar(2016, 1, 28, 15, 14)
                                )
                        }
                },
                {
                        "With half an hour elapsed",
                        "Since 20:25 (30m)",
                        new Time[]{
                                createTimeForGetClockedInSinceTestWithElapsedAndClockedInTime(
                                        1800L,
                                        new GregorianCalendar(2016, 1, 28, 20, 25)
                                )
                        }
                }
        };
    }

    @DataProvider
    public static Object[][] setVisibilityForClockedInSinceView_dataProvider()
            throws ClockOutBeforeClockInException {
        return new Object[][]{
                {
                        mockProjectWithActiveIndicator(Boolean.FALSE),
                        View.GONE
                },
                {
                        mockProjectWithActiveIndicator(Boolean.TRUE),
                        View.VISIBLE
                }
        };
    }

    private static Time createTimeForGetClockedInSinceTestWithElapsedAndClockedInTime(
            long intervalInSeconds,
            Calendar clockedInTime
    ) {
        Time time = mock(Time.class);

        when(time.isActive()).thenReturn(true);
        when(time.getInterval()).thenReturn(intervalInSeconds * 1000);

        when(time.getStartInMilliseconds()).thenReturn(clockedInTime.getTimeInMillis());

        return time;
    }

    private static Project mockProjectWithActiveIndicator(boolean isProjectActive) {
        Project project = mock(Project.class);
        when(project.isActive()).thenReturn(isProjectActive);

        return project;
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
    @UseDataProvider("getClockedInSince_dataProvider")
    public void getClockedInSince(String message, String expected, Time[] times)
            throws InvalidProjectNameException {
        Project project = createProjectBuilder("Project name")
                .build();

        ProjectsModel model = new ProjectsModel(project);
        if (null == times) {
            assertNull(message, model.getClockedInSince(resources));
            return;
        }
        project.addTime(Arrays.asList(times));

        assertEquals(message, expected, model.getClockedInSince(resources));
    }

    @Test
    @UseDataProvider("setVisibilityForClockedInSinceView_dataProvider")
    public void setVisibilityForClockedInSinceView(
            Project project,
            int expectedViewVisibility
    ) throws InvalidProjectNameException, ClockOutBeforeClockInException {
        ProjectsModel model = new ProjectsModel(project);
        TextView textView = mock(TextView.class);

        model.setVisibilityForClockedInSinceView(textView);

        verify(textView, times(1)).setVisibility(expectedViewVisibility);
    }
}
