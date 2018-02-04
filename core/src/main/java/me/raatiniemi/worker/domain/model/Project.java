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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.util.Optional;

import static java.util.Objects.requireNonNull;
import static me.raatiniemi.worker.domain.validator.ProjectName.isValid;

/**
 * Represent a project.
 */
public class Project extends DomainObject {
    /**
     * Name for the project.
     */
    private final String name;

    /**
     * Time registered for the project.
     */
    private final List<Time> registeredTime = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param id   Id for the project.
     * @param name Name of the project.
     * @throws InvalidProjectNameException If project name is null or empty.
     */
    private Project(final Long id, final String name) throws InvalidProjectNameException {
        super(id);

        if (!isValid(name)) {
            throw new InvalidProjectNameException();
        }

        this.name = name;
    }

    public static Builder builder(String projectName) {
        return new Builder(projectName);
    }

    /**
     * Getter method for the project name.
     *
     * @return Project name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method for the project time.
     *
     * @return Project time.
     */
    public List<Time> getRegisteredTime() {
        return Collections.unmodifiableList(registeredTime);
    }

    /**
     * Add time for the project from a list.
     *
     * @param time Time to add to the project.
     */
    public void addTime(final List<Time> time) {
        requireNonNull(time, "Time is not allowed to be null");

        // If the list with items are empty, there's no
        // need to attempt to add them.
        if (time.isEmpty()) {
            return;
        }

        registeredTime.addAll(time);
    }

    /**
     * Retrieve the elapsed time for an active project.
     *
     * @return Elapsed time in milliseconds, zero if project is not active.
     */
    public long getElapsed() {
        Optional<Time> value = getActiveTime();
        if (value.isPresent()) {
            Time time = value.get();

            return time.getInterval();
        }

        return 0L;
    }

    /**
     * Retrieve the active time, if available.
     *
     * @return Active time, or empty optional if project is not active.
     */
    private Optional<Time> getActiveTime() {
        if (registeredTime.isEmpty()) {
            return Optional.empty();
        }

        Time time = registeredTime.get(0);
        if (time.isActive()) {
            return Optional.of(time);
        }

        return Optional.empty();
    }

    /**
     * Retrieve the date when the project was clocked in.
     *
     * @return Date when project was clocked in, or null if project is not active.
     */
    public Date getClockedInSince() {
        // Retrieve the last time, i.e. the active time session.
        Optional<Time> value = getActiveTime();
        if (value.isPresent()) {
            Time time = value.get();

            // TODO: Do not instantiate inside method, return value from get start?
            return new Date(time.getStartInMilliseconds());
        }

        return null;
    }

    /**
     * Check if the project is active.
     *
     * @return True if the project is active, otherwise false.
     */
    public boolean isActive() {
        Optional<Time> value = getActiveTime();

        return value.isPresent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Project)) {
            return false;
        }

        Project project = (Project) o;
        return Objects.equals(getId(), project.getId())
                && getName().equals(project.getName())
                && getRegisteredTime().equals(project.getRegisteredTime());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Objects.hashCode(getId());
        result = 31 * result + getName().hashCode();
        result = 31 * result + getRegisteredTime().hashCode();
        return result;
    }

    public static class Builder {
        private final String projectName;
        private Long id;

        private Builder(String projectName) {
            this.projectName = projectName;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Project build() throws InvalidProjectNameException {
            return new Project(id, projectName);
        }
    }
}
