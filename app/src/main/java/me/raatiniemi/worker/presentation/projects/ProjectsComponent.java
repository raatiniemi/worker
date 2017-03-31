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

package me.raatiniemi.worker.presentation.projects;

import javax.inject.Singleton;

import dagger.Component;
import me.raatiniemi.worker.presentation.AndroidModule;
import me.raatiniemi.worker.presentation.PreferenceModule;
import me.raatiniemi.worker.presentation.projects.view.CreateProjectFragment;
import me.raatiniemi.worker.presentation.projects.view.ProjectsActivity;
import me.raatiniemi.worker.presentation.projects.view.ProjectsFragment;

@Singleton
@Component(modules = {AndroidModule.class, PreferenceModule.class, ProjectsModule.class})
public interface ProjectsComponent {
    void inject(ProjectsActivity __);

    void inject(ProjectsFragment projectsView);

    void inject(CreateProjectFragment createProjectFragment);
}
