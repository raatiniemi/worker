package me.raatiniemi.worker.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import me.raatiniemi.worker.BuildConfig;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DateIntervalFormatTest {
    @Test
    public void testFormatHoursMinutes1m() {
        String value = DateIntervalFormat.format(60000L);

        assertEquals("1m", value);
    }

    @Test
    public void testFormatHoursMinutes10m() {
        String value = DateIntervalFormat.format(600000L);

        assertEquals("10m", value);
    }

    @Test
    public void testFormatHoursMinutes30m() {
        String value = DateIntervalFormat.format(1800000L);

        assertEquals("30m", value);
    }

    @Test
    public void testFormatHoursMinutes60m() {
        String value = DateIntervalFormat.format(3600000L);

        assertEquals("1h 0m", value);
    }

    @Test
    public void testFormatHoursMinutes7h30m() {
        String value = DateIntervalFormat.format(27000000L);

        assertEquals("7h 30m", value);
    }

    @Test
    public void testFormatHoursMinutes30h() {
        String value = DateIntervalFormat.format(108000000L);

        assertEquals("30h 0m", value);
    }

    @Test
    public void testFormatHoursMinutes56h25m() {
        String value = DateIntervalFormat.format(203100000L);

        assertEquals("56h 25m", value);
    }

    @Test
    public void testFormatHoursMinutes59m40s() {
        String value = DateIntervalFormat.format(3580000L);

        assertEquals("1h 0m", value);
    }

    @Test
    public void testFormatFractionHours15m() {
        String value = DateIntervalFormat.format(
            900000L,
            DateIntervalFormat.Type.FRACTION_HOURS
        );

        assertEquals("0.25", value);
    }

    @Test
    public void testFormatFractionHours1h() {
        String value = DateIntervalFormat.format(
            3600000L,
            DateIntervalFormat.Type.FRACTION_HOURS
        );

        assertEquals("1.00", value);
    }

    @Test
    public void testFormatFractionHours1h15m() {
        String value = DateIntervalFormat.format(
            4500000L,
            DateIntervalFormat.Type.FRACTION_HOURS
        );

        assertEquals("1.25", value);
    }

    @Test
    public void testFormatFractionHours1h59m35s() {
        String value = DateIntervalFormat.format(
            7175000L,
            DateIntervalFormat.Type.FRACTION_HOURS
        );

        assertEquals("2.00", value);
    }

    @Test
    public void testFormatFractionHours30h() {
        String value = DateIntervalFormat.format(
            108000000L,
            DateIntervalFormat.Type.FRACTION_HOURS
        );

        assertEquals("30.00", value);
    }

    @Test
    public void testFormatFractionHours56h25m() {
        String value = DateIntervalFormat.format(
            203100000L,
            DateIntervalFormat.Type.FRACTION_HOURS
        );

        assertEquals("56.42", value);
    }
}
