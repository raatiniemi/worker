/*
 * Copyright (C) 2015-2016 Worker Project
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

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.ClockActivityChange;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.interactor.RemoveProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.presenter.RxPresenter;
import me.raatiniemi.worker.presentation.projects.model.ProjectsModel;
import me.raatiniemi.worker.presentation.projects.view.ProjectsView;
import me.raatiniemi.worker.presentation.settings.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.presentation.util.RxUtil;
import me.raatiniemi.worker.presentation.util.Settings;
import me.raatiniemi.worker.presentation.view.notification.PauseNotification;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static me.raatiniemi.util.NullUtil.nonNull;

/**
 * Presenter for the projects module, handles loading of projects.
 */
public class ProjectsPresenter extends RxPresenter<ProjectsView> {
    /**
     * Tag used when logging.
     */
    private static final String TAG = "ProjectsPresenter";

    private final EventBus eventBus;

    /**
     * Use case for getting projects.
     */
    private final GetProjects getProjects;

    /**
     * Use case for getting registered project time.
     */
    private final GetProjectTimeSince getProjectTimeSince;

    /**
     * Use case for project clock in/out.
     */
    private final ClockActivityChange clockActivityChange;

    /**
     * Use case for removing projects.
     */
    private final RemoveProject removeProject;

    /**
     * Interval iterator for refreshing active projects.
     */
    private Subscription refreshProjects;

