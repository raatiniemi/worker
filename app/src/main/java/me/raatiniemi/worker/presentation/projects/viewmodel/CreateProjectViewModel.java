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

import me.raatiniemi.worker.domain.interactor.CreateProject;
import me.raatiniemi.worker.domain.model.Project;
import rx.Observable;
import rx.subjects.PublishSubject;

final class CreateProjectViewModel implements CreateProjectViewModelInput, CreateProjectViewModelOutput {
    final CreateProjectViewModelInput input = this;
    final CreateProjectViewModelOutput output = this;

    private final PublishSubject<String> projectName = PublishSubject.create();
    private final PublishSubject<Void> createProject = PublishSubject.create();
    private final PublishSubject<Project> onCreateProject = PublishSubject.create();

    private final CreateProject useCase;

    CreateProjectViewModel(CreateProject useCase) {
        this.useCase = useCase;

        createProject.withLatestFrom(projectName, (__, name) -> name)
                .flatMap(this::executeUseCase)
                .subscribe(onCreateProject);
    }

    private Observable<Project> executeUseCase(String name) {
        try {
            Project project = new Project.Builder(name)
                    .build();

            return Observable.just(useCase.execute(project));
        } catch (Exception e) {
            return Observable.error(e);
        }
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
    public Observable<Project> onCreateProject() {
        return onCreateProject.asObservable();
    }
}
