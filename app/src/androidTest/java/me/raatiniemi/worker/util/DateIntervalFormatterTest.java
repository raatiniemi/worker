package me.raatiniemi.worker.util;

import android.app.Application;
import android.test.ApplicationTestCase;

public class DateIntervalFormatterTest extends ApplicationTestCase<Application>
{
    public DateIntervalFormatterTest()
    {
        super(Application.class);
    }

    public void testFormat1m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(60000);

        assertEquals("0h 1m", value);
    }

    public void testFormat10m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(600000);

        assertEquals("0h 10m", value);
    }

    public void testFormat30m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(1800000);

        assertEquals("0h 30m", value);
    }

    public void testFormat60m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(3600000);

        assertEquals("1h 0m", value);
    }

    public void testFormat7h30m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(27000000);

        assertEquals("7h 30m", value);
    }

    public void testFormat30h()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(108000000);

        assertEquals("30h 0m", value);
    }

    public void testFormat56h25m()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(203100000);

        assertEquals("56h 25m", value);
    }

    public void testFormat59m40s()
    {
        DateIntervalFormatter dateInterval = new DateIntervalFormatter();
        String value = dateInterval.format(3580000);

        assertEquals("1h 0m", value);
    }
}
