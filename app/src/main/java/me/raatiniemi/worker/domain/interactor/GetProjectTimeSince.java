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

import java.util.Calendar;
import java.util.List;

import me.raatiniemi.worker.data.provider.WorkerContract.TimeColumns;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InvalidStartingPointException;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.domain.repository.query.Criteria;

/**
 * Get the registered time for a project since a defined starting point, i.e. {@link #DAY},
 * {@link #WEEK}, or {@link #MONTH}.
 */
public class GetProjectTimeSince {
    public static final int DAY = 0;
    public static final int WEEK = 1;
    public static final int MONTH = 2;

    private final TimeRepository timeRepository;

    public GetProjectTimeSince(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    private static Criteria buildStartingPointCriteria(int startingPoint) {
        // TODO: Remove dependency on `TimeColumns.START`
        // The domain package should not depend on outside definitions.
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

    /**
     * Get the registered time for a project since the starting point.
     *
     * @param project       Project for which to get the registered time.
     * @param startingPoint Starting point, i.e. {@link #DAY}, {@link #WEEK}, or {@link #MONTH}.
     * @return Registered time for the project since the starting point.
     * @throws DomainException If domain rules are violated.
     */
    public List<Time> execute(Project project, int startingPoint) throws DomainException {
        return timeRepository.matching(project, buildStartingPointCriteria(startingPoint));
    }
}
