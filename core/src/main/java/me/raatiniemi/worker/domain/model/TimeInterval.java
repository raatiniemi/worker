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

import static java.util.Objects.requireNonNull;

/**
 * Represent a time interval registered to a project.
 */
public class TimeInterval {
    /**
     * Id for the domain object.
     */
    private final Long id;

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

    private TimeInterval(Builder builder) throws ClockOutBeforeClockInException {
        if (builder.stopInMilliseconds > 0) {
            if (builder.stopInMilliseconds < builder.startInMilliseconds) {
                throw new ClockOutBeforeClockInException(
                );
            }
        }

        id = builder.id;
        projectId = builder.projectId;
        startInMilliseconds = builder.startInMilliseconds;
        stopInMilliseconds = builder.stopInMilliseconds;
        registered = builder.registered;
    }

    public static Builder builder(long projectId) {
        return new Builder(projectId);
    }

    /**
     * Retrieve the id for the domain object.
     *
     * @return Id for the domain object.
     */
    public Long getId() {
        return id;
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

    public TimeInterval markAsRegistered() throws ClockOutBeforeClockInException {
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

    public TimeInterval unmarkRegistered() throws ClockOutBeforeClockInException {
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
    public TimeInterval clockOutAt(final Date date) throws ClockOutBeforeClockInException {
        requireNonNull(date, "Date is not allowed to be null");

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
        if (isActive()) {
            return 0L;
        }

        return calculateInterval(stopInMilliseconds);
    }

    private long calculateInterval(long stopInMilliseconds) {
        return stopInMilliseconds - startInMilliseconds;
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
        if (isActive()) {
            return calculateInterval(new Date().getTime());
        }

        return calculateInterval(stopInMilliseconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TimeInterval)) {
            return false;
        }

        TimeInterval timeInterval = (TimeInterval) o;
        return Objects.equals(getId(), timeInterval.getId())
                && getProjectId() == timeInterval.getProjectId()
                && getStartInMilliseconds() == timeInterval.getStartInMilliseconds()
                && getStopInMilliseconds() == timeInterval.getStopInMilliseconds()
                && isRegistered() == timeInterval.isRegistered();
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

        protected Builder(long projectId) {
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

        public TimeInterval build() throws ClockOutBeforeClockInException {
            return new TimeInterval(this);
        }
    }
}
