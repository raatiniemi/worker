package me.raatiniemi.worker.util;

public class DateIntervalFormatter
{
    public String format(long interval)
    {
        // Convert milliseconds to seconds.
        interval = interval / 1000;

        // Calculate the number of hours and minutes based
        // on the total number of seconds.
        long hours = (interval / (60 * 60) % 24);
        long minutes = (interval / 60 % 60);

        // TODO: If hours have passed 24, then the time "resets".

        // If the number of seconds is at >= 30 we should add an extra minute
        // to the minutes, i.e. round up the minutes if they have passed 50%.
        //
        // Otherwise, total time of 49 seconds will still display 0m and not 1m.
        long seconds = (interval % 60);
        if (seconds >= 30) {
            minutes++;
        }

        return String.format("%dh %dm", hours, minutes);
    }
}
