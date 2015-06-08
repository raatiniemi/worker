package me.raatiniemi.worker.util;

import android.app.Application;
import android.test.ApplicationTestCase;

public class DateIntervalFormatTest extends ApplicationTestCase<Application> {
    public DateIntervalFormatTest() {
        super(Application.class);
    }

    public void testFormatHoursMinutes1m() {
        String value = DateIntervalFormat.format(60000);

        assertEquals("1m", value);
    }

    public void testFormatHoursMinutes10m() {
        String value = DateIntervalFormat.format(600000);

        assertEquals("10m", value);
    }

    public void testFormatHoursMinutes30m() {
        String value = DateIntervalFormat.format(1800000);

        assertEquals("30m", value);
    }

    public void testFormatHoursMinutes60m() {
        String value = DateIntervalFormat.format(3600000);

        assertEquals("1h 0m", value);
    }

    public void testFormatHoursMinutes7h30m() {
        String value = DateIntervalFormat.format(27000000);

        assertEquals("7h 30m", value);
    }

    public void testFormatHoursMinutes30h() {
        String value = DateIntervalFormat.format(108000000);

        assertEquals("30h 0m", value);
    }

    public void testFormatHoursMinutes56h25m() {
        String value = DateIntervalFormat.format(203100000);

        assertEquals("56h 25m", value);
    }

    public void testFormatHoursMinutes59m40s() {
        String value = DateIntervalFormat.format(3580000);

        assertEquals("1h 0m", value);
    }

    public void testFormatFractionHours15m() {
        String value = DateIntervalFormat.format(900000, DateIntervalFormat.Type.FRACTION_HOURS);

        assertEquals("0.25", value);
    }

    public void testFormatFractionHours1h() {
        String value = DateIntervalFormat.format(3600000, DateIntervalFormat.Type.FRACTION_HOURS);

        assertEquals("1.00", value);
    }

    public void testFormatFractionHours1h15m() {
        String value = DateIntervalFormat.format(4500000, DateIntervalFormat.Type.FRACTION_HOURS);

        assertEquals("1.25", value);
    }

    public void testFormatFractionHours1h59m35s() {
        String value = DateIntervalFormat.format(7175000, DateIntervalFormat.Type.FRACTION_HOURS);

        assertEquals("2.00", value);
    }

    public void testFormatFractionHours30h() {
        String value = DateIntervalFormat.format(108000000, DateIntervalFormat.Type.FRACTION_HOURS);

        assertEquals("30.00", value);
    }

    public void testFormatFractionHours56h25m() {
        String value = DateIntervalFormat.format(203100000, DateIntervalFormat.Type.FRACTION_HOURS);

        assertEquals("56.42", value);
    }
}
