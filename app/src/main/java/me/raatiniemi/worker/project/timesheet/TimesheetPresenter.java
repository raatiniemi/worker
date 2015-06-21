package me.raatiniemi.worker.project.timesheet;

import android.content.Context;

import me.raatiniemi.worker.base.presenter.RxPresenter;
import me.raatiniemi.worker.model.project.ProjectProvider;

public class TimesheetPresenter extends RxPresenter<TimesheetFragment> {
    private ProjectProvider mProvider;

    public TimesheetPresenter(Context context, ProjectProvider provider) {
        super(context);

        mProvider = provider;
    }
}
