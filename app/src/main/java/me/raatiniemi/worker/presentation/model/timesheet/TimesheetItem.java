package me.raatiniemi.worker.presentation.model.timesheet;

import java.util.Date;

import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.presentation.base.view.adapter.ExpandableListAdapter;
import me.raatiniemi.worker.util.DateIntervalFormat;

public class TimesheetItem extends ExpandableListAdapter.ExpandableItem<Date, Time> {
    public TimesheetItem(Date group) {
        super(group);
    }

    public boolean isRegistered() {
        boolean registered = true;

        for (Time time : this) {
            if (time.isRegistered()) {
                continue;
            }

            registered = false;
            break;
        }

        return registered;
    }

    public String getTimeSummaryWithDifference() {
        long interval = 0;

        for (Time time : this) {
            interval += time.getInterval();
        }

        String summarize = DateIntervalFormat.format(
                interval,
                DateIntervalFormat.Type.FRACTION_HOURS
        );

        Float difference = Float.valueOf(summarize) - 8;
        if (difference != 0) {
            String format = difference > 0 ? " (+%.2f)" : " (%.2f)";
            summarize += String.format(format, difference);
        }

        return summarize;
    }
}
