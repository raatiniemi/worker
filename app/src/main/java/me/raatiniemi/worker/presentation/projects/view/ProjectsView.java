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

package me.raatiniemi.worker.presentation.projects.view;

import java.util.List;

import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.view.MvpView;

/**
 * Methods related to handling of the project view.
 */
public interface ProjectsView extends MvpView {
    /**
     * Get the projects from the view.
     *
     * @return Projects from the view.
     */
    List<ProjectsItem> getProjects();

    /**
     * Show message for failed project retrieval.
     */
    void showGetProjectsErrorMessage();

    /**
     * Add a list of projects to the view.
     *
     * @param projects List of projects to add.
     */
    void addProjects(List<ProjectsItem> projects);

    /**
     * Update ongoing notification for project.
     *
     * @param project Project for which to update the notification.
     */
    void updateNotificationForProject(ProjectsItem project);

    /**
     * Update project in the list.
     *
     * @param project Project to update in the list.
     */
    void updateProject(ProjectsItem project);

    /**
     * Show message for failed clock in action.
     */
    void showClockInErrorMessage();

    /**
     * Show message for failed clock out action.
     */
    void showClockOutErrorMessage();

    /**
     * Delete a project from the view at the given position.
     *
     * @param position Position of the project to delete.
     */
    void deleteProjectAtPosition(int position);

    /**
     * Restore a project at its previous position.
     *
     * @param previousPosition Previous position of the project.
     * @param project          Project to restore.
     */
    void restoreProjectAtPreviousPosition(
            int previousPosition,
            ProjectsItem project
    );

    /**
     * Show message for successful project deletion.
     */
    void showDeleteProjectSuccessMessage();

    /**
     * Show message for failed project deletion.
     */
    void showDeleteProjectErrorMessage();

    /**
     * Refresh view for projects within the adapter.
     *
     * @param positions Positions for the project to refresh.
     */
    void refreshPositions(List<Integer> positions);

    /**
     * Reload projects.
     */
    void reloadProjects();
}
