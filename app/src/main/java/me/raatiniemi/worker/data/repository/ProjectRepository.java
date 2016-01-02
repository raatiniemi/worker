/*
 * Copyright (C) 2015 Worker Project
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
        // TODO: Map to Project when the strategy is emitting ProjectEntity.
        return getStrategy().get();
    }
}
