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

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.repository.TimeIntervalRepository;

/**
 * Use case for marking time as registered.
 * <p>
 * TODO: Rename use case to something more descriptive.
 * The use case can also mark time as not registered.
 */
public class MarkRegisteredTime {
    /**
     * Time interval repository.
     */
    private final TimeIntervalRepository timeIntervalRepository;

    /**
     * Constructor.
     *
     * @param timeIntervalRepository Time interval repository.
     */
    public MarkRegisteredTime(TimeIntervalRepository timeIntervalRepository) {
        this.timeIntervalRepository = timeIntervalRepository;
    }

    private static List<TimeInterval> collectTimeToUpdate(List<TimeInterval> timeIntervals)
            throws ClockOutBeforeClockInException {
        List<TimeInterval> timeIntervalToUpdate = new ArrayList<>();

        boolean shouldMarkAsRegistered = shouldMarkAsRegistered(timeIntervals);
        for (TimeInterval timeInterval : timeIntervals) {
            if (shouldMarkAsRegistered) {
                timeIntervalToUpdate.add(timeInterval.markAsRegistered());
                continue;
            }

            timeIntervalToUpdate.add(timeInterval.unmarkRegistered());
        }

        return timeIntervalToUpdate;
    }

    private static boolean shouldMarkAsRegistered(List<TimeInterval> timeIntervals) {
        return !timeIntervals.get(0).isRegistered();
    }

    public List<TimeInterval> execute(List<TimeInterval> timeIntervals) {
        List<TimeInterval> timeToUpdate = collectTimeToUpdate(timeIntervals);
        return timeIntervalRepository.update(timeToUpdate);
    }
}
