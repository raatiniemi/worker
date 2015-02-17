package me.raatiniemi.worker.util;

/**
 * Format time interval.
 */
public class DateIntervalFormatter
{
    /**
     * Available format types.
     */
    public static enum Type
    {
        /**
         * Format the interval with hours and minutes, e.g. "5h 12m".
         */
        HOURS_MINUTES
    }

    /**
     * Format the interval with given format type.
     * @param interval Interval to format in milliseconds.
     * @param type Type of format to use.
     * @return Interval formatted with given type.
     */
    public String format(long interval, Type type)
    {
        String format;

        // Determined what kind of format type to use.
        switch (type) {
            case HOURS_MINUTES:
            default:
                format = format(interval);
        }

        return format;
    }

    /**
     * Format the interval with hours and minutes.
     * @param interval Interval to format in milliseconds.
     * @return Interval formatted with hours and minutes.
     */
    public String format(long interval)
    {
        // Convert milliseconds to seconds.
        interval = interval / 1000;

        // Calculate the number of hours and minutes based
        // on the total number of seconds.
        long hours = (interval / (60 * 60) % 24);
        long minutes = (interval / 60 % 60);

        // Check if the interval has passed 24 hours.
        long days = (interval / (60 * 60 * 24));
        if (days > 0) {
            hours += (days * 24);
        }

        // If the number of seconds is at >= 30 we should add an extra minute
        // to the minutes, i.e. round up the minutes if they have passed 50%.
        //
        // Otherwise, total time of 49 seconds will still display 0m and not 1m.
        long seconds = (interval % 60);
        if (seconds >= 30) {
            minutes++;
        }

        // If the minutes reaches 60, we have to reset
        // the minutes and increment the hours.
        if (minutes == 60) {
            minutes = 0;
            hours++;
        }

        return String.format("%dh %dm", hours, minutes);
    }
}
