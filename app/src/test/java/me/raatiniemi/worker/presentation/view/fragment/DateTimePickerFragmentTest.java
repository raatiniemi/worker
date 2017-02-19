package me.raatiniemi.worker.presentation.view.fragment;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import me.raatiniemi.worker.RobolectricTestCase;

public class DateTimePickerFragmentTest extends RobolectricTestCase {
    private DateTimePickerFragment fragment;
    private Calendar today;
    private Calendar nextYear;

    @Before
    public void setUp() throws Exception {
        fragment = new DateTimePickerFragment();

        today = Calendar.getInstance();
        nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
    }

    @Test
    public void setMinDate_withValidDate() {
        fragment.setMinDate(today);
    }

    @Test(expected = RuntimeException.class)
    public void setMinDate_afterMaxDate() {
        fragment.setMaxDate(today);
        fragment.setMinDate(nextYear);
    }

    @Test
    public void setMaxDate_withValidDate() {
        fragment.setMaxDate(nextYear);
    }

    @Test(expected = RuntimeException.class)
    public void setMaxDate_beforeMinDate() {
        fragment.setMinDate(nextYear);
        fragment.setMaxDate(today);
    }
}
