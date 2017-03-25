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

import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.view.MvpView;

/**
 * Methods related to handling of the project view.
 */
public interface ProjectsView extends MvpView {
    /**
     * Update ongoing notification for project.
     *
     * @param project Project for which to update the notification.
     */
    void updateNotificationForProject(ProjectsItem project);

    /**
     * Update project in the list.
     *
     * @param position Position of project to update.
     * @param project Project to update in the list.
     */
    void updateProject(int position, ProjectsItem project);

    /**
     * Show message for failed clock in action.
     */
    void showClockInErrorMessage();

    /**
     * Show message for failed clock out action.
     */
    void showClockOutErrorMessage();
}
