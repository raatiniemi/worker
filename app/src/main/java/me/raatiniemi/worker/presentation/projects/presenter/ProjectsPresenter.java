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
import me.raatiniemi.worker.presentation.presenter.BasePresenter;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.projects.view.ProjectsView;
import me.raatiniemi.worker.presentation.settings.model.TimeSummaryStartingPointChangeEvent;
import me.raatiniemi.worker.presentation.util.OngoingNotificationPreferences;
import me.raatiniemi.worker.presentation.util.RxUtil;
import me.raatiniemi.worker.presentation.util.TimeSummaryPreferences;
import me.raatiniemi.worker.presentation.view.notification.PauseNotification;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static me.raatiniemi.util.NullUtil.isNull;
import static me.raatiniemi.worker.presentation.util.RxUtil.unsubscribeIfNotNull;

/**
 * Presenter for the projects module, handles loading of projects.
 */
public class ProjectsPresenter extends BasePresenter<ProjectsView> {
    private Subscription refreshProjectsSubscription;

    private final TimeSummaryPreferences timeSummaryPreferences;
    private final OngoingNotificationPreferences ongoingNotificationPreferences;

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
     * Constructor.
     *
     * @param context                        Context used with the presenter.
     * @param timeSummaryPreferences         Preferences for the time summary.
     * @param ongoingNotificationPreferences Preferences for ongoing notification.
     * @param eventBus                       Event bus.
     * @param getProjects                    Use case for getting projects.
     * @param getProjectTimeSince            Use case for getting registered project time.
     * @param clockActivityChange            Use case for project clock in/out.
     * @param removeProject                  Use case for removing projects.
     */
    public ProjectsPresenter(
            Context context,
            TimeSummaryPreferences timeSummaryPreferences,
            OngoingNotificationPreferences ongoingNotificationPreferences,
            EventBus eventBus,
            GetProjects getProjects,
            GetProjectTimeSince getProjectTimeSince,
            ClockActivityChange clockActivityChange,
            RemoveProject removeProject
    ) {
        super(context);

        this.timeSummaryPreferences = timeSummaryPreferences;
        this.ongoingNotificationPreferences = ongoingNotificationPreferences;
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
        List<Integer> activePositions = getFromView(view -> {
            List<Integer> positions = new ArrayList<>();

            List<ProjectsItem> projects = view.getProjects();
            for (ProjectsItem project : projects) {
                if (!project.isActive()) {
                    continue;
                }

                Timber.d("Queuing refresh of project: %s", project.getTitle());
                positions.add(projects.indexOf(project));
            }

            return positions;

        });
        if (isNull(activePositions)) {
            return Collections.emptyList();
        }

        return activePositions;
    }

    /**
     * Refresh the project positions on the view.
     *
     * @param positions Positions on the view to refresh.
     */
    private void refreshActiveProjects(List<Integer> positions) {
        // Check that we have found active projects to refresh.
        if (positions.isEmpty()) {
            Timber.d("No projects are active, nothing to refresh");
            return;
        }

        performWithView(view -> {
            Timber.d("Refreshing active projects");

            view.refreshPositions(positions);
        });
    }

