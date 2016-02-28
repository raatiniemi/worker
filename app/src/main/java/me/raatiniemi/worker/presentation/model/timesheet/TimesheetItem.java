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
        String timeSummary = getTimeSummary();

        float difference = calculateTimeDifference(timeSummary);
        return timeSummary + getFormattedTimeDifference(difference);
    }

    private String getTimeSummary() {
        return DateIntervalFormat.format(
                calculateTimeIntervalSummary(),
                DateIntervalFormat.Type.FRACTION_HOURS
        );
    }

    private long calculateTimeIntervalSummary() {
        long interval = 0;

        for (Time time : this) {
            interval += time.getInterval();
        }

        return interval;
    }

    private float calculateTimeDifference(String timeSummary) {
        return Float.valueOf(timeSummary) - 8;
    }

    private String getFormattedTimeDifference(float difference) {
        return String.format(
                getTimeDifferenceFormat(difference),
                difference
        );
    }

    private String getTimeDifferenceFormat(float difference) {
        if (0 == difference) {
            return "";
        }

        if (0 < difference) {
            return " (+%.2f)";
        }

        return " (%.2f)";
    }
}
