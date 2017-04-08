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

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import me.raatiniemi.worker.domain.interactor.ClockActivityChange;
import me.raatiniemi.worker.domain.interactor.ClockIn;
import me.raatiniemi.worker.domain.interactor.ClockOut;
import me.raatiniemi.worker.domain.interactor.CreateProject;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.interactor.RemoveProject;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.domain.repository.TimeRepository;
import me.raatiniemi.worker.presentation.projects.viewmodel.ClockActivityViewModel;
import me.raatiniemi.worker.presentation.projects.viewmodel.CreateProjectViewModel;
import me.raatiniemi.worker.presentation.projects.viewmodel.ProjectsViewModel;
import me.raatiniemi.worker.presentation.projects.viewmodel.RefreshActiveProjectsViewModel;
import me.raatiniemi.worker.presentation.projects.viewmodel.RemoveProjectViewModel;

@Module
public class ProjectsModule {
    @Provides
    ProjectsViewModel.ViewModel providesProjectsViewModel(
            @NonNull ProjectRepository projectRepository,
            @NonNull TimeRepository timeRepository
    ) {
        return new ProjectsViewModel.ViewModel(
                new GetProjects(projectRepository, timeRepository),
                new GetProjectTimeSince(timeRepository)
        );
    }

    @Provides
    ClockActivityViewModel.ViewModel providesClockActivityChangeViewModel(
            @NonNull ProjectRepository projectRepository,
            @NonNull TimeRepository timeRepository
    ) {
        return new ClockActivityViewModel.ViewModel(
                new ClockActivityChange(
                        projectRepository,
                        timeRepository,
                        new ClockIn(timeRepository),
                        new ClockOut(timeRepository)
                ),
                new GetProjectTimeSince(timeRepository)
        );
    }

    @Provides
    RemoveProjectViewModel.ViewModel providesRemoveProjectViewModel(
            @NonNull ProjectRepository projectRepository
    ) {
        return new RemoveProjectViewModel.ViewModel(
                new RemoveProject(projectRepository)
        );
    }

    @Provides
    RefreshActiveProjectsViewModel.ViewModel providesRefreshActiveProjectsViewModel() {
        return new RefreshActiveProjectsViewModel.ViewModel();
    }

    @Provides
    CreateProjectViewModel.ViewModel providesCreateProjectViewModel(
            @NonNull ProjectRepository projectRepository
    ) {
        return new CreateProjectViewModel.ViewModel(
                new CreateProject(projectRepository)
        );
    }
}
