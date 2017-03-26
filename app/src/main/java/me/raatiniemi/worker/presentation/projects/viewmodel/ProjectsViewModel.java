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
import rx.subjects.PublishSubject;
import timber.log.Timber;

public interface ProjectsViewModel {
    interface Input {
        void startingPointForTimeSummary(int startingPoint);
    }

    interface Output {
        Observable<List<ProjectsItem>> projects();
    }

    interface Error {
        Observable<Throwable> projectsError();
    }

    class ViewModel implements Input, Output, Error {
        public final Input input = this;
        public final Output output = this;
        public final Error error = this;

        private int startingPoint = GetProjectTimeSince.MONTH;
        private Observable<List<ProjectsItem>> projects;
        private PublishSubject<Throwable> projectsError = PublishSubject.create();

        private GetProjects getProjects;
        private GetProjectTimeSince getProjectTimeSince;

        public ViewModel(GetProjects getProjects, GetProjectTimeSince getProjectTimeSince) {
            this.getProjects = getProjects;
            this.getProjectTimeSince = getProjectTimeSince;

            projects = executeGetProjects()
                    .flatMap(Observable::from)
                    .map(this::populateItemWithRegisteredTime)
                    .compose(redirectErrorsToSubject())
                    .compose(hideErrors())
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
                return getProjectTimeSince.execute(project, startingPoint);
            } catch (DomainException e) {
                Timber.w(e, "Unable to get registered time for project");

                return Collections.emptyList();
            }
        }

        private Observable.Transformer<ProjectsItem, ProjectsItem> redirectErrorsToSubject() {
            return source -> source
                    .doOnError(projectsError::onNext)
                    .onErrorResumeNext(Observable.empty());
        }

        private Observable.Transformer<ProjectsItem, ProjectsItem> hideErrors() {
            return source -> source
                    .doOnError(e -> {
                    })
                    .onErrorResumeNext(Observable.empty());
        }

        @Override
        public void startingPointForTimeSummary(int startingPoint) {
            switch (startingPoint) {
                case GetProjectTimeSince.MONTH:
                case GetProjectTimeSince.WEEK:
                case GetProjectTimeSince.DAY:
                    this.startingPoint = startingPoint;
                    break;
                default:
                    Timber.d("Invalid starting point supplied: %i", startingPoint);
            }
        }

        @Override
        public Observable<List<ProjectsItem>> projects() {
            return projects;
        }

        @Override
        public Observable<Throwable> projectsError() {
            return projectsError;
        }
    }
}
