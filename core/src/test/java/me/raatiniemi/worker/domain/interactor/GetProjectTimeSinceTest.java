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

package me.raatiniemi.worker.domain.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;

import me.raatiniemi.worker.domain.exception.InvalidStartingPointException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class GetProjectTimeSinceTest {
    private TimeIntervalRepository timeIntervalRepository;
    private GetProjectTimeSince getProjectTimeSince;
    private Project project;

    private static long getMillisecondsForStartingPoint(int startingPoint) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        switch (startingPoint) {
            case GetProjectTimeSince.DAY:
                break;
            case GetProjectTimeSince.WEEK:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case GetProjectTimeSince.MONTH:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            default:
                throw new InvalidStartingPointException(
                        "Starting point '" + startingPoint + "' is not valid"
                );
        }

        return calendar.getTimeInMillis();
    }

    @Before
    public void setUp() {
        timeIntervalRepository = mock(TimeIntervalRepository.class);
        getProjectTimeSince = new GetProjectTimeSince(timeIntervalRepository);
        project = Project.builder("Name")
                .id(1L)
                .build();
    }

    @Test
    public void execute_withDay() {
        getProjectTimeSince.execute(project, GetProjectTimeSince.DAY);

        verify(timeIntervalRepository)
                .findAll(
                        eq(project),
                        eq(getMillisecondsForStartingPoint(GetProjectTimeSince.DAY))
                );
    }

    @Test
    public void execute_withWeek() {
        getProjectTimeSince.execute(project, GetProjectTimeSince.WEEK);

        verify(timeIntervalRepository)
                .findAll(
                        eq(project),
                        eq(getMillisecondsForStartingPoint(GetProjectTimeSince.WEEK))
                );
    }

    @Test
    public void execute_withMonth() {
        getProjectTimeSince.execute(project, GetProjectTimeSince.MONTH);

        verify(timeIntervalRepository)
                .findAll(
                        eq(project),
                        eq(getMillisecondsForStartingPoint(GetProjectTimeSince.MONTH))
                );
    }

    @Test(expected = InvalidStartingPointException.class)
    public void execute_withInvalidStartingPoint() {
        getProjectTimeSince.execute(project, -1);
    }
}
