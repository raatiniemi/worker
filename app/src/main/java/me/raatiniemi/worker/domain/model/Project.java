/*
 * Copyright (C) 2015-2016 Worker Project
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.exception.ClockActivityException;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;

/**
 * Represent a project.
 */
public class Project extends DomainObject {
    /**
     * Name for the project.
     */
    private String mName;

    /**
     * Description for the project.
     */
    private String mDescription;

    /**
     * Flag for archived project.
     */
    private boolean mArchived = false;

    /**
     * Time registered for the project.
     */
    private final List<Time> mTime;

    /**
     * Constructor.
     *
     * @param id   Id for the project.
     * @param name Name of the project.
     * @throws InvalidProjectNameException If project name is null or empty.
     */
    public Project(final Long id, final String name) throws InvalidProjectNameException {
        super(id);

        setName(name);

        // Set default value for non-constructor arguments.
        mTime = new ArrayList<>();
    }

    /**
     * Getter method for the project name.
     *
     * @return Project name.
     */
    public String getName() {
        return mName;
    }

    /**
     * Setter method for the project name.
     *
     * @param name Project name.
     * @throws InvalidProjectNameException If project name is null or empty.
     */
    private void setName(final String name) throws InvalidProjectNameException {
        if (null == name || 0 == name.length()) {
            throw new InvalidProjectNameException("Project name is null or empty");
        }

        mName = name;
    }

    public void rename(final String name) throws InvalidProjectNameException {
        setName(name);
    }

    /**
     * Getter method for the project description.
     *
     * @return Project description.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Setter method for the project description.
     *
     * @param description Project description.
     */
    public void setDescription(String description) {
        // If the description is empty we should reset it to null.
        if (null == description || 0 == description.length()) {
            description = null;
        }

        mDescription = description;
    }

    /**
     * Getter method for archived project flag.
     *
     * @return True if project is archived, otherwise false.
     */
    public boolean isArchived() {
        return mArchived;
    }

    public void archive() {
        mArchived = true;
    }

    public void unarchive() {
        mArchived = false;
    }

    /**
     * Getter method for the project time.
     *
     * @return Project time.
     */
    public List<Time> getTime() {
        return mTime;
    }

    /**
     * Add time for the project.
     *
     * @param time Time to add to the project.
     */
    public void addTime(final Time time) {
        if (null == time) {
            throw new NullPointerException("Time is not allowed to be null");
        }

        getTime().add(time);
    }

    /**
     * Add time for the project from a list.
     *
     * @param time Time to add to the project.
     */
    public void addTime(final List<Time> time) {
        if (null == time) {
            throw new NullPointerException("Time is not allowed to be null");
        }

        // If the list with items are empty, there's no
        // need to attempt to add them.
        if (time.isEmpty()) {
            return;
        }

        getTime().addAll(time);
    }

    /**
     * Summarize the registered time for the project in milliseconds.
     *
     * @return Registered time in milliseconds.
     */
    public long summarizeTime() {
        // Total time in number of seconds.
        long total = 0;

        List<Time> time = getTime();
        if (!time.isEmpty()) {
            // Iterate of the registered time and
            // retrieve the time interval.
            for (Time item : time) {
                total += item.getTime();
            }
        }

        return total;
    }

    /**
     * Retrieve the elapsed time for an active project.
     *
     * @return Elapsed time in milliseconds, zero if project is not active.
     */
    public long getElapsed() {
        long elapsed = 0;

        // Retrieve the interval for the active time.
        Time time = getActiveTime();
        if (null != time) {
            elapsed = time.getInterval();
        }

        return elapsed;
    }

    /**
     * Retrieve the active time, if available.
     *
     * @return Active time, or null if project is not active.
     */
    private Time getActiveTime() {
        // If no time is registered, the project can't be active.
        List<Time> list = getTime();
        if (list.isEmpty()) {
            return null;
        }

        // If the first item is not active, the project is not active.
        Time time = list.get(0);
        if (!time.isActive()) {
            return null;
        }

        return time;
    }

    /**
     * Retrieve the date when the project was clocked in.
     *
     * @return Date when project was clocked in, or null if project is not active.
     */
    public Date getClockedInSince() {
        // Retrieve the last time, i.e. the active time session.
        Time time = getActiveTime();
        if (null == time) {
            return null;
        }

        // TODO: Do not instantiate inside method, return value from get start?
        return new Date(time.getStartInMilliseconds());
    }

    /**
     * Clock in project at a given date and time.
     *
     * @param date Date and time for when to clock in the project.
     * @return The clocked in Time.
     * @throws ClockActivityException         If the project is active.
     * @throws ClockOutBeforeClockInException If clock in occur after clock out.
     */
    public Time clockInAt(final Date date)
            throws ClockActivityException, ClockOutBeforeClockInException {
        if (null == date) {
            throw new NullPointerException("Time is not allowed to be null");
        }

        // If the project is already active, we can't clock in.
        if (isActive()) {
            throw new ClockActivityException("Unable to clock in, project is already active");
        }

        // Instantiate the Time domain object with the project
        // and clock in with the supplied date.
        return new Time.Builder(getId())
                .startInMilliseconds(date.getTime())
                .build();
    }

    /**
     * Clock out project at a given date and time.
     *
     * @param date Date and time for when to clock out the project.
     * @return The clocked out Time.
     * @throws ClockActivityException         If the project is not active.
     * @throws ClockOutBeforeClockInException If clock out occur before clock in.
     */
    public Time clockOutAt(final Date date)
            throws ClockActivityException, ClockOutBeforeClockInException {
        if (null == date) {
            throw new NullPointerException("Time is not allowed to be null");
        }

        // Retrieve the active Time domain object, and clock
        // out with the supplied date.
        //
        // If none is available, i.e. we have not clocked in,
        // we can't clock out.
        Time time = getActiveTime();
        if (null == time) {
            throw new ClockActivityException("Unable to clock out, project is not active");
        }

        time = time.clockOutAt(date);
        mTime.set(0, time);

        return time;
    }

    /**
     * Check if the project is active.
     *
     * @return True if the project is active, otherwise false.
     */
    public boolean isActive() {
        return null != getActiveTime();
    }

    public static class Builder {
        private final String mProjectName;
        private Long mId;
        private String mDescription;
        private boolean mArchived;

        public Builder(String projectName) {
            mProjectName = projectName;
        }

        public Builder id(Long id) {
            mId = id;
            return this;
        }

        public Builder describe(String description) {
            mDescription = description;
            return this;
        }

        public Builder archive() {
            mArchived = true;
            return this;
        }

        public Project build() throws InvalidProjectNameException {
            Project project = new Project(mId, mProjectName);
            project.setDescription(mDescription);
            if (mArchived) {
                project.archive();
            }

            return project;
        }
    }
}
