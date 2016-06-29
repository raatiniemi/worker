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

package me.raatiniemi.worker.presentation.presenter;

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
import me.raatiniemi.worker.presentation.base.presenter.RxPresenter;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.model.timesheet.TimeInAdapterResult;
import me.raatiniemi.worker.presentation.model.timesheet.TimesheetChildModel;
import me.raatiniemi.worker.presentation.model.timesheet.TimesheetGroupModel;
import me.raatiniemi.worker.presentation.util.Settings;
import me.raatiniemi.worker.presentation.view.fragment.TimesheetFragment;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import rx.functions.Func1;

public class TimesheetPresenter extends RxPresenter<TimesheetFragment> {
    /**
     * Tag used when logging.
     */
    private static final String TAG = "TimesheetPresenter";

    private final EventBus mEventBus;

    private final long mProjectId;

    /**
     * Use case for getting project timesheet.
     */
    private final GetTimesheet mGetTimesheet;

    /**
     * Use case for marking time as registered.
     */
    private final MarkRegisteredTime mMarkRegisteredTime;

    /**
     * Use case for removing time.
     */
    private final RemoveTime mRemoveTime;

    /**
     * Constructor.
     *
     * @param context            Context used with the presenter.
     * @param eventBus           Event bus.
     * @param projectId          Id for the project.
     * @param getTimesheet       Use case for getting project timesheet.
     * @param markRegisteredTime Use case for marking time as registered.
     * @param removeTime         Use case for removing time.
     */
    public TimesheetPresenter(
            Context context,
            EventBus eventBus,
            long projectId,
            GetTimesheet getTimesheet,
            MarkRegisteredTime markRegisteredTime,
            RemoveTime removeTime
    ) {
        super(context);

        mEventBus = eventBus;
        mProjectId = projectId;
        mGetTimesheet = getTimesheet;
        mMarkRegisteredTime = markRegisteredTime;
        mRemoveTime = removeTime;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void attachView(TimesheetFragment view) {
        super.attachView(view);

        mEventBus.register(this);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void detachView() {
        super.detachView();

        mEventBus.unregister(this);
    }

    public void getTimesheet(final Long id, final int offset) {
        // Before we setup the timesheet subscription we have to cancel
        // the previous one, if available.
        unsubscribe();

        // Setup the subscription for retrieving timesheet.
        mSubscription = Observable
                .defer(new Func0<Observable<Map<Date, List<Time>>>>() {
                    @Override
                    public Observable<Map<Date, List<Time>>> call() {
                        boolean hideRegisteredTime = Settings.shouldHideRegisteredTime(getContext());
                        return Observable.just(
                                mGetTimesheet.execute(id, offset, hideRegisteredTime)
                        );
                    }
                })
                .map(new Func1<Map<Date, List<Time>>, List<TimesheetGroupModel>>() {
                    @Override
                    public List<TimesheetGroupModel> call(Map<Date, List<Time>> result) {
                        List<TimesheetGroupModel> items = new ArrayList<>();
                        for (Map.Entry<Date, List<Time>> date : result.entrySet()) {
                            TimesheetGroupModel item = new TimesheetGroupModel(date.getKey());
                            for (Time time : date.getValue()) {
                                item.add(new TimesheetChildModel(time));
                            }

                            items.add(item);
                        }
                        return items;
                    }
                })
                .compose(this.<List<TimesheetGroupModel>>applySchedulers())
                .subscribe(new Subscriber<List<TimesheetGroupModel>>() {
                    @Override
                    public void onNext(List<TimesheetGroupModel> items) {
                        Log.d(TAG, "getTimesheet onNext");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
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
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        // Push the error to the view.
                        getView().showError(e);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "getTimesheet onCompleted");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing finish");
                            return;
                        }

                        // Available data have been pushed.
                        getView().finishLoading();
                    }
                });
    }

    public void remove(List<TimeInAdapterResult> results) {
        Observable.just(results)
                .map(new Func1<List<TimeInAdapterResult>, List<TimeInAdapterResult>>() {
                    @Override
                    public List<TimeInAdapterResult> call(List<TimeInAdapterResult> results) {
                        List<Time> timeToRemove = new ArrayList<>();
                        for (TimeInAdapterResult result : results) {
                            timeToRemove.add(result.getTime());
                        }

                        mRemoveTime.execute(timeToRemove);
                        return results;
                    }
                })
                .compose(this.<List<TimeInAdapterResult>>applySchedulers())
                .subscribe(new Subscriber<List<TimeInAdapterResult>>() {
                    @Override
                    public void onNext(List<TimeInAdapterResult> results) {
                        Log.d(TAG, "remove onNext");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
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
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        // Push the error to the view.
                        getView().showError(e);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "remove onCompleted");
                    }
                });
    }

    public void register(List<TimeInAdapterResult> results) {
        // TODO: Refactor to use optimistic propagation.
        Observable.just(results)
                .flatMap(new Func1<List<TimeInAdapterResult>, Observable<List<TimeInAdapterResult>>>() {
                    @Override
                    public Observable<List<TimeInAdapterResult>> call(List<TimeInAdapterResult> results) {
                        List<Time> timeToUpdate = new ArrayList<>();
                        for (TimeInAdapterResult result : results) {
                            timeToUpdate.add(result.getTime());
                        }

                        try {
                            List<Time> updatedTime = mMarkRegisteredTime.execute(timeToUpdate);

                            List<TimeInAdapterResult> newResults = new ArrayList<>();
                            for (TimeInAdapterResult result : results) {
                                Time previousTime = result.getTime();

                                for (Time time : updatedTime) {
                                    if (time.getId().equals(previousTime.getId())) {
                                        newResults.add(
                                                TimeInAdapterResult.build(result, time)
                                        );
                                        break;
                                    }
                                }
                            }

                            return Observable.just(newResults);
                        } catch (DomainException e) {
                            return Observable.error(e);
                        }
                    }
                })
                .compose(this.<List<TimeInAdapterResult>>applySchedulers())
                .subscribe(new Subscriber<List<TimeInAdapterResult>>() {
                    @Override
                    public void onNext(List<TimeInAdapterResult> results) {
                        Log.d(TAG, "register onNext");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
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
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing error");
                            return;
                        }

                        // Push the error to the view.
                        getView().showError(e);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "register onCompleted");
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OngoingNotificationActionEvent event) {
        if (!isViewAttached()) {
            Log.d(TAG, "View is not attached, skip reloading timesheet");
            return;
        }

        if (event.getProjectId() != mProjectId) {
            Log.d(TAG, "No need to refresh, event is related to another project");
            return;
        }

        getView().refresh();
    }
}
