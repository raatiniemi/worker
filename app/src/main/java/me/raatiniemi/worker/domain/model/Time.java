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

package me.raatiniemi.worker.domain.model;

import java.util.Date;
import java.util.Objects;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;

import static me.raatiniemi.worker.util.NullUtil.isNull;

/**
 * Represent a time interval registered to a project.
 */
public class Time extends DomainObject {
    /**
     * Id for the project connected to the time interval.
     */
    private final long projectId;

    private final long startInMilliseconds;

    private final long stopInMilliseconds;

    /**
     * Flag for registered time.
     */
    private final boolean registered;

    private Time(Builder builder)
            throws ClockOutBeforeClockInException {
        super(builder.id);

        validateTimeInterval(builder);

        projectId = builder.projectId;
        startInMilliseconds = builder.startInMilliseconds;
        stopInMilliseconds = builder.stopInMilliseconds;
        registered = builder.registered;
    }

    private static void validateTimeInterval(Builder builder)
            throws ClockOutBeforeClockInException {
        if (builder.stopInMilliseconds == 0) {
            return;
        }

        if (builder.stopInMilliseconds < builder.startInMilliseconds) {
            throw new ClockOutBeforeClockInException(
            );
        }
    }

    public static Builder builder(long projectId) {
        return new Builder(projectId);
    }

    /**
     * Getter method for the project id.
     *
     * @return Id for the project connected to the time interval.
     */
    public long getProjectId() {
        return projectId;
    }

    public long getStartInMilliseconds() {
        return startInMilliseconds;
    }

    public long getStopInMilliseconds() {
        return stopInMilliseconds;
    }

    /**
     * Getter method for registered time flag.
     *
     * @return True if time is registered, otherwise false.
     */
    public boolean isRegistered() {
        return registered;
    }

    public Time markAsRegistered() throws ClockOutBeforeClockInException {
        if (isRegistered()) {
            return this;
        }

        return builder(getProjectId())
                .id(getId())
                .startInMilliseconds(getStartInMilliseconds())
                .stopInMilliseconds(getStopInMilliseconds())
                .register()
                .build();
    }

    public Time unmarkRegistered() throws ClockOutBeforeClockInException {
        if (!isRegistered()) {
            return this;
        }

        return builder(getProjectId())
                .id(getId())
                .startInMilliseconds(getStartInMilliseconds())
                .stopInMilliseconds(getStopInMilliseconds())
                .build();
    }

    /**
     * Set the clock out timestamp at given date.
     *
     * @param date Date at which to clock out.
     * @throws NullPointerException           If date argument is null.
     * @throws ClockOutBeforeClockInException If clock out occur before clock in.
     */
    public Time clockOutAt(final Date date) throws ClockOutBeforeClockInException {
        if (isNull(date)) {
            throw new NullPointerException("Date is not allowed to be null");
        }

        Builder builder = builder(getProjectId())
                .id(getId())
                .startInMilliseconds(getStartInMilliseconds())
                .stopInMilliseconds(date.getTime());

        if (isRegistered()) {
            builder.register();
        }

        return builder.build();
    }

    /**
     * Check if the time interval is active.
     *
     * @return True if time interval is active, otherwise false.
     */
    public boolean isActive() {
        return 0 == getStopInMilliseconds();
    }

    /**
     * Get the registered time.
     * <p/>
     * The time is only considered registered if the interval is not active,
     * i.e. both the start and stop values must be valid (not zero).
     *
     * @return Registered time in milliseconds, or zero if interval is active.
     */
    public long getTime() {
        long time = 0L;

        if (!isActive()) {
            time = getStopInMilliseconds() - getStartInMilliseconds();
        }

        return time;
    }

    /**
     * Get the time interval.
     * <p/>
     * If the interval is active, the current time will be used to calculate
     * the time between start and now.
     *
     * @return Interval in milliseconds.
     */
    public long getInterval() {
        long stop = getStopInMilliseconds();

        if (isActive()) {
            stop = (new Date()).getTime();
        }

        return stop - getStartInMilliseconds();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Time)) {
            return false;
        }

        Time time = (Time) o;
        return Objects.equals(getId(), time.getId())
                && getProjectId() == time.getProjectId()
                && getStartInMilliseconds() == time.getStartInMilliseconds()
                && getStopInMilliseconds() == time.getStopInMilliseconds()
                && isRegistered() == time.isRegistered();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Objects.hashCode(getId());
        result = 31 * result + (int) (getProjectId() ^ (getProjectId() >>> 32));
        result = 31 * result + (int) (getStartInMilliseconds() ^ (getStartInMilliseconds() >>> 32));
        result = 31 * result + (int) (getStopInMilliseconds() ^ (getStopInMilliseconds() >>> 32));
        result = 31 * result + (isRegistered() ? 1 : 0);
        return result;
    }

    public static class Builder {
        private final long projectId;
        private Long id = null;
        private long startInMilliseconds = 0L;
        private long stopInMilliseconds = 0L;
        private boolean registered = false;

        private Builder(long projectId) {
            this.projectId = projectId;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder startInMilliseconds(long startInMilliseconds) {
            this.startInMilliseconds = startInMilliseconds;
            return this;
        }

        public Builder stopInMilliseconds(long stopInMilliseconds) {
            this.stopInMilliseconds = stopInMilliseconds;
            return this;
        }

        public Builder register() {
            registered = true;
            return this;
        }

        public Time build() throws ClockOutBeforeClockInException {
            return new Time(this);
        }
    }
}
