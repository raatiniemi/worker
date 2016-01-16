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

import me.raatiniemi.worker.domain.model.Project;
import rx.Observable;

/**
 * Represent a unified interface for working with projects using different data sources.
 */
public interface ProjectRepository {
    /**
     * Get projects.
     *
     * @return Observable emitting projects.
     */
    @NonNull
    Observable<Project> get();

    /**
     * Get project with id.
     *
     * @param id Id for the project.
     * @return Observable emitting project.
     */
    @NonNull
    Observable<Project> get(long id);

    /**
     * Add project with name.
     *
     * @param name Name of the project.
     * @return Observable emitting project.
     */
    @NonNull
    Observable<Project> add(String name);

    /**
     * Remove project by id.
     * <p>
     * The operation also removes the time registered to the project.
     *
     * @param id Id of the project to remove.
     * @return Observable emitting project id.
     * TODO: When removing project, return void or something more meaningful?
     */
    @NonNull
    Observable<Long> remove(long id);
}
