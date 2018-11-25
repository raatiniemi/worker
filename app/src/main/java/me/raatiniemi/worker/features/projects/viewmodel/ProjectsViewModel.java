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

package me.raatiniemi.worker.features.projects.viewmodel;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InvalidStartingPointException;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.TimeInterval;
import me.raatiniemi.worker.domain.model.TimeIntervalStartingPoint;
import me.raatiniemi.worker.features.projects.model.ProjectsItem;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static me.raatiniemi.worker.util.RxUtil.hideErrors;
import static me.raatiniemi.worker.util.RxUtil.redirectErrors;

public interface ProjectsViewModel {
    interface Input {
        void startingPointForTimeSummary(int startingPoint);
    }

    interface Output {
        @NonNull
        Observable<List<ProjectsItem>> projects();
    }

    interface Error {
        @NonNull
        Observable<Throwable> projectsError();
    }

    class ViewModel implements Input, Output, Error {
        private final Input input;
        private final Output output;
        private final Error error;

        private TimeIntervalStartingPoint startingPoint = TimeIntervalStartingPoint.MONTH;
        private final Observable<List<ProjectsItem>> projects;
        private final PublishSubject<Throwable> projectsError = PublishSubject.create();

        private final GetProjects getProjects;
        private final GetProjectTimeSince getProjectTimeSince;

        public ViewModel(
                @NonNull GetProjects getProjects,
                @NonNull GetProjectTimeSince getProjectTimeSince
        ) {
            input = this;
            output = this;
            error = this;

            this.getProjects = getProjects;
            this.getProjectTimeSince = getProjectTimeSince;

            projects = executeGetProjects()
                    .flatMap(Observable::from)
                    .map(this::populateItemWithRegisteredTime)
                    .compose(redirectErrors(projectsError))
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
        private ProjectsItem populateItemWithRegisteredTime(@NonNull Project project) {
            List<TimeInterval> registeredTime = getRegisteredTime(project);

            return ProjectsItem.from(project, registeredTime);
        }

        @NonNull
        private List<TimeInterval> getRegisteredTime(@NonNull Project project) {
            try {
                return getProjectTimeSince.execute(project, startingPoint.getRawValue());
            } catch (DomainException e) {
                Timber.w(e, "Unable to get registered time for project");

                return Collections.emptyList();
            }
        }

        @Override
        public void startingPointForTimeSummary(int startingPoint) {
            try {
                this.startingPoint = TimeIntervalStartingPoint.from(startingPoint);
            } catch (InvalidStartingPointException e) {
                Timber.w(e, "Invalid starting point supplied: %i", startingPoint);
            }
        }

        @NonNull
        @Override
        public Observable<List<ProjectsItem>> projects() {
            return projects;
        }

        @NonNull
        @Override
        public Observable<Throwable> projectsError() {
            return projectsError;
        }

        @NonNull
        public Input input() {
            return input;
        }

        @NonNull
        public Output output() {
            return output;
        }

        @NonNull
        public Error error() {
            return error;
        }
    }
}
