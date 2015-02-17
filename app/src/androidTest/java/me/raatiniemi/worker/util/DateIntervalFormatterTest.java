package me.raatiniemi.worker.util;

import android.app.Application;
import android.test.ApplicationTestCase;

public class DateIntervalFormatterTest extends ApplicationTestCase<Application>
{
    public DateIntervalFormatterTest()
    {
        super(Application.class);
    }

    public void testFormatHoursMinutes1m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(60000);

        assertEquals("0h 1m", value);
    }

    public void testFormatHoursMinutes10m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(600000);

        assertEquals("0h 10m", value);
    }

    public void testFormatHoursMinutes30m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(1800000);

        assertEquals("0h 30m", value);
    }

    public void testFormatHoursMinutes60m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(3600000);

        assertEquals("1h 0m", value);
    }

    public void testFormatHoursMinutes7h30m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(27000000);

        assertEquals("7h 30m", value);
    }

    public void testFormatHoursMinutes30h()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(108000000);

        assertEquals("30h 0m", value);
    }

    public void testFormatHoursMinutes56h25m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(203100000);

        assertEquals("56h 25m", value);
    }

    public void testFormatHoursMinutes59m40s()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(3580000);

        assertEquals("1h 0m", value);
    }

    public void testFormatFractalHours15m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(900000, DateIntervalFormatter.Type.FRACTAL_HOURS);

        assertEquals("0.25", value);
    }

    public void testFormatFractalHours1h()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(3600000, DateIntervalFormatter.Type.FRACTAL_HOURS);

        assertEquals("1.0", value);
    }

    public void testFormatFractalHours1h15m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(4500000, DateIntervalFormatter.Type.FRACTAL_HOURS);

        assertEquals("1.25", value);
    }

    public void testFormatFractalHours1h59m35s()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(7175000, DateIntervalFormatter.Type.FRACTAL_HOURS);

        assertEquals("2.0", value);
    }

    public void testFormatFractalHours30h()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(108000000, DateIntervalFormatter.Type.FRACTAL_HOURS);

        assertEquals("30.0", value);
    }

    public void testFormatFractalHours56h25m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(203100000, DateIntervalFormatter.Type.FRACTAL_HOURS);

        // TODO: Fix issue, should be 56.42 (41,666666667)
        assertEquals("56.42", value);
    }
}
