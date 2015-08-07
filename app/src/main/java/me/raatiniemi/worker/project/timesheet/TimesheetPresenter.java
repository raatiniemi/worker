package me.raatiniemi.worker.project.timesheet;

import android.content.Context;
import android.util.Log;

import java.util.List;

import me.raatiniemi.worker.base.presenter.RxPresenter;
import me.raatiniemi.worker.model.domain.project.ProjectProvider;
import me.raatiniemi.worker.model.time.Time;
import me.raatiniemi.worker.project.timesheet.TimesheetAdapter.TimeInAdapterResult;
import me.raatiniemi.worker.project.timesheet.TimesheetAdapter.TimesheetItem;
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
            .subscribe(new Action1<List<TimesheetItem>>() {
                @Override
                public void call(List<TimesheetItem> items) {
                    // Check that we still have the view attached.
                    if (!isViewAttached()) {
                        Log.d(TAG, "View is not attached, skip pushing timesheet");
                        return;
                    }

                    if (0 == offset) {
                        getView().setData(items);
                        return;
                    }
                    getView().addData(items);
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
