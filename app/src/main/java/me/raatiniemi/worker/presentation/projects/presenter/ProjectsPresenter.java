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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.ClockActivityChange;
import me.raatiniemi.worker.domain.interactor.GetProjectTimeSince;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.presentation.presenter.BasePresenter;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItem;
import me.raatiniemi.worker.presentation.projects.model.ProjectsItemAdapterResult;
import me.raatiniemi.worker.presentation.projects.view.ProjectsView;
import me.raatiniemi.worker.presentation.util.RxUtil;
import me.raatiniemi.worker.presentation.util.TimeSummaryPreferences;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Presenter for the projects module, handles loading of projects.
 */
public class ProjectsPresenter extends BasePresenter<ProjectsView> {
    private final TimeSummaryPreferences timeSummaryPreferences;

    /**
     * Use case for getting registered project time.
     */
    private final GetProjectTimeSince getProjectTimeSince;

    /**
     * Use case for project clock in/out.
     */
    private final ClockActivityChange clockActivityChange;

    /**
     * Constructor.
     *
     * @param timeSummaryPreferences Preferences for the time summary.
     * @param getProjectTimeSince    Use case for getting registered project time.
     * @param clockActivityChange    Use case for project clock in/out.
     */
    public ProjectsPresenter(
            TimeSummaryPreferences timeSummaryPreferences,
            GetProjectTimeSince getProjectTimeSince,
            ClockActivityChange clockActivityChange
    ) {
        this.timeSummaryPreferences = timeSummaryPreferences;
        this.getProjectTimeSince = getProjectTimeSince;
        this.clockActivityChange = clockActivityChange;
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
     * Change the clock activity status for project, i.e. clock in/out.
     *
     * @param result Project with position to change clock activity status.
     * @param date   Date and time to use for the clock activity change.
     */
    public void clockActivityChange(final ProjectsItemAdapterResult result, final Date date) {
        final int position = result.getPosition();
        final ProjectsItem item = result.getProjectsItem();

        Observable.just(item.asProject())
                .flatMap(project -> clockActivityChangeViaUseCase(project, date))
                .map(project -> {
                    List<Time> registeredTime = getRegisteredTime(project);

                    return new ProjectsItem(project, registeredTime);
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<ProjectsItem>() {
                    @Override
                    public void onNext(ProjectsItem project) {
                        Timber.d("clockActivityChange onNext");

                        performWithView(view -> {
                            view.updateNotificationForProject(project);
                            view.updateProject(position, project);
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("clockActivityChange onError");

                        // Log the error even if the view have been detached.
                        Timber.w(e, "Failed to change clock activity");

                        performWithView(view -> {
                            if (item.isActive()) {
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
}
