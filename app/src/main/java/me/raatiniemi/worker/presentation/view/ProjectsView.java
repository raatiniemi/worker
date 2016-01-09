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

package me.raatiniemi.worker.presentation.view;

import android.support.annotation.NonNull;

import java.util.List;

import me.raatiniemi.worker.presentation.base.view.fragment.ListFragment;
import me.raatiniemi.worker.domain.Project;
import me.raatiniemi.worker.presentation.view.adapter.ProjectsAdapter;

/**
 * Methods related to handling of the project view.
 */
public interface ProjectsView extends ListFragment<ProjectsAdapter, Project> {
    /**
     * Add a created project to the view.
     *
     * @param project Created project.
     */
    void addCreatedProject(@NonNull Project project);

    /**
     * Update project in the list.
     *
     * @param project Project to update in the list.
     */
    void updateProject(Project project);

    /**
     * Delete project from the list.
     *
     * @param project Project to be deleted from the list.
     */
    void deleteProject(Project project);

    /**
     * Open the dialog for creating a new project.
     */
    void createNewProject();

    /**
     * Refresh view for projects within the adapter.
     *
     * @param positions Positions for the project to refresh.
     */
    void refreshPositions(List<Integer> positions);
}
