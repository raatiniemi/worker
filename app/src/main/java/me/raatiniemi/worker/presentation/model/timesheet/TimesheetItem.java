package me.raatiniemi.worker.presentation.model.timesheet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.raatiniemi.worker.domain.model.CalculatedTime;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.util.CalculateTime;
import me.raatiniemi.worker.presentation.base.view.adapter.ExpandableListAdapter;
import me.raatiniemi.worker.util.DateIntervalFormat;

public class TimesheetItem extends ExpandableListAdapter.ExpandableItem<Date, Time> {
    private static final SimpleDateFormat sDateFormat;

    static {
        sDateFormat = new SimpleDateFormat("EEEE (MMMM d)", Locale.getDefault());
    }

    public TimesheetItem(Date group) {
        super(group);
    }

    public String getTitle() {
        return sDateFormat.format(getGroup());
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
            CalculatedTime calculatedTime = CalculateTime.calculateTime(time.getInterval());
            interval += calculatedTime.asMilliseconds();
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
