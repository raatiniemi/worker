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
    private final TimeRepository mTimeRepository;

    /**
     * Constructor.
     *
     * @param timeRepository Time repository.
     */
    public MarkRegisteredTime(TimeRepository timeRepository) {
        mTimeRepository = timeRepository;
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
            return mTimeRepository.update(time.unmarkRegistered());
        }

        return mTimeRepository.update(time.markAsRegistered());
    }
}
