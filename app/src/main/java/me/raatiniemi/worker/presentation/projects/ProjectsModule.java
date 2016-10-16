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

package me.raatiniemi.worker.presentation.projects;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.raatiniemi.worker.data.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.data.repository.ProjectResolverRepository;
import me.raatiniemi.worker.domain.interactor.CreateProject;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.presentation.projects.presenter.NewProjectPresenter;

@Module
public class ProjectsModule {
    @Provides
    @Singleton
    NewProjectPresenter providesNewProjectPresenter(Context context) {
        ProjectRepository projectRepository = new ProjectResolverRepository(
                context.getContentResolver(),
                new ProjectCursorMapper(),
                new ProjectContentValuesMapper()
        );

        return new NewProjectPresenter(
                context,
                new CreateProject(projectRepository)
        );
    }
}
