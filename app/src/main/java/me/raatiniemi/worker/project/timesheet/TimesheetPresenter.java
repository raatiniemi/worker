package me.raatiniemi.worker.project.timesheet;

import android.content.Context;
import android.util.Log;

import java.util.List;

import me.raatiniemi.worker.base.presenter.RxPresenter;
import me.raatiniemi.worker.model.domain.project.ProjectProvider;
import me.raatiniemi.worker.model.domain.time.Time;
import me.raatiniemi.worker.project.timesheet.TimesheetAdapter.TimeInAdapterResult;
import me.raatiniemi.worker.project.timesheet.TimesheetAdapter.TimesheetItem;
import rx.Subscriber;
import rx.functions.Action1;

public class TimesheetPresenter extends RxPresenter<TimesheetFragment> {
    /**
     * Tag used when logging.
     */
    private static final String TAG = "TimesheetPresenter";

    /**
     * Provider for working with projects.
     */
    private ProjectProvider mProvider;

    /**
     * Constructor.
     *
     * @param context Context used with the presenter.
     * @param provider Provider for working with projects.
     */
    public TimesheetPresenter(Context context, ProjectProvider provider) {
        super(context);

        mProvider = provider;
    }

    public void getTimesheet(final Long id, final int offset) {
        // Before we setup the timesheet subscription we have to cancel
        // the previous one, if available.
        unsubscribe();

        // Setup the subscription for retrieving timesheet.
        mSubscription = mProvider.getTimesheet(id, offset)
            .compose(this.<List<TimesheetItem>>applySchedulers())
            .subscribe(new Subscriber<List<TimesheetItem>>() {
                @Override
                public void onNext(List<TimesheetItem> items) {
                    Log.d(TAG, "getTimesheet onNext");

                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip pushing timesheet");
                        return;
                    }

                    // Depending on the offset we have to set the initial data
                    // or add data to the existing collection.
                    if (0 == offset) {
                        // Push the data to the view.
                        getView().setData(items);
                        return;
                    }

                    // Push the data to the view.
                    getView().addData(items);
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
                }
            });
    }

    public void remove(final TimeInAdapterResult result) {
        mProvider.remove(result.getTime())
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
}
