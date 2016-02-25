package me.raatiniemi.worker.presentation.model.timesheet;

import java.util.Date;

import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.presentation.base.view.adapter.ExpandableListAdapter;

public class TimesheetItem extends ExpandableListAdapter.ExpandableItem<Date, Time> {
    public TimesheetItem(Date group) {
        super(group);
    }
}
