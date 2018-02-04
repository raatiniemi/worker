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
import me.raatiniemi.worker.domain.model.Time;

public class TimeFactory extends Time.Builder {
    private TimeFactory(long projectId) {
        super(projectId);
    }

    public static TimeFactory builder(Long projectId) {
        return new TimeFactory(projectId);
    }

    public static TimeFactory builder() {
        return builder(1L);
    }

    @Override
    public TimeFactory id(Long id) {
        super.id(id);

        return this;
    }

    @Override
    public TimeFactory startInMilliseconds(long startInMilliseconds) {
        super.startInMilliseconds(startInMilliseconds);

        return this;
    }

    @Override
    public TimeFactory stopInMilliseconds(long stopInMilliseconds) {
        super.stopInMilliseconds(stopInMilliseconds);

        return this;
    }

    @Override
    public TimeFactory register() {
        super.register();

        return this;
    }

    @Override
    public Time build() {
        try {
            return super.build();
        } catch (ClockOutBeforeClockInException e) {
            throw new RuntimeException(e);
        }
    }
}
