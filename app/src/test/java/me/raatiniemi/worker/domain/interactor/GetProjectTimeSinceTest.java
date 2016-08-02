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

package me.raatiniemi.worker.domain.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;

import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InvalidStartingPointException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.domain.repository.query.Criteria;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class GetProjectTimeSinceTest {
    private TimeRepository mTimeRepository;
    private GetProjectTimeSince getProjectTimeSince;
    private Project mProject;

    private static Criteria buildStartingPointCriteria(int startingPoint) {
        return Criteria.moreThanOrEqualTo(
                TimeColumns.START,
                getMillisecondsForStartingPoint(startingPoint)
        );
    }

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
    public void setUp() throws Exception {
        mTimeRepository = mock(TimeRepository.class);
        getProjectTimeSince = new GetProjectTimeSince(mTimeRepository);
        mProject = new Project.Builder("Name")
                .id(1L)
                .build();
    }

    @Test
    public void execute_withDay() throws DomainException {
        getProjectTimeSince.execute(mProject, GetProjectTimeSince.DAY);

        verify(mTimeRepository)
                .matching(
                        mProject,
                        buildStartingPointCriteria(GetProjectTimeSince.DAY)
                );
    }

    @Test
    public void execute_withWeek() throws DomainException {
        getProjectTimeSince.execute(mProject, GetProjectTimeSince.WEEK);

        verify(mTimeRepository)
                .matching(
                        mProject,
                        buildStartingPointCriteria(GetProjectTimeSince.WEEK)
                );
    }

    @Test
    public void execute_withMonth() throws DomainException {
        getProjectTimeSince.execute(mProject, GetProjectTimeSince.MONTH);

        verify(mTimeRepository)
                .matching(
                        mProject,
                        buildStartingPointCriteria(GetProjectTimeSince.MONTH)
                );
    }

    @Test(expected = InvalidStartingPointException.class)
    public void execute_withInvalidStartingPoint() throws DomainException {
        getProjectTimeSince.execute(mProject, -1);
    }
}
