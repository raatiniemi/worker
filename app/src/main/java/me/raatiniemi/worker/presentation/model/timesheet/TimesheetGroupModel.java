package me.raatiniemi.worker.presentation.model.timesheet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.raatiniemi.worker.presentation.base.view.adapter.ExpandableListAdapter;
import me.raatiniemi.worker.presentation.util.DateIntervalFormat;
import me.raatiniemi.worker.presentation.util.FractionIntervalFormat;

public class TimesheetGroupModel
        extends ExpandableListAdapter.ExpandableItem<Date, TimesheetChildModel> {
    private static final DateIntervalFormat sIntervalFormat;

    static {
        sIntervalFormat = new FractionIntervalFormat();
    }

    private final SimpleDateFormat sDateFormat = new SimpleDateFormat("EEEE (MMMM d)", Locale.getDefault());

    public TimesheetGroupModel(Date group) {
        super(group);
    }

    public long getId() {
        return getGroup().getTime();
    }

    public String getTitle() {
        return sDateFormat.format(getGroup());
    }

    public boolean isRegistered() {
        boolean registered = true;

        for (TimesheetChildModel child : this) {
            if (child.isRegistered()) {
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
        return String.format("%.2f", calculateTimeIntervalSummary());
    }

    private float calculateTimeIntervalSummary() {
        float interval = 0;

        for (TimesheetChildModel child : this) {
            interval += calculateFractionFromMilliseconds(
                    child.calculateIntervalInMilliseconds()
            );
        }

        return interval;
    }

    private float calculateFractionFromMilliseconds(long intervalInMilliseconds) {
        String fraction = sIntervalFormat.format(intervalInMilliseconds);

        return Float.valueOf(fraction);
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
