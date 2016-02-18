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

import java.util.List;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.GetTimesheet;
import me.raatiniemi.worker.domain.interactor.MarkRegisteredTime;
import me.raatiniemi.worker.domain.interactor.RemoveTime;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.presentation.base.presenter.RxPresenter;
import me.raatiniemi.worker.presentation.view.adapter.TimesheetAdapter.TimeInAdapterResult;
import me.raatiniemi.worker.presentation.view.adapter.TimesheetAdapter.TimesheetItem;
import me.raatiniemi.worker.presentation.view.fragment.TimesheetFragment;
import me.raatiniemi.worker.util.Settings;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import rx.functions.Func1;

public class TimesheetPresenter extends RxPresenter<TimesheetFragment> {
    /**
     * Tag used when logging.
     */
    private static final String TAG = "TimesheetPresenter";

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
     * @param getTimesheet       Use case for getting project timesheet.
     * @param markRegisteredTime Use case for marking time as registered.
     * @param removeTime         Use case for removing time.
     */
    public TimesheetPresenter(
            Context context,
            GetTimesheet getTimesheet,
            MarkRegisteredTime markRegisteredTime,
            RemoveTime removeTime
    ) {
        super(context);

        mGetTimesheet = getTimesheet;
        mMarkRegisteredTime = markRegisteredTime;
        mRemoveTime = removeTime;
    }

    public void getTimesheet(final Long id, final int offset) {
        // Before we setup the timesheet subscription we have to cancel
        // the previous one, if available.
        unsubscribe();

        // Setup the subscription for retrieving timesheet.
        mSubscription = Observable.defer(new Func0<Observable<List<TimesheetItem>>>() {
            @Override
            public Observable<List<TimesheetItem>> call() {
                boolean hideRegisteredTime = Settings.shouldHideRegisteredTime(getContext());
                return Observable.just(
                        mGetTimesheet.execute(id, offset, hideRegisteredTime)
                );
            }
        })
                .flatMapIterable(new Func1<List<TimesheetItem>, Iterable<TimesheetItem>>() {
                    @Override
                    public Iterable<TimesheetItem> call(List<TimesheetItem> items) {
                        return items;
                    }
                })
                .compose(this.<TimesheetItem>applySchedulers())
                .subscribe(new Subscriber<TimesheetItem>() {
                    @Override
                    public void onNext(TimesheetItem item) {
                        Log.d(TAG, "getTimesheet onNext");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing item");
                            return;
                        }

                        // Push the data to the view.
                        getView().add(item);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "getTimesheet onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to get timesheet: " + e.getMessage());

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

    public void remove(final TimeInAdapterResult result) {
        Observable.just(result.getTime())
                .map(new Func1<Time, Time>() {
                    @Override
                    public Time call(final Time time) {
                        mRemoveTime.execute(time);

                        // To avoid breaking the API from the ProjectProvider,
                        // we should still return the time.
                        //
                        // However, this should be refactored to use optimistic
                        // propagation, i.e. like removing projects.
                        return time;
                    }
                })
                .compose(this.<Time>applySchedulers())
                .subscribe(new Subscriber<Time>() {
                    @Override
                    public void onNext(Time time) {
                        Log.d(TAG, "remove onNext");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing time deletion");
                            return;
                        }

                        // Attempt to remove the result from view.
                        getView().remove(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "remove onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to remove time: " + e.getMessage());

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

    public void register(final TimeInAdapterResult result) {
        // TODO: Refactor to use optimistic propagation.
        Observable.just(result.getTime())
                .flatMap(new Func1<Time, Observable<Time>>() {
                    @Override
                    public Observable<Time> call(final Time time) {
                        try {
                            return Observable.just(
                                    mMarkRegisteredTime.execute(time)
                            );
                        } catch (DomainException e) {
                            return Observable.error(e);
                        }
                    }
                })
                .compose(this.<Time>applySchedulers())
                .subscribe(new Subscriber<Time>() {
                    @Override
                    public void onNext(Time time) {
                        Log.d(TAG, "register onNext");

                        // Check that we still have the view attached.
                        if (!isViewAttached()) {
                            Log.d(TAG, "View is not attached, skip pushing time update");
                            return;
                        }

                        // If we should hide registered time, we should remove
                        // the item rather than updating it.
                        if (Settings.shouldHideRegisteredTime(getContext())) {
                            getView().remove(result);
                            return;
                        }

                        // Update the time item within the adapter result and send
                        // it to the view for update.
                        result.setTime(time);
                        getView().update(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "register onError");

                        // Log the error even if the view have been detached.
                        Log.w(TAG, "Failed to mark time as registered: " + e.getMessage());

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
}