    /**
     * Setup the subscription for refreshing active projects.
     */
    public void beginRefreshingActiveProjects() {
        // Before we create a new subscription for refreshing active projects
        // we have to unsubscribe to the existing one, if one is available.
        stopRefreshingActiveProjects();

        Timber.d("Subscribe to the refresh of active projects");
        refreshProjectsSubscription = Observable.interval(60, TimeUnit.SECONDS, Schedulers.newThread())
                .map(aLong -> getPositionsForActiveProjects())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onNext(List<Integer> positions) {
                        Timber.d("beginRefreshingActiveProjects onNext");

                        // Push the data to the view.
                        refreshActiveProjects(positions);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("beginRefreshingActiveProjects onError");

                        // Log the error even if the view have been detached.
                        Timber.w(e, "Failed to get positions");
                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("beginRefreshingActiveProjects onCompleted");
                    }
                });
    }

    /**
     * Unsubscribe to the refresh of active projects.
     */
    public void stopRefreshingActiveProjects() {
        unsubscribeIfNotNull(refreshProjectsSubscription);
    }

    /**
     * Refresh active projects.
     */
    public void refreshActiveProjects() {
        Timber.d("Refreshing active projects");
        Observable.defer(() -> Observable.just(getPositionsForActiveProjects()))
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onNext(List<Integer> positions) {
                        Timber.d("refreshActiveProjects onNext");

                        // Push the data to the view.
                        refreshActiveProjects(positions);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("refreshActiveProjects onError");

                        // Log the error even if the view have been detached.
                        Timber.w(e, "Failed to get positions");
                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("refreshActiveProjects onCompleted");
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
                    List<ProjectsItem> items = new ArrayList<>();

                    for (Project project : projects) {
                        List<Time> registeredTime = getRegisteredTime(project);

                        items.add(new ProjectsItem(project, registeredTime));
                    }

                    return items;
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<List<ProjectsItem>>() {
                    @Override
                    public void onNext(List<ProjectsItem> items) {
                        Timber.d("getProjects onNext");

                        performWithView(view -> view.addProjects(items));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("getProjects onError");

                        // Log the error even if the view have been detached.
                        Timber.w(e, "Failed to get projects");

                        performWithView(ProjectsView::showGetProjectsErrorMessage);
                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("getProjects onCompleted");
                    }
                });
    }

    private List<Time> getRegisteredTime(Project project) {
        try {
            int startingPointForTimeSummary = timeSummaryPreferences.getStartingPointForTimeSummary();

            return getProjectTimeSince.execute(project, startingPointForTimeSummary);
        } catch (DomainException e) {
            Timber.w(e, "Unable to get registered time for project");
        }

        return Collections.emptyList();
    }

    /**
     * Delete project.
     *
     * @param project Project to be deleted.
     */
    public void deleteProject(final ProjectsItem project) {
        // Before removing the project we need its current index, it's
        // needed to handle the restoration if deletion fails.
        final Integer index = getFromView(view -> view.getProjects().indexOf(project));
        if (isNull(index)) {
            Timber.w("Unable to get position from view");
            return;
        }

        performWithView(view ->
            // Remove project from the view before executing the use case,
            // i.e. optimistic propagation, to simulate better latency.
            //
            // If the deletion fails the project will be added back to the
            // view again, at the previous location.
            view.deleteProjectAtPosition(index)
        );

        Observable.just(project)
                .flatMap(projectsItem -> {
                    removeProject.execute(projectsItem.asProject());

                    return Observable.empty();
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onNext(Object o) {
                        // Nothing to do, everything have been done...
                        Timber.d("deleteProject onNext");
                    }

                    @Override
                    public void onError(final Throwable e) {
                        Timber.d("deleteProject onError");

                        // Even if the view have been detached we'd want the
                        // error messaged logged.
                        Timber.w(e, "Failed to delete project");

                        performWithView(view -> {
                            view.restoreProjectAtPreviousPosition(index, project);
                            view.showDeleteProjectErrorMessage();
                        });
                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("deleteProject onCompleted");

                        performWithView(ProjectsView::showDeleteProjectSuccessMessage);
                    }
                });
    }

    /**
     * Change the clock activity status for project, i.e. clock in/out.
     *
     * @param projectsItem Project to change clock activity status.
     * @param date         Date and time to use for the clock activity change.
     */
    public void clockActivityChange(final ProjectsItem projectsItem, final Date date) {
        Observable.just(projectsItem.asProject())
                .flatMap(project -> clockActivityChangeViaUseCase(project, date))
                .map(project -> {
                    List<Time> registeredTime = getRegisteredTime(project);

                    return new ProjectsItem(project, registeredTime);
                })
                .compose(RxUtil.applySchedulers())
                .doOnNext(this::postOngoingNotification)
                .subscribe(new Subscriber<ProjectsItem>() {
                    @Override
                    public void onNext(ProjectsItem project) {
                        Timber.d("clockActivityChange onNext");

                        performWithView(view -> view.updateProject(project));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("clockActivityChange onError");

                        // Log the error even if the view have been detached.
                        Timber.w(e, "Failed to change clock activity");

                        performWithView(view -> {
                            if (projectsItem.isActive()) {
                                view.showClockOutErrorMessage();
                                return;
                            }

                            view.showClockInErrorMessage();
                        });
                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("clockActivityChange onCompleted");
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

    private void postOngoingNotification(ProjectsItem projectsItem) {
        Project project = projectsItem.asProject();

        NotificationManager manager = (NotificationManager) getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(
                String.valueOf(project.getId()),
                Worker.NOTIFICATION_ON_GOING_ID
        );

        if (!ongoingNotificationPreferences.isOngoingNotificationEnabled()) {
            return;
        }

        if (project.isActive()) {
            manager.notify(
                    String.valueOf(project.getId()),
                    Worker.NOTIFICATION_ON_GOING_ID,
                    PauseNotification.build(
                            getContext(),
                            project,
                            ongoingNotificationPreferences.isOngoingNotificationChronometerEnabled()
                    )
            );
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(TimeSummaryStartingPointChangeEvent event) {
        performWithView(ProjectsView::reloadProjects);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OngoingNotificationActionEvent event) {
        performWithView(ProjectsView::reloadProjects);
    }
}
