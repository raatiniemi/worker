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

import me.raatiniemi.worker.domain.interactor.RemoveProject;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItemAdapterResult;
import rx.Observable;
import rx.subjects.PublishSubject;

public interface RemoveProjectViewModel {
    interface Input {
        void remove(@NonNull ProjectsItemAdapterResult result);
    }

    interface Output {
        @NonNull
        Observable<ProjectsItemAdapterResult> removeProjectSuccess();
    }

    interface Error {
        @NonNull
        Observable<ProjectsItemAdapterResult> removeProjectError();
    }

    class ViewModel implements Input, Output, Error {
        public final Input input = this;
        public final Output output = this;
        public final Error error = this;

        private final PublishSubject<ProjectsItemAdapterResult> removeProjectSuccess = PublishSubject.create();
        private final PublishSubject<ProjectsItemAdapterResult> removeProjectError = PublishSubject.create();
        private final PublishSubject<ProjectsItemAdapterResult> project = PublishSubject.create();

        private final RemoveProject removeProject;

        public ViewModel(@NonNull RemoveProject removeProject) {
            this.removeProject = removeProject;

            project
                    .switchMap(result -> executeUseCase(result)
                            .compose(redirectErrorToSubject(result))
                            .compose(hideErrors())
                    )
                    .subscribe(removeProjectSuccess);
        }

        @NonNull
        private Observable.Transformer<ProjectsItemAdapterResult, ProjectsItemAdapterResult> redirectErrorToSubject(@NonNull ProjectsItemAdapterResult result) {
            return source -> source.doOnError(__ -> removeProjectError.onNext(result))
                    .onErrorResumeNext(Observable.empty());
        }

        @NonNull
        private Observable.Transformer<ProjectsItemAdapterResult, ProjectsItemAdapterResult> hideErrors() {
            return source -> source
                    .doOnError(__ -> {
                    })
                    .onErrorResumeNext(Observable.empty());
        }

        @NonNull
        private Observable<ProjectsItemAdapterResult> executeUseCase(@NonNull ProjectsItemAdapterResult result) {
            try {
                ProjectsItem projectsItem = result.getProjectsItem();
                removeProject.execute(projectsItem.asProject());

                return Observable.just(result);
            } catch (Exception e) {
                return Observable.error(e);
            }
        }

        @Override
        public void remove(@NonNull ProjectsItemAdapterResult result) {
            project.onNext(result);
        }

        @NonNull
        @Override
        public Observable<ProjectsItemAdapterResult> removeProjectSuccess() {
            return removeProjectSuccess;
        }

        @NonNull
        @Override
        public Observable<ProjectsItemAdapterResult> removeProjectError() {
            return removeProjectError;
        }
    }
}
