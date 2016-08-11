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

import java.util.ArrayList;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;

/**
 * Use case for marking time as registered.
 * <p>
 * TODO: Rename use case to something more descriptive.
 * The use case can also mark time as not registered.
 */
public class MarkRegisteredTime {
    /**
     * Time repository.
     */
    private final TimeRepository timeRepository;

    /**
     * Constructor.
     *
     * @param timeRepository Time repository.
     */
    public MarkRegisteredTime(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    private static List<Time> collectTimeToUpdate(List<Time> times)
            throws ClockOutBeforeClockInException {
        List<Time> timeToUpdate = new ArrayList<>();

        boolean shouldMarkAsRegistered = shouldMarkAsRegistered(times);
        for (Time time : times) {
            if (shouldMarkAsRegistered) {
                timeToUpdate.add(time.markAsRegistered());
                continue;
            }

            timeToUpdate.add(time.unmarkRegistered());
        }

        return timeToUpdate;
    }

    private static boolean shouldMarkAsRegistered(List<Time> times) {
        return !times.get(0).isRegistered();
    }

    /**
     * Mark time as registered.
     *
     * @param time Time to mark.
     * @return Marked time.
     * @throws DomainException If domain rules are violated.
     */
    public Time execute(final Time time) throws DomainException {
        if (time.isRegistered()) {
            return timeRepository.update(time.unmarkRegistered());
        }

        return timeRepository.update(time.markAsRegistered());
    }

    public List<Time> execute(List<Time> times) throws DomainException {
        List<Time> timeToUpdate = collectTimeToUpdate(times);
        return timeRepository.update(timeToUpdate);
    }
}
