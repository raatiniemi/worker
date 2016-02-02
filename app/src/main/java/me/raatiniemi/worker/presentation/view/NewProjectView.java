/*
 * Copyright (C) 2016 Worker Project
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

import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.base.view.MvpView;

/**
 * View-interface for creating new projects.
 */
public interface NewProjectView extends MvpView {
    /**
     * Handle successful project creation.
     *
     * @param project Created project.
     */
    void createProjectSuccessful(Project project);

    /**
     * Show error message for invalid project name.
     */
    void showInvalidNameError();

    /**
     * Show error message for duplicate project name.
     */
    void showDuplicateNameError();

    /**
     * Show message for unknown error.
     */
    void showUnknownError();
}
