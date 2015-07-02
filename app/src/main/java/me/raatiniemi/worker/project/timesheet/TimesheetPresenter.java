package me.raatiniemi.worker.project.timesheet;

import android.content.Context;
import android.util.Log;

import java.util.List;

import me.raatiniemi.worker.base.presenter.RxPresenter;
import me.raatiniemi.worker.model.project.ProjectProvider;
import me.raatiniemi.worker.model.time.Time;
import me.raatiniemi.worker.project.timesheet.TimesheetAdapter.TimeInAdapterResult;
import me.raatiniemi.worker.util.TimesheetExpandableDataProvider.Groupable;
import rx.functions.Action1;

public class TimesheetPresenter extends RxPresenter<TimesheetFragment> {
    /**
     * Tag used when logging.
     */
    private static final String TAG = "TimesheetPresenter";

    private ProjectProvider mProvider;

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
            .compose(this.<List<Groupable>>applySchedulers())
            .subscribe(new Action1<List<Groupable>>() {
                @Override
                public void call(List<Groupable> groupables) {
                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip pushing timesheet");
                        return;
                    }

                    // Push the data to the view.
                    // TODO: Differentiate between set and add?
                    getView().addData(groupables);
                }
            });
    }

    public void remove(final TimeInAdapterResult result) {
        mProvider.remove(result.getTime())
            .compose(this.<Time>applySchedulers())
            .subscribe(new Action1<Time>() {
                @Override
                public void call(Time time) {
                    getView().remove(result);
                }
            });
    }
}
