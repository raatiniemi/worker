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

import java.util.Date;
import java.util.List;
import java.util.Map;

import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.domain.repository.query.Criteria;

/**
 * Use case for getting segment from project timesheet.
 */
public class GetTimesheet {
    /**
     * Time repository.
     */
    private final TimeRepository timeRepository;

    /**
     * Constructor.
     *
     * @param timeRepository Time repository.
     */
    public GetTimesheet(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    /**
     * Get segment from project timesheet.
     *
     * @param projectId          Id for project.
     * @param offset             Offset for segment.
     * @param hideRegisteredTime Should registered time be hidden.
     * @return Segment of project timesheet.
     */
    public Map<Date, List<Time>> execute(
            final Long projectId,
            final int offset,
            boolean hideRegisteredTime
    ) {
        Criteria criteria = null;
        if (hideRegisteredTime) {
            // TODO: Refactor to remove dependency on the data-package for column name.
            criteria = Criteria.equalTo(TimeColumns.REGISTERED, "0");
        }

        return timeRepository.getTimesheet(projectId, offset, criteria);
    }
}
