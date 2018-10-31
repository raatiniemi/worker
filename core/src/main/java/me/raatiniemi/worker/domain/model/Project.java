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

import java.util.Objects;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;

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
     * Constructor.
     *
     * @param id   Id for the project.
     * @param name Name of the project.
     */
    private Project(final Long id, final String name) {
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
                && getName().equals(project.getName());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Objects.hashCode(getId());
        result = 31 * result + getName().hashCode();
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

        public Project build() {
            return new Project(id, projectName);
        }
    }
}
