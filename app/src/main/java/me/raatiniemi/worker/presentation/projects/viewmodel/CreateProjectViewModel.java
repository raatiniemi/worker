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

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.interactor.CreateProject;
import me.raatiniemi.worker.domain.model.Project;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static me.raatiniemi.util.NullUtil.nonNull;

public final class CreateProjectViewModel implements CreateProjectViewModelInput, CreateProjectViewModelOutput, CreateProjectViewModelError {
    public final CreateProjectViewModelInput input = this;
    public final CreateProjectViewModelOutput output = this;
    public final CreateProjectViewModelError error = this;

    private final PublishSubject<String> projectName = PublishSubject.create();
    private final BehaviorSubject<Boolean> isProjectNameValid = BehaviorSubject.create(Boolean.FALSE);
    private final PublishSubject<Void> createProject = PublishSubject.create();
    private final PublishSubject<Project> createProjectSuccess = PublishSubject.create();
    private final PublishSubject<Throwable> createProjectError = PublishSubject.create();

    private final CreateProject useCase;

    public CreateProjectViewModel(CreateProject useCase) {
        this.useCase = useCase;

        projectName.map(this::isNameValid)
                .subscribe(isProjectNameValid);

        createProject.withLatestFrom(projectName, (__, name) -> name)
                .switchMap(name -> executeUseCase(name)
                        .compose(redirectErrorsToSubject())
                        .compose(hideErrors()))
                .subscribe(createProjectSuccess);
    }

    private boolean isNameValid(String name) {
        return nonNull(name) && !name.isEmpty();
    }

    private Observable<Project> executeUseCase(String name) {
        try {
            Project project = Project.builder(name)
                    .build();

            return Observable.just(useCase.execute(project));
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    private Observable.Transformer<Project, Project> redirectErrorsToSubject() {
        return source -> source
                .doOnError(createProjectError::onNext)
                .onErrorResumeNext(Observable.empty());
    }

    private Observable.Transformer<Project, Project> hideErrors() {
        return source -> source
                .doOnError(e -> {
                })
                .onErrorResumeNext(Observable.empty());
    }

    @Override
    public void projectName(String name) {
        projectName.onNext(name);
    }

    @Override
    public void createProject() {
        createProject.onNext(null);
    }

    @Override
    public Observable<Boolean> isProjectNameValid() {
        return isProjectNameValid;
    }

    @Override
    public Observable<Project> createProjectSuccess() {
        return createProjectSuccess.asObservable();
    }

    @Override
    public Observable<String> invalidProjectNameError() {
        return createProjectError
                .filter(this::isInvalidProjectNameError)
                .map(Throwable::getMessage);
    }

    private boolean isInvalidProjectNameError(Throwable e) {
        return e instanceof InvalidProjectNameException;
    }

    @Override
    public Observable<String> duplicateProjectNameError() {
        return createProjectError
                .filter(this::isDuplicateProjectNameError)
                .map(Throwable::getMessage);
    }

    private boolean isDuplicateProjectNameError(Throwable e) {
        return e instanceof ProjectAlreadyExistsException;
    }

    @Override
    public Observable<String> createProjectError() {
        return createProjectError
                .filter(this::isUnknownError)
                .map(Throwable::getMessage);
    }

    private boolean isUnknownError(Throwable e) {
        return !isInvalidProjectNameError(e) && !isDuplicateProjectNameError(e);
    }
}