    /**
     * Constructor.
     *
     * @param context             Context used with the presenter.
     * @param eventBus            Event bus.
     * @param getProjects         Use case for getting projects.
     * @param getProjectTimeSince Use case for getting registered project time.
     * @param clockActivityChange Use case for project clock in/out.
     * @param removeProject       Use case for removing projects.
     */
    public ProjectsPresenter(
            Context context,
            EventBus eventBus,
            GetProjects getProjects,
            GetProjectTimeSince getProjectTimeSince,
            ClockActivityChange clockActivityChange,
            RemoveProject removeProject
    ) {
        super(context);

        this.eventBus = eventBus;
        this.getProjects = getProjects;
        this.getProjectTimeSince = getProjectTimeSince;
        this.clockActivityChange = clockActivityChange;
        this.removeProject = removeProject;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void attachView(ProjectsView view) {
        super.attachView(view);

        eventBus.register(this);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void detachView() {
        super.detachView();

        eventBus.unregister(this);
    }

    /**
     * Get the positions for the active projects from the view.
     *
     * @return Positions for the active projects.
     */
    private List<Integer> getPositionsForActiveProjects() {
        List<Integer> positions = new ArrayList<>();

        // Check that we still have the view attached.
        if (isViewDetached()) {
            Log.d(TAG, "View is not attached, skip checking active projects");
            return positions;
        }

        // Iterate the projects and collect the index of active projects.
        List<ProjectsModel> projects = getView().getProjects();
        for (ProjectsModel project : projects) {
            if (!project.isActive()) {
                continue;
            }

            Log.d(TAG, "Queuing refresh of project: " + project.getTitle());
            positions.add(projects.indexOf(project));
        }
        return positions;
    }

    /**
     * Refresh the project positions on the view.
     *
     * @param positions Positions on the view to refresh.
     */
    private void refreshActiveProjects(List<Integer> positions) {
        // Check that we have found active projects to refresh.
        if (positions.isEmpty()) {
            Log.d(TAG, "No projects are active, nothing to refresh");
            return;
        }

        // Check that we still have the view attached.
        if (isViewDetached()) {
            Log.d(TAG, "View is not attached, skip refreshing active projects");
            return;
        }

        // Refresh the active projects that have been found.
        Log.d(TAG, "Refreshing active projects");
        getView().refreshPositions(positions);
    }

    /**
     * Setup the subscription for refreshing active projects.
     */
    public void beginRefreshingActiveProjects() {
        // Before we create a new subscription for refreshing active projects
        // we have to unsubscribe to the existing one, if one is available.
        stopRefreshingActiveProjects();

        Log.d(TAG, "Subscribe to the refresh of active projects");
        refreshProjects = Observable.interval(60, TimeUnit.SECONDS, Schedulers.newThread())
                .map(aLong -> getPositionsForActiveProjects())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onNext(List<Integer> positions) {
                        Log.d(TAG, "beginRefreshingActiveProjects onNext");

                        // Push the data to the view.
                        refreshActiveProjects(positions);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "beginRefreshingActiveProjects onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to get positions", e);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "beginRefreshingActiveProjects onCompleted");
                    }
                });
    }

    /**
     * Unsubscribe to the refresh of active projects.
     */
    public void stopRefreshingActiveProjects() {
        if (nonNull(refreshProjects) && !refreshProjects.isUnsubscribed()) {
            Log.d(TAG, "Unsubscribe to the refresh of active projects");
            refreshProjects.unsubscribe();
        }
        refreshProjects = null;
    }

    /**
     * Refresh active projects.
     */
    public void refreshActiveProjects() {
        Log.d(TAG, "Refreshing active projects");
        Observable.defer(() -> Observable.just(getPositionsForActiveProjects()))
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onNext(List<Integer> positions) {
                        Log.d(TAG, "refreshActiveProjects onNext");

                        // Push the data to the view.
                        refreshActiveProjects(positions);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "refreshActiveProjects onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to get positions", e);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "refreshActiveProjects onCompleted");
                    }
                });
    }

    /**
     * Retrieve the projects and push them to the view.
     */
    public void getProjects() {
        // Setup the subscription for retrieving projects.
        Observable
                .defer(() -> {
                    try {
                        return Observable.just(getProjects.execute());
                    } catch (DomainException e) {
                        return Observable.error(e);
                    }
                })
                .map(projects -> {
                    List<ProjectsModel> items = new ArrayList<>();

                    for (Project project : projects) {
                        List<Time> registeredTime = getRegisteredTime(project);

                        items.add(new ProjectsModel(project, registeredTime));
                    }

                    return items;
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<List<ProjectsModel>>() {
                    @Override
                    public void onNext(List<ProjectsModel> items) {
                        Log.d(TAG, "getProjects onNext");

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing projects");
                            return;
                        }

                        getView().addProjects(items);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "getProjects onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to get projects", e);

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        getView().showGetProjectsErrorMessage();
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "getProjects onCompleted");
                    }
                });
    }

    private List<Time> getRegisteredTime(Project project) {
        try {
            int startingPointForTimeSummary = Settings.getStartingPointForTimeSummary(getContext());

            return getProjectTimeSince.execute(project, startingPointForTimeSummary);
        } catch (DomainException e) {
            Log.w(TAG, "Unable to get registered time for project", e);
        }

        return Collections.emptyList();
    }

    /**
     * Delete project.
     *
     * @param project Project to be deleted.
     */
    public void deleteProject(final ProjectsModel project) {
        // Before removing the project we need its current index, it's
        // needed to handle the restoration if deletion fails.
        final int index = getView().getProjects().indexOf(project);

        // Remove project from the view before executing the use case,
        // i.e. optimistic propagation, to simulate better latency.
        //
        // If the deletion fails the project will be added back to the
        // view again, at the previous location.
        getView().deleteProjectAtPosition(index);

        Observable.just(project)
                .flatMap(projectsModel -> {
                    removeProject.execute(projectsModel.asProject());

                    return Observable.empty();
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onNext(Object o) {
                        // Nothing to do, everything have been done...
                        Log.d(TAG, "deleteProject onNext");
                    }

                    @Override
                    public void onError(final Throwable e) {
                        Log.d(TAG, "deleteProject onError");

                        // Even if the view have been detached we'd want the
                        // error messaged logged.
                        Log.w(TAG, "Failed to delete project", e);

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        getView().restoreProjectAtPreviousPosition(index, project);
                        getView().showDeleteProjectErrorMessage();
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "deleteProject onCompleted");

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing successful deletion");
                            return;
                        }

                        getView().showDeleteProjectSuccessMessage();
                    }
                });
    }

    /**
     * Change the clock activity status for project, i.e. clock in/out.
     *
     * @param projectsModel Project to change clock activity status.
     * @param date          Date and time to use for the clock activity change.
     */
    public void clockActivityChange(final ProjectsModel projectsModel, final Date date) {
        Observable.just(projectsModel.asProject())
                .flatMap(project -> clockActivityChangeViaUseCase(project, date))
                .map(project -> {
                    List<Time> registeredTime = getRegisteredTime(project);

                    return new ProjectsModel(project, registeredTime);
                })
                .compose(RxUtil.applySchedulers())
                .doOnNext(this::postOngoingNotification)
                .subscribe(new Subscriber<ProjectsModel>() {
                    @Override
                    public void onNext(ProjectsModel project) {
                        Log.d(TAG, "clockActivityChange onNext");

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip updating project");
                            return;
                        }

                        // Update the project.
                        getView().updateProject(project);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "clockActivityChange onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to change clock activity", e);

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        if (projectsModel.isActive()) {
                            getView().showClockOutErrorMessage();
                            return;
                        }
                        getView().showClockInErrorMessage();
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "clockActivityChange onCompleted");
                    }
                });
    }

    private Observable<Project> clockActivityChangeViaUseCase(Project project, Date date) {
        try {
            return Observable.just(clockActivityChange.execute(project, date));
        } catch (DomainException e) {
            return Observable.error(e);
        }
    }

    private void postOngoingNotification(ProjectsModel projectsModel) {
        Project project = projectsModel.asProject();

        NotificationManager manager = (NotificationManager) getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(
                String.valueOf(project.getId()),
                Worker.NOTIFICATION_ON_GOING_ID
        );

        if (!Settings.isOngoingNotificationEnabled(getContext())) {
            return;
        }

        if (project.isActive()) {
            manager.notify(
                    String.valueOf(project.getId()),
                    Worker.NOTIFICATION_ON_GOING_ID,
                    PauseNotification.build(getContext(), project)
            );
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TimeSummaryStartingPointChangeEvent event) {
        if (isViewDetached()) {
            Log.d(TAG, "View is not attached, skip reloading projects");
            return;
        }

        getView().reloadProjects();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OngoingNotificationActionEvent event) {
        if (isViewDetached()) {
            Log.d(TAG, "View is not attached, skip reloading projects");
            return;
        }

        getView().reloadProjects();
    }
}
