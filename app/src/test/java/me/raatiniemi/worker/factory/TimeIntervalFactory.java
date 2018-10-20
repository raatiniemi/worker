/*
 * Copyright (C) 2018 Worker Project
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

package me.raatiniemi.worker.factory;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.TimeInterval;

public class TimeIntervalFactory extends TimeInterval.Builder {
    private TimeIntervalFactory(long projectId) {
        super(projectId);
    }

    public static TimeIntervalFactory builder(Long projectId) {
        return new TimeIntervalFactory(projectId);
    }

    public static TimeIntervalFactory builder() {
        return builder(1L);
    }

    @Override
    public TimeIntervalFactory id(Long id) {
        super.id(id);

        return this;
    }

    @Override
    public TimeIntervalFactory startInMilliseconds(long startInMilliseconds) {
        super.startInMilliseconds(startInMilliseconds);

        return this;
    }

    @Override
    public TimeIntervalFactory stopInMilliseconds(long stopInMilliseconds) {
        super.stopInMilliseconds(stopInMilliseconds);

        return this;
    }

    @Override
    public TimeIntervalFactory register() {
        super.register();

        return this;
    }

    @Override
    public TimeInterval build() {
        try {
            return super.build();
        } catch (ClockOutBeforeClockInException e) {
            throw new RuntimeException(e);
        }
    }
}
