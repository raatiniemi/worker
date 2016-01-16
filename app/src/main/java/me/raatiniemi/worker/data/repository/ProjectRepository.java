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

package me.raatiniemi.worker.data.repository;

import android.support.annotation.NonNull;

import me.raatiniemi.worker.data.repository.strategy.ProjectStrategy;
import me.raatiniemi.worker.domain.Project;
import rx.Observable;

/**
 * Represents a unified interface for working with different data sources (strategies).
 */
public class ProjectRepository {
    /**
     * Project repository strategy.
     */
    private ProjectStrategy mStrategy;

    /**
     * Constructor.
     *
     * @param strategy Project repository strategy.
     */
    public ProjectRepository(ProjectStrategy strategy) {
        mStrategy = strategy;
    }

    /**
     * Get the project repository strategy.
     *
     * @return Project repository strategy.
     */
    protected ProjectStrategy getStrategy() {
        return mStrategy;
    }

    /**
     * Get projects.
     *
     * @return Observable emitting projects.
     */
    @NonNull
    public Observable<Project> get() {
        return getStrategy().get();
    }

    /**
     * Get project with id.
     *
     * @param id Id for the project.
     * @return Observable emitting project.
     */
    @NonNull
    public Observable<Project> get(final long id) {
        return getStrategy().get(id);
    }

    /**
     * Add project with name.
     *
     * @param name Name of the project.
     * @return Observable emitting project.
     */
    @NonNull
    public Observable<Project> add(final String name) {
        return getStrategy().add(name);
    }

    /**
     * Remove project by id.
     *
     * @param id Id of the project to remove.
     * @return Observable emitting project id.
     */
    @NonNull
    public Observable<Long> remove(final long id) {
        return getStrategy().remove(id);
    }
}