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

import android.view.View;
import android.widget.TextView;

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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class ProjectsItemSetVisibilityForClockedInSinceView {
    private final int expectedViewVisibility;
    private final Project project;

    public ProjectsItemSetVisibilityForClockedInSinceView(
            int expectedViewVisibility,
            Project project
    ) {
        this.expectedViewVisibility = expectedViewVisibility;
        this.project = project;
    }

    @Parameters
    public static Collection<Object[]> getParameters() throws InvalidProjectNameException {
        return Arrays.asList(
                new Object[][]{
                        {
                                View.GONE,
                                buildProjectWithActiveIndicator(Boolean.FALSE)
                        },
                        {
                                View.VISIBLE,
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
    public void getClockedInSince() {
        ProjectsItem projectsItem = ProjectsItem.from(project);
        TextView textView = mock(TextView.class);

        projectsItem.setVisibilityForClockedInSinceView(textView);

        verify(textView, times(1)).setVisibility(expectedViewVisibility);
    }
}
