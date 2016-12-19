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

package me.raatiniemi.worker.presentation.projects.presenter;

import android.content.Context;

import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;
import me.raatiniemi.worker.domain.exception.ProjectAlreadyExistsException;
import me.raatiniemi.worker.domain.interactor.CreateProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.presenter.BasePresenter;
import me.raatiniemi.worker.presentation.projects.view.NewProjectFragment;
import me.raatiniemi.worker.presentation.projects.view.NewProjectView;
import me.raatiniemi.worker.presentation.util.RxUtil;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Presenter for the {@link NewProjectFragment}.
 */
public class NewProjectPresenter extends BasePresenter<NewProjectView> {
    /**
     * Use case for creating new projects.
     */
    private final CreateProject createProject;

    /**
     * Constructor.
     *
     * @param context       Context used with the presenter.
     * @param createProject Use case for creating projects.
     */
    public NewProjectPresenter(
            final Context context,
            final CreateProject createProject
    ) {
        super(context);

        this.createProject = createProject;
    }

    /**
     * Create new project.
     *
     * @param name Name of the project.
     */
    public void createNewProject(final String name) {
        try {
            Project project = new Project.Builder(name)
                    .build();

            Observable.just(project)
                    .flatMap(this::createProjectViaUseCase)
                    .compose(RxUtil.applySchedulers())
                    .subscribe(new Subscriber<Project>() {
                        @Override
                        public void onNext(Project project) {
                            Timber.d("createNewProject onNext");

                            performWithView(view -> view.createProjectSuccessful(project));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.d("createNewProject onError");

                            // Log the error even if the view have been detached.
                            Timber.w(e, "Failed to create project");

                            performWithView(view -> {
                                if (e instanceof ProjectAlreadyExistsException) {
                                    view.showDuplicateNameError();
                                    return;
                                }

                                view.showUnknownError();
                            });
                        }

                        @Override
                        public void onCompleted() {
                            Timber.d("createNewProject onCompleted");
                        }
                    });
        } catch (InvalidProjectNameException e) {
            Timber.d(e, "Invalid name for project");
            performWithView(NewProjectView::showInvalidNameError);
        }
    }

    private Observable<Project> createProjectViaUseCase(Project project) {
        try {
            return Observable.just(createProject.execute(project));
        } catch (Exception e) {
            return Observable.error(e);
        }
    }
}
