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

package me.raatiniemi.worker.presentation.project.presenter;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.GetTimesheet;
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime;
import me.raatiniemi.worker.domain.interactor.RemoveTime;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.presenter.BasePresenter;
import me.raatiniemi.worker.presentation.project.model.TimeInAdapterResult;
import me.raatiniemi.worker.presentation.project.model.TimesheetChildModel;
import me.raatiniemi.worker.presentation.project.model.TimesheetGroupModel;
import me.raatiniemi.worker.presentation.project.view.TimesheetView;
import me.raatiniemi.worker.presentation.util.RxUtil;
import me.raatiniemi.worker.presentation.util.Settings;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

import static me.raatiniemi.worker.presentation.util.RxUtil.unsubscribeIfNotNull;

public class TimesheetPresenter extends BasePresenter<TimesheetView> {
    /**
     * Tag used when logging.
     */
    private static final String TAG = "TimesheetPresenter";

    private Subscription getTimesheetSubscription;

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
     * @param context            Context used with the presenter.
     * @param eventBus           Event bus.
     * @param getTimesheet       Use case for getting project timesheet.
     * @param markRegisteredTime Use case for marking time as registered.
     * @param removeTime         Use case for removing time.
     */
    public TimesheetPresenter(
            Context context,
            EventBus eventBus,
            GetTimesheet getTimesheet,
            MarkRegisteredTime markRegisteredTime,
            RemoveTime removeTime
    ) {
        super(context);

        this.eventBus = eventBus;
        this.getTimesheet = getTimesheet;
        this.markRegisteredTime = markRegisteredTime;
        this.removeTime = removeTime;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void attachView(TimesheetView view) {
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
        unsubscribeIfNotNull(getTimesheetSubscription);
    }

    public void getTimesheet(final Long id, final int offset) {
        unsubscribeIfNotNull(getTimesheetSubscription);

        // Setup the subscription for retrieving timesheet.
        getTimesheetSubscription = Observable
                .defer(() -> {
                    boolean hideRegisteredTime = Settings.shouldHideRegisteredTime(getContext());
                    return Observable.just(
                            getTimesheet.execute(id, offset, hideRegisteredTime)
                    );
                })
                .map(result -> {
                    List<TimesheetGroupModel> items = new ArrayList<>();
                    for (Map.Entry<Date, List<Time>> date : result.entrySet()) {
                        TimesheetGroupModel item = new TimesheetGroupModel(date.getKey());
                        for (Time time : date.getValue()) {
                            item.add(new TimesheetChildModel(time));
                        }

                        items.add(item);
                    }
                    return items;
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<List<TimesheetGroupModel>>() {
                    @Override
                    public void onNext(List<TimesheetGroupModel> items) {
                        Log.d(TAG, "getTimesheet onNext");

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing item");
                            return;
                        }

                        // Push the data to the view.
                        getView().add(items);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "getTimesheet onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to get timesheet", e);

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        getView().showGetTimesheetErrorMessage();
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "getTimesheet onCompleted");

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing finish");
                            return;
                        }

                        // Available data have been pushed.
                        getView().finishLoading();
                    }
                });
    }

    public void remove(List<TimeInAdapterResult> results) {
        final int numberOfItems = results.size();

        Observable.just(results)
                .map(items -> {
                    List<Time> timeToRemove = new ArrayList<>();
                    // noinspection Convert2streamapi
                    for (TimeInAdapterResult result : items) {
                        timeToRemove.add(result.getTime());
                    }

                    removeTime.execute(timeToRemove);
                    return items;
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<List<TimeInAdapterResult>>() {
                    @Override
                    public void onNext(List<TimeInAdapterResult> results) {
                        Log.d(TAG, "remove onNext");

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing time deletion");
                            return;
                        }

                        // Attempt to remove the result from view.
                        getView().remove(results);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "remove onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to remove time", e);

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        getView().showDeleteErrorMessage(numberOfItems);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "remove onCompleted");
                    }
                });
    }

    public void register(List<TimeInAdapterResult> results) {
        final int numberOfItems = results.size();

        // TODO: Refactor to use optimistic propagation.
        Observable.just(results)
                .flatMap(this::registerTimeViaUseCase)
                .compose(RxUtil.applySchedulers())
                .subscribe(new Subscriber<List<TimeInAdapterResult>>() {
                    @Override
                    public void onNext(List<TimeInAdapterResult> results) {
                        Log.d(TAG, "register onNext");

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing time update");
                            return;
                        }

                        // If we should hide registered time, we should remove
                        // the item rather than updating it.
                        if (Settings.shouldHideRegisteredTime(getContext())) {
                            getView().remove(results);
                            return;
                        }

                        // Update the time item within the adapter result and send
                        // it to the view for update.
                        getView().update(results);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "register onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to mark time as registered", e);

                        // Check that we still have the view attached.
                        if (isViewDetached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        getView().showRegisterErrorMessage(numberOfItems);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "register onCompleted");
                    }
                });
    }

    private Observable<List<TimeInAdapterResult>> registerTimeViaUseCase(List<TimeInAdapterResult> results) {
        List<Time> timeToUpdate = new ArrayList<>();
        // noinspection Convert2streamapi
        for (TimeInAdapterResult result : results) {
            timeToUpdate.add(result.getTime());
        }

        try {
            List<Time> updatedTime = markRegisteredTime.execute(timeToUpdate);

            List<TimeInAdapterResult> newResults = new ArrayList<>();
            for (TimeInAdapterResult result : results) {
                Time previousTime = result.getTime();

                for (Time time : updatedTime) {
                    if (time.getId().equals(previousTime.getId())) {
                        newResults.add(TimeInAdapterResult.build(result, time));
                        break;
                    }
                }
            }

            return Observable.just(newResults);
        } catch (DomainException e) {
            return Observable.error(e);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OngoingNotificationActionEvent event) {
        if (isViewDetached()) {
            Log.d(TAG, "View is not attached, skip reloading timesheet");
            return;
        }

        if (event.getProjectId() != getView().getProjectId()) {
            Log.d(TAG, "No need to refresh, event is related to another project");
            return;
        }

        getView().refresh();
    }
}
