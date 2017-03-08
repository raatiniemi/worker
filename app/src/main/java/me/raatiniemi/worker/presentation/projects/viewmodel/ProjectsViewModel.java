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

package me.raatiniemi.worker.presentation.projects.viewmodel;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import rx.Observable;
import timber.log.Timber;

interface ProjectsViewModel {
    interface Output {
        Observable<List<ProjectsItem>> projects();
    }

    class ViewModel implements Output {
        final Output output = this;

        private Observable<List<ProjectsItem>> projects;

        private GetProjects getProjects;
        private GetProjectTimeSince getProjectTimeSince;

        ViewModel(GetProjects getProjects, GetProjectTimeSince getProjectTimeSince) {
            this.getProjects = getProjects;
            this.getProjectTimeSince = getProjectTimeSince;

            projects = executeGetProjects()
                    .flatMap(Observable::from)
                    .map(this::populateItemWithRegisteredTime)
                    .toList();
        }

        @NonNull
        private Observable<List<Project>> executeGetProjects() {
            return Observable.defer(() -> {
                try {
                    return Observable.just(getProjects.execute());
                } catch (DomainException e) {
                    return Observable.error(e);
                }
            });
        }

        @NonNull
        private ProjectsItem populateItemWithRegisteredTime(Project project) {
            List<Time> registeredTime = getRegisteredTime(project);

            return new ProjectsItem(project, registeredTime);
        }

        @NonNull
        private List<Time> getRegisteredTime(Project project) {
            try {
                return getProjectTimeSince.execute(project, GetProjectTimeSince.MONTH);
            } catch (DomainException e) {
                Timber.w(e, "Unable to get registered time for project");

                return Collections.emptyList();
            }
        }

        @Override
        public Observable<List<ProjectsItem>> projects() {
            return projects;
        }
    }
}
