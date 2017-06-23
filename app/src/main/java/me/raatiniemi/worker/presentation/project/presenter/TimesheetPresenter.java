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

package me.raatiniemi.worker.presentation.project.presenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.GetTimesheet;
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime;
import me.raatiniemi.worker.domain.interactor.RemoveTime;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.model.TimesheetItem;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.presenter.BasePresenter;
import me.raatiniemi.worker.presentation.project.model.TimesheetAdapterResult;
import me.raatiniemi.worker.presentation.project.model.TimesheetGroup;
import me.raatiniemi.worker.presentation.project.view.TimesheetView;
import me.raatiniemi.worker.presentation.util.HideRegisteredTimePreferences;
import me.raatiniemi.worker.presentation.util.RxUtil;
import me.raatiniemi.worker.util.Optional;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

import static me.raatiniemi.worker.presentation.util.RxUtil.unsubscribeIfNotNull;

public class TimesheetPresenter extends BasePresenter<TimesheetView> {
    private Subscription getTimesheetSubscription;

    private final HideRegisteredTimePreferences hideRegisteredTimePreferences;
    private final EventBus eventBus;

    /**
     * Use case for getting project timesheet.
     */
    private final GetTimesheet getTimesheet;

    /**
     * Use case for marking time as registered.
     */
    private final MarkRegisteredTime markRegisteredTime;

    /**
     * Use case for removing time.
     */
    private final RemoveTime removeTime;

    /**
     * Constructor.
     *
     * @param hideRegisteredTimePreferences Preferences for hide registered time.
     * @param eventBus                      Event bus.
     * @param getTimesheet                  Use case for getting project timesheet.
     * @param markRegisteredTime            Use case for marking time as registered.
     * @param removeTime                    Use case for removing time.
     */
    public TimesheetPresenter(
            HideRegisteredTimePreferences hideRegisteredTimePreferences,
            EventBus eventBus,
            GetTimesheet getTimesheet,
            MarkRegisteredTime markRegisteredTime,
            RemoveTime removeTime
    ) {
        this.hideRegisteredTimePreferences = hideRegisteredTimePreferences;
        this.eventBus = eventBus;
        this.getTimesheet = getTimesheet;
        this.markRegisteredTime = markRegisteredTime;
        this.removeTime = removeTime;
    }

    @Override
    public void attachView(TimesheetView view) {
        super.attachView(view);

        eventBus.register(this);
    }

    @Override
    public void detachView() {
        super.detachView();

        eventBus.unregister(this);
        unsubscribeIfNotNull(getTimesheetSubscription);
    }

    public void getTimesheet(final Long id, final int offset) {
        unsubscribeIfNotNull(getTimesheetSubscription);

        // Setup the subscription for retrieving timesheet.
        getTimesheetSubscription = Observable
                .defer(() -> {
                    boolean hideRegisteredTime = hideRegisteredTimePreferences.shouldHideRegisteredTime();
                    return Observable.just(
                            getTimesheet.execute(id, offset, hideRegisteredTime)
                    );
                })
                .map(result -> {
                    List<TimesheetGroup> groups = new ArrayList<>();

                    //noinspection Convert2streamapi
                    for (Map.Entry<Date, SortedSet<TimesheetItem>> date : result.entrySet()) {
                        groups.add(TimesheetGroup.build(date.getKey(), date.getValue()));
                    }

                    return groups;
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(
                        groups -> {
                            Timber.d("getTimesheet onNext");

                            performWithView(view -> view.add(groups));
                        },
                        e -> {
                            Timber.d("getTimesheet onError");

                            // Log the error even if the view have been detached.
                            Timber.w(e, "Failed to get timesheet");
                            performWithView(TimesheetView::showGetTimesheetErrorMessage);
                        },
                        () -> {
                            Timber.d("getTimesheet onCompleted");

                            performWithView(TimesheetView::finishLoading);
                        }
                );
    }

    public void remove(List<TimesheetAdapterResult> results) {
        final int numberOfItems = results.size();

        Observable.just(results)
                .map(items -> {
                    List<Time> timeToRemove = new ArrayList<>();
                    // noinspection Convert2streamapi
                    for (TimesheetAdapterResult result : items) {
                        timeToRemove.add(result.getTime());
                    }

                    removeTime.execute(timeToRemove);
                    return items;
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(
                        items -> {
                            Timber.d("remove onNext");

                            performWithView(view -> view.remove(items));
                        },
                        e -> {
                            Timber.d("remove onError");

                            // Log the error even if the view have been detached.
                            Timber.w(e, "Failed to remove time");
                            performWithView(view -> view.showDeleteErrorMessage(numberOfItems));
                        },
                        () -> Timber.d("remove onCompleted")
                );
    }

    public void register(List<TimesheetAdapterResult> results) {
        final int numberOfItems = results.size();

        // TODO: Refactor to use optimistic propagation.
        Observable.just(results)
                .flatMap(this::registerTimeViaUseCase)
                .compose(RxUtil.applySchedulers())
                .subscribe(
                        items -> {
                            Timber.d("register onNext");

                            performWithView(view -> {
                                if (hideRegisteredTimePreferences.shouldHideRegisteredTime()) {
                                    view.remove(items);
                                    return;
                                }

                                view.update(items);
                            });
                        },
                        e -> {
                            Timber.d("register onError");

                            // Log the error even if the view have been detached.
                            Timber.w(e, "Failed to mark time as registered");
                            performWithView(view -> view.showRegisterErrorMessage(numberOfItems));
                        },
                        () -> Timber.d("register onCompleted")
                );
    }

    private Observable<List<TimesheetAdapterResult>> registerTimeViaUseCase(List<TimesheetAdapterResult> results) {
        List<Time> timeToUpdate = new ArrayList<>();
        // noinspection Convert2streamapi
        for (TimesheetAdapterResult result : results) {
            timeToUpdate.add(result.getTime());
        }

        try {
            List<Time> updates = markRegisteredTime.execute(timeToUpdate);

            return Observable.just(mapUpdatesToPositionOfSelectedItems(updates, results));
        } catch (DomainException e) {
            return Observable.error(e);
        }
    }

    private static List<TimesheetAdapterResult> mapUpdatesToPositionOfSelectedItems(
            List<Time> updates,
            List<TimesheetAdapterResult> selectedItems
    ) {
        List<TimesheetAdapterResult> newResults = new ArrayList<>();

        for (TimesheetAdapterResult selectedItem : selectedItems) {
            Optional<Time> value = findUpdateForSelectedItem(selectedItem, updates);
            if (value.isPresent()) {
                TimesheetItem timesheetItem = new TimesheetItem(value.get());
                newResults.add(TimesheetAdapterResult.build(selectedItem, timesheetItem));
            }
        }

        return newResults;
    }

    private static Optional<Time> findUpdateForSelectedItem(TimesheetAdapterResult selectedItem, List<Time> updates) {
        Time previousTime = selectedItem.getTime();

        for (Time update : updates) {
            if (update.getId().equals(previousTime.getId())) {
                return Optional.of(update);
            }
        }

        return Optional.empty();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OngoingNotificationActionEvent event) {
        performWithView(view -> {
            if (event.getProjectId() != view.getProjectId()) {
                Timber.d("No need to refresh, event is related to another project");
                return;
            }

            view.refresh();
        });
    }
}
