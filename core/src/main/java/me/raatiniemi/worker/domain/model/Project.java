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
import java.util.List;
import java.util.Objects;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.util.Optional;

import static java.util.Objects.requireNonNull;
import static me.raatiniemi.worker.domain.validator.ProjectName.isValid;

/**
 * Represent a project.
 */
public class Project {
    /**
     * Id for the domain object.
     */
    private final Long id;

    /**
     * Name for the project.
     */
    private final String name;

    /**
     * Time registered for the project.
     */
    private final List<TimeInterval> timeIntervals = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param id   Id for the project.
     * @param name Name of the project.
     * @throws InvalidProjectNameException If project name is null or empty.
     */
    private Project(final Long id, final String name) throws InvalidProjectNameException {
        if (!isValid(name)) {
            throw new InvalidProjectNameException();
        }

        this.id = id;
        this.name = name;
    }

    public static Builder builder(String projectName) {
        return new Builder(projectName);
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
    public List<TimeInterval> getTimeIntervals() {
        return Collections.unmodifiableList(timeIntervals);
    }

    /**
     * Add time for the project from a list.
     *
     * @param timeIntervals Time to add to the project.
     */
    public void addTime(final List<TimeInterval> timeIntervals) {
        requireNonNull(timeIntervals, "Time is not allowed to be null");

        // If the list with items are empty, there's no
        // need to attempt to add them.
        if (timeIntervals.isEmpty()) {
            return;
        }

        this.timeIntervals.addAll(timeIntervals);
    }

    /**
     * Retrieve the active time, if available.
     *
     * @return Active time, or empty optional if project is not active.
     */
    private Optional<TimeInterval> getActiveTimeInterval() {
        if (timeIntervals.isEmpty()) {
            return Optional.empty();
        }

        TimeInterval timeInterval = timeIntervals.get(0);
        if (timeInterval.isActive()) {
            return Optional.of(timeInterval);
        }

        return Optional.empty();
    }

    /**
     * Check if the project is active.
     *
     * @return True if the project is active, otherwise false.
     */
    public boolean isActive() {
        Optional<TimeInterval> value = getActiveTimeInterval();

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
                && getTimeIntervals().equals(project.getTimeIntervals());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Objects.hashCode(getId());
        result = 31 * result + getName().hashCode();
        result = 31 * result + getTimeIntervals().hashCode();
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
